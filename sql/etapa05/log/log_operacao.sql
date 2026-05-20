USE lava_jato;

-- =========================================================
-- ETAPA 05 - TABELA DE LOG
-- Execute antes dos procedimentos e triggers da etapa 05.
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
