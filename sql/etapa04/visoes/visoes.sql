USE lava_jato;

-- =========================================================
-- ETAPA 04 - SQL DAS VISOES
-- Execute este arquivo depois de create_tables.sql e insert_data.sql.
-- =========================================================

-- View 01
-- Justificativa:
-- Centraliza uma visualizacao pronta para acompanhamento operacional.
-- Reune cliente, veiculo, servico e funcionario em um unico resultado.
-- Estrutura: 3+ joins + where.
DROP VIEW IF EXISTS vw_atendimentos_finalizados_detalhados;

CREATE VIEW vw_atendimentos_finalizados_detalhados AS
SELECT
    a.id_atendimento,
    a.data,
    a.hora,
    c.nome AS cliente,
    v.placa,
    v.modelo,
    s.nome_servico,
    f.nome AS funcionario_responsavel,
    a.status
FROM atendimento a
JOIN cliente c
    ON c.id_cliente = a.id_cliente_veiculo
JOIN veiculo v
    ON v.placa = a.placa_veiculo
   AND v.id_cliente = a.id_cliente_veiculo
JOIN servico s
    ON s.id_servico = a.id_servico
JOIN realiza r
    ON r.id_atendimento = a.id_atendimento
JOIN funcionario f
    ON f.id_funcionario = r.id_funcionario
WHERE a.status = 'finalizado';

-- View 02
-- Justificativa:
-- Facilita a analise de clientes melhor avaliados.
-- A view mostra a avaliacao e calcula quantos atendimentos finalizados
-- o cliente ja teve, sem precisar reescrever a subconsulta.
-- Estrutura: 1 join + subconsulta.
DROP VIEW IF EXISTS vw_clientes_com_boas_avaliacoes;

CREATE VIEW vw_clientes_com_boas_avaliacoes AS
SELECT
    c.id_cliente,
    c.nome,
    av.nota,
    av.comentarios,
    av.data AS data_avaliacao,
    (
        SELECT COUNT(*)
        FROM atendimento a
        WHERE a.id_cliente_veiculo = c.id_cliente
          AND a.status = 'finalizado'
    ) AS total_atendimentos_finalizados
FROM cliente c
JOIN avaliacao av
    ON av.id_cliente = c.id_cliente
WHERE av.nota >= (
    SELECT AVG(av2.nota)
    FROM avaliacao av2
);

-- Consultas simples para visualizar as views criadas.
SELECT * FROM vw_atendimentos_finalizados_detalhados;
SELECT * FROM vw_clientes_com_boas_avaliacoes;
