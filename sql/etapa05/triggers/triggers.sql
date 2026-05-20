USE lava_jato;

-- =========================================================
-- ETAPA 05 - TRIGGERS
-- Execute depois da tabela de log da etapa 05.
-- =========================================================

DROP TRIGGER IF EXISTS trg_atendimento_status_log;
DROP TRIGGER IF EXISTS trg_pagamento_valida_desconto;

DELIMITER $$

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

-- Exemplo de consulta do log:
-- SELECT * FROM log_operacao ORDER BY id_log;
