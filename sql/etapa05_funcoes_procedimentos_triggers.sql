USE lava_jato;

-- =========================================================
-- ETAPA 05 - FUNCOES, PROCEDIMENTOS E TRIGGERS
-- Execute este arquivo depois de create_tables.sql e insert_data.sql.
-- =========================================================

-- Tabela auxiliar usada por trigger e procedimentos para registrar operacoes
-- relevantes do dominio.
CREATE TABLE IF NOT EXISTS log_operacao (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    tabela_afetada VARCHAR(50) NOT NULL,
    id_registro INT NOT NULL,
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP FUNCTION IF EXISTS fn_valor_liquido_atendimento;
DROP FUNCTION IF EXISTS fn_situacao_avaliacao;
DROP PROCEDURE IF EXISTS sp_atualizar_status_atendimento;
DROP PROCEDURE IF EXISTS sp_recalcular_pagamentos_finalizados_com_cursor;
DROP TRIGGER IF EXISTS trg_atendimento_status_log;
DROP TRIGGER IF EXISTS trg_pagamento_valida_desconto;

DELIMITER $$

-- Funcao 01
-- Justificativa:
-- Centraliza o calculo do valor liquido pago em um atendimento.
-- A regra evita valor liquido negativo quando o desconto for maior que o total.
-- Utiliza estrutura condicional.
CREATE FUNCTION fn_valor_liquido_atendimento(p_id_atendimento INT)
RETURNS DECIMAL(10,2)
READS SQL DATA
BEGIN
    DECLARE v_valor_total DECIMAL(10,2);
    DECLARE v_descontos DECIMAL(10,2);
    DECLARE v_valor_liquido DECIMAL(10,2);

    SELECT valor_total, COALESCE(descontos, 0.00)
    INTO v_valor_total, v_descontos
    FROM pagamento
    WHERE id_atendimento = p_id_atendimento
    LIMIT 1;

    IF v_valor_total IS NULL THEN
        SET v_valor_liquido = NULL;
    ELSEIF v_descontos > v_valor_total THEN
        SET v_valor_liquido = 0.00;
    ELSE
        SET v_valor_liquido = v_valor_total - v_descontos;
    END IF;

    RETURN v_valor_liquido;
END$$

-- Funcao 02
-- Justificativa:
-- Classifica a satisfacao do cliente a partir da nota da avaliacao.
-- Facilita consultas e relatorios sem repetir a regra de negocio.
-- Utiliza estrutura condicional.
CREATE FUNCTION fn_situacao_avaliacao(p_nota TINYINT)
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE v_situacao VARCHAR(20);

    IF p_nota IS NULL THEN
        SET v_situacao = 'sem avaliacao';
    ELSEIF p_nota <= 4 THEN
        SET v_situacao = 'critica';
    ELSEIF p_nota <= 7 THEN
        SET v_situacao = 'regular';
    ELSE
        SET v_situacao = 'excelente';
    END IF;

    RETURN v_situacao;
END$$

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

-- Trigger 01
-- Justificativa:
-- Registra mudancas de status dos atendimentos, mantendo historico operacional.
-- Este trigger atualiza a tabela de logs exigida na etapa.
CREATE TRIGGER trg_atendimento_status_log
AFTER UPDATE ON atendimento
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO log_operacao (
            tabela_afetada,
            id_registro,
            acao,
            descricao
        ) VALUES (
            'atendimento',
            NEW.id_atendimento,
            'ALTERACAO_STATUS',
            CONCAT(
                'Status alterado de ',
                OLD.status,
                ' para ',
                NEW.status
            )
        );
    END IF;
END$$

-- Trigger 02
-- Justificativa:
-- Impede pagamentos inconsistentes no dominio do lava jato.
-- Um desconto nao pode ser maior que o valor total cobrado.
CREATE TRIGGER trg_pagamento_valida_desconto
BEFORE INSERT ON pagamento
FOR EACH ROW
BEGIN
    IF NEW.descontos IS NULL THEN
        SET NEW.descontos = 0.00;
    END IF;

    IF NEW.descontos > NEW.valor_total THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'O desconto nao pode ser maior que o valor total do pagamento.';
    END IF;
END$$

DELIMITER ;

-- Exemplos de uso:
-- SELECT fn_valor_liquido_atendimento(1) AS valor_liquido;
-- SELECT fn_situacao_avaliacao(8) AS situacao_avaliacao;
-- CALL sp_atualizar_status_atendimento(1, 'finalizado');
-- CALL sp_recalcular_pagamentos_finalizados_com_cursor();
-- SELECT * FROM log_operacao ORDER BY id_log;
