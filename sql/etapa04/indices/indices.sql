USE lava_jato;

-- =========================================================
-- ETAPA 04 - SQL DOS INDICES
-- Execute este arquivo depois de create_tables.sql e insert_data.sql.
-- Os CREATE INDEX devem ser executados apenas uma vez.
-- =========================================================

-- Indice 01
-- Justificativa:
-- Esta combinacao melhora filtros por status e periodo.
-- Ela eh usada na Consulta 02 e na View 01.
CREATE INDEX idx_atendimento_status_data
ON atendimento (status, data);

-- Indice 02
-- Justificativa:
-- Esta combinacao melhora filtros por nota e o join com cliente.
-- Ela eh usada na Consulta 04 e na View 02.
CREATE INDEX idx_avaliacao_nota_cliente
ON avaliacao (nota, id_cliente);
