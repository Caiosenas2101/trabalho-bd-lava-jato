USE lava_jato;

-- =========================================================
-- ETAPA 04 - SQL DAS CONSULTAS
-- Execute este arquivo depois de create_tables.sql e insert_data.sql.
-- =========================================================

-- Consulta 01
-- Join + Group By + Having
-- Lista servicos finalizados com faturamento liquido minimo.
SELECT
    s.id_servico,
    s.nome_servico,
    COUNT(a.id_atendimento) AS total_execucoes,
    SUM(p.valor_total - p.descontos) AS faturamento_liquido
FROM servico s
JOIN atendimento a
    ON a.id_servico = s.id_servico
JOIN pagamento p
    ON p.id_atendimento = a.id_atendimento
WHERE a.status = 'finalizado'
GROUP BY s.id_servico, s.nome_servico
HAVING SUM(p.valor_total - p.descontos) >= 60
ORDER BY faturamento_liquido DESC;

-- Consulta 02
-- 2 Joins + Where
-- Mostra atendimentos finalizados em um intervalo de datas.
SELECT
    a.id_atendimento,
    c.nome AS cliente,
    s.nome_servico,
    a.data,
    a.hora,
    a.status
FROM atendimento a
JOIN cliente c
    ON c.id_cliente = a.id_cliente_veiculo
JOIN servico s
    ON s.id_servico = a.id_servico
WHERE a.status = 'finalizado'
  AND a.data BETWEEN '2026-04-10' AND '2026-04-20'
ORDER BY a.data, a.hora;

-- Consulta 03
-- Anti Join pela esquerda
-- Lista atendimentos que ainda nao possuem pagamento registrado.
SELECT
    a.id_atendimento,
    c.nome AS cliente,
    s.nome_servico,
    a.data,
    a.hora,
    a.status
FROM atendimento a
JOIN cliente c
    ON c.id_cliente = a.id_cliente_veiculo
JOIN servico s
    ON s.id_servico = a.id_servico
LEFT JOIN pagamento p
    ON p.id_atendimento = a.id_atendimento
WHERE p.id_pagamento IS NULL
ORDER BY a.data, a.hora;

-- Consulta 04
-- Subconsulta
-- Lista clientes com avaliacao acima da media geral.
SELECT
    c.id_cliente,
    c.nome,
    av.nota,
    av.comentarios
FROM avaliacao av
JOIN cliente c
    ON c.id_cliente = av.id_cliente
WHERE av.nota > (
    SELECT AVG(av2.nota)
    FROM avaliacao av2
)
ORDER BY av.nota DESC, c.nome;
