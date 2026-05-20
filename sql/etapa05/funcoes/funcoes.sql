USE lava_jato;

-- =========================================================
-- ETAPA 05 - FUNCOES
-- Execute depois de create_tables.sql e insert_data.sql.
-- =========================================================

DROP FUNCTION IF EXISTS fn_valor_liquido_atendimento;
DROP FUNCTION IF EXISTS fn_situacao_avaliacao;

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

DELIMITER ;

-- Exemplos de uso:
-- SELECT fn_valor_liquido_atendimento(1) AS valor_liquido;
-- SELECT fn_situacao_avaliacao(8) AS situacao_avaliacao;
