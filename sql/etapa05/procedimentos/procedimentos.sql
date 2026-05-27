USE lava_jato;

-- =========================================================
-- ETAPA 05 - PROCEDIMENTOS
-- Execute depois da tabela de log e das funcoes da etapa 05.
-- =========================================================

DROP PROCEDURE IF EXISTS sp_atualizar_status_atendimento;
DROP PROCEDURE IF EXISTS sp_recalcular_pagamento_atendimento;
DROP PROCEDURE IF EXISTS sp_recalcular_pagamentos_finalizados_com_cursor;

DELIMITER $$

-- Procedimento 01
-- Justificativa:
-- Atualiza o status de um atendimento de forma controlada.
-- Garante que apenas status validos sejam gravados.
CREATE PROCEDURE sp_atualizar_status_atendimento(
    IN p_id_atendimento INT,
    IN p_novo_status VARCHAR(20)
)
BEGIN
    DECLARE v_total INT DEFAULT 0;
    DECLARE v_status_normalizado VARCHAR(20);

    SET v_status_normalizado = LOWER(TRIM(p_novo_status));

    IF v_status_normalizado NOT IN ('agendado', 'em andamento', 'finalizado', 'cancelado') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Status de atendimento invalido.';
    END IF;

    SELECT COUNT(*)
    INTO v_total
    FROM atendimento
    WHERE id_atendimento = p_id_atendimento;

    IF v_total = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Atendimento nao encontrado.';
    END IF;

    UPDATE atendimento
    SET status = v_status_normalizado
    WHERE id_atendimento = p_id_atendimento;
END$$

-- Procedimento 02
-- Justificativa:
-- Recalcula o pagamento de um atendimento especifico no momento do fechamento.
-- Faz sentido na operacao real porque cada atendimento eh pago individualmente.
CREATE PROCEDURE sp_recalcular_pagamento_atendimento(
    IN p_id_atendimento INT
)
BEGIN
    DECLARE v_total INT DEFAULT 0;
    DECLARE v_status VARCHAR(20);
    DECLARE v_id_servico INT;
    DECLARE v_preco DECIMAL(10,2);
    DECLARE v_nota TINYINT;
    DECLARE v_desconto DECIMAL(10,2);
    DECLARE v_comprovante VARCHAR(200);

    SELECT COUNT(*)
    INTO v_total
    FROM atendimento
    WHERE id_atendimento = p_id_atendimento;

    IF v_total = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Atendimento nao encontrado.';
    END IF;

    SELECT a.status, a.id_servico, s.preco, av.nota
    INTO v_status, v_id_servico, v_preco, v_nota
    FROM atendimento a
    JOIN servico s
        ON s.id_servico = a.id_servico
    LEFT JOIN avaliacao av
        ON av.id_atendimento = a.id_atendimento
    WHERE a.id_atendimento = p_id_atendimento;

    IF v_status <> 'finalizado' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Apenas atendimentos finalizados podem gerar pagamento.';
    END IF;

    IF fn_situacao_avaliacao(v_nota) = 'critica' THEN
        SET v_desconto = ROUND(v_preco * 0.10, 2);
    ELSE
        SET v_desconto = 0.00;
    END IF;

    SET v_comprovante = CONCAT(
        'REC-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), '-ATD-', p_id_atendimento
    );

    IF EXISTS (
        SELECT 1
        FROM pagamento
        WHERE id_atendimento = p_id_atendimento
    ) THEN
        UPDATE pagamento
        SET valor_total = v_preco,
            descontos = v_desconto,
            comprovante = v_comprovante
        WHERE id_atendimento = p_id_atendimento;
    ELSE
        INSERT INTO pagamento (
            id_atendimento,
            forma_pagto,
            valor_total,
            descontos,
            comprovante
        ) VALUES (
            p_id_atendimento,
            'Pendente',
            v_preco,
            v_desconto,
            v_comprovante
        );
    END IF;

    INSERT INTO log_operacao (
        tabela_afetada,
        id_registro,
        acao,
        descricao
    ) VALUES (
        'pagamento',
        p_id_atendimento,
        'RECALCULO_INDIVIDUAL',
        CONCAT(
            'Pagamento recalculado individualmente para atendimento ',
            p_id_atendimento,
            ', servico ',
            v_id_servico,
            ', desconto ',
            FORMAT(v_desconto, 2)
        )
    );
END$$

-- Procedimento 03
-- Justificativa:
-- Percorre individualmente os atendimentos finalizados para recalcular pagamentos,
-- gerar comprovantes sequenciais e registrar cada processamento no log.
-- O cursor se justifica porque cada linha dispara uma sequencia de decisoes e
-- operacoes dependentes do atendimento atual: calculo de desconto, upsert do
-- pagamento e registro individual do processamento.
CREATE PROCEDURE sp_recalcular_pagamentos_finalizados_com_cursor()
BEGIN
    DECLARE v_fim INT DEFAULT 0;
    DECLARE v_id_atendimento INT;
    DECLARE v_id_servico INT;
    DECLARE v_preco DECIMAL(10,2);
    DECLARE v_nota TINYINT;
    DECLARE v_desconto DECIMAL(10,2);
    DECLARE v_comprovante VARCHAR(200);
    DECLARE v_sequencia INT DEFAULT 0;

    DECLARE cur_atendimentos CURSOR FOR
        SELECT
            a.id_atendimento,
            a.id_servico,
            s.preco,
            av.nota
        FROM atendimento a
        JOIN servico s
            ON s.id_servico = a.id_servico
        LEFT JOIN avaliacao av
            ON av.id_atendimento = a.id_atendimento
        WHERE a.status = 'finalizado'
        ORDER BY a.data, a.hora, a.id_atendimento;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_fim = 1;

    OPEN cur_atendimentos;

    loop_atendimentos: LOOP
        FETCH cur_atendimentos
        INTO v_id_atendimento, v_id_servico, v_preco, v_nota;

        IF v_fim = 1 THEN
            LEAVE loop_atendimentos;
        END IF;

        SET v_sequencia = v_sequencia + 1;

        IF fn_situacao_avaliacao(v_nota) = 'critica' THEN
            SET v_desconto = ROUND(v_preco * 0.10, 2);
        ELSE
            SET v_desconto = 0.00;
        END IF;

        SET v_comprovante = CONCAT(
            'REC-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), '-',
            LPAD(v_sequencia, 4, '0'), '-ATD-', v_id_atendimento
        );

        IF EXISTS (
            SELECT 1
            FROM pagamento
            WHERE id_atendimento = v_id_atendimento
        ) THEN
            UPDATE pagamento
            SET valor_total = v_preco,
                descontos = v_desconto,
                comprovante = v_comprovante
            WHERE id_atendimento = v_id_atendimento;
        ELSE
            INSERT INTO pagamento (
                id_atendimento,
                forma_pagto,
                valor_total,
                descontos,
                comprovante
            ) VALUES (
                v_id_atendimento,
                'Pendente',
                v_preco,
                v_desconto,
                v_comprovante
            );
        END IF;

        INSERT INTO log_operacao (
            tabela_afetada,
            id_registro,
            acao,
            descricao
        ) VALUES (
            'pagamento',
            v_id_atendimento,
            'RECALCULO_CURSOR',
            CONCAT(
                'Pagamento recalculado para atendimento ',
                v_id_atendimento,
                ', servico ',
                v_id_servico,
                ', desconto ',
                FORMAT(v_desconto, 2)
            )
        );
    END LOOP;

    CLOSE cur_atendimentos;
END$$

DELIMITER ;

-- Exemplos de uso:
-- CALL sp_atualizar_status_atendimento(1, 'finalizado');
-- CALL sp_recalcular_pagamento_atendimento(1);
-- CALL sp_recalcular_pagamentos_finalizados_com_cursor();
