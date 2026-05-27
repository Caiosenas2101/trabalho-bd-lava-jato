package com.lavajato.lavajato.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RelatorioRepository {

    private final JdbcTemplate jdbcTemplate;

    public RelatorioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> servicosComFaturamentoMinimo(BigDecimal faturamentoMinimo) {
        return jdbcTemplate.queryForList(
                """
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
                        HAVING SUM(p.valor_total - p.descontos) >= ?
                        ORDER BY faturamento_liquido DESC
                        """,
                faturamentoMinimo);
    }

    public List<Map<String, Object>> atendimentosFinalizadosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return jdbcTemplate.queryForList(
                """
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
                          AND a.data BETWEEN ? AND ?
                        ORDER BY a.data, a.hora
                        """,
                dataInicio,
                dataFim);
    }

    public List<Map<String, Object>> atendimentosSemPagamento() {
        return jdbcTemplate.queryForList(
                """
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
                        ORDER BY a.data, a.hora
                        """);
    }

    public List<Map<String, Object>> clientesComAvaliacaoAcimaDaMedia() {
        return jdbcTemplate.queryForList(
                """
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
                        ORDER BY av.nota DESC, c.nome
                        """);
    }

    public List<Map<String, Object>> viewAtendimentosFinalizados() {
        return jdbcTemplate.queryForList(
                "SELECT * FROM vw_atendimentos_finalizados_detalhados ORDER BY data DESC, hora DESC");
    }

    public List<Map<String, Object>> viewClientesComBoasAvaliacoes() {
        return jdbcTemplate.queryForList(
                "SELECT * FROM vw_clientes_com_boas_avaliacoes ORDER BY nota DESC, nome");
    }

    public BigDecimal valorLiquidoAtendimento(Integer idAtendimento) {
        return jdbcTemplate.queryForObject(
                "SELECT fn_valor_liquido_atendimento(?)",
                BigDecimal.class,
                idAtendimento);
    }

    public String situacaoAvaliacao(Integer nota) {
        return jdbcTemplate.queryForObject(
                "SELECT fn_situacao_avaliacao(?)",
                String.class,
                nota);
    }

    public List<Map<String, Object>> avaliacaoDoAtendimento(Integer idAtendimento) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            av.id_avaliacao,
                            av.id_atendimento,
                            c.nome AS cliente,
                            av.nota,
                            fn_situacao_avaliacao(av.nota) AS classificacao,
                            av.comentarios,
                            av.data
                        FROM avaliacao av
                        JOIN cliente c
                            ON c.id_cliente = av.id_cliente
                        WHERE av.id_atendimento = ?
                        """,
                idAtendimento);
    }

    public void atualizarStatusAtendimento(Integer idAtendimento, String status) {
        jdbcTemplate.update("CALL sp_atualizar_status_atendimento(?, ?)", idAtendimento, status);
    }

    public void recalcularPagamentosFinalizados() {
        jdbcTemplate.update("CALL sp_recalcular_pagamentos_finalizados_com_cursor()");
    }

    public List<Map<String, Object>> logs() {
        return jdbcTemplate.queryForList(
                """
                        SELECT id_log, tabela_afetada, id_registro, acao, descricao, data_hora
                        FROM log_operacao
                        ORDER BY id_log DESC
                        LIMIT 50
                        """);
    }

    public List<Map<String, Object>> indicadoresDashboard() {
        return jdbcTemplate.queryForList(
                """
                        SELECT 'clientes' AS indicador, COUNT(*) AS valor FROM cliente
                        UNION ALL
                        SELECT 'servicos', COUNT(*) FROM servico
                        UNION ALL
                        SELECT 'atendimentos', COUNT(*) FROM atendimento
                        UNION ALL
                        SELECT 'avaliacao_media', ROUND(AVG(nota), 2) FROM avaliacao
                        UNION ALL
                        SELECT 'faturamento_liquido', ROUND(SUM(valor_total - descontos), 2) FROM pagamento
                        """);
    }

    public List<Map<String, Object>> veiculosParaAtendimento() {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            v.placa,
                            v.id_cliente,
                            v.modelo,
                            c.nome AS cliente
                        FROM veiculo v
                        JOIN cliente c
                            ON c.id_cliente = v.id_cliente
                        ORDER BY c.nome, v.placa
                        """);
    }

    // =========================================================
    // DASHBOARD ESTATISTICO - INDICADORES + GRAFICOS DINAMICOS
    // Todos os dados vem do banco de dados (MySQL) via JDBC.
    // =========================================================

    public Map<String, Object> resumoDashboard(LocalDate inicio, LocalDate fim) {
        Map<String, Object> totais = jdbcTemplate.queryForMap(
                """
                        SELECT
                            (SELECT COUNT(*) FROM cliente)                                                  AS total_clientes,
                            (SELECT COUNT(*) FROM funcionario)                                              AS total_funcionarios,
                            (SELECT COUNT(*) FROM servico)                                                  AS total_servicos,
                            (SELECT COUNT(*) FROM veiculo)                                                  AS total_veiculos,
                            (SELECT COUNT(*) FROM atendimento)                                              AS total_atendimentos,
                            (SELECT COUNT(*) FROM atendimento WHERE status = 'finalizado')                  AS atendimentos_finalizados,
                            (SELECT COUNT(*) FROM avaliacao)                                                AS total_avaliacoes
                        """);

        Map<String, Object> periodo = jdbcTemplate.queryForMap(
                """
                        SELECT
                            COUNT(a.id_atendimento)                                                         AS atendimentos_periodo,
                            COALESCE(ROUND(SUM(p.valor_total - p.descontos), 2), 0)                         AS faturamento_periodo,
                            COALESCE(ROUND(AVG(p.valor_total - p.descontos), 2), 0)                         AS ticket_medio,
                            COALESCE(ROUND(SUM(p.descontos), 2), 0)                                         AS descontos_periodo
                        FROM atendimento a
                        LEFT JOIN pagamento p
                            ON p.id_atendimento = a.id_atendimento
                        WHERE a.status = 'finalizado'
                          AND a.data BETWEEN ? AND ?
                        """,
                inicio, fim);

        Map<String, Object> estatisticas = jdbcTemplate.queryForMap(
                """
                        SELECT
                            COUNT(*)                                                                         AS qtd_avaliacoes,
                            COALESCE(ROUND(AVG(av.nota), 2), 0)                                              AS media_nota,
                            COALESCE(ROUND(VAR_POP(av.nota), 2), 0)                                          AS variancia_nota,
                            COALESCE(ROUND(STDDEV_POP(av.nota), 2), 0)                                       AS desvio_padrao_nota,
                            COALESCE(MIN(av.nota), 0)                                                        AS nota_minima,
                            COALESCE(MAX(av.nota), 0)                                                        AS nota_maxima
                        FROM avaliacao av
                        JOIN atendimento a ON a.id_atendimento = av.id_atendimento
                        WHERE a.data BETWEEN ? AND ?
                        """,
                inicio, fim);

        Map<String, Object> mediana = jdbcTemplate.queryForMap(
                """
                        SELECT COALESCE(ROUND(AVG(nota), 2), 0) AS mediana_nota
                        FROM (
                            SELECT av.nota,
                                   ROW_NUMBER() OVER (ORDER BY av.nota) AS rn,
                                   COUNT(*) OVER ()                     AS total
                            FROM avaliacao av
                            JOIN atendimento a ON a.id_atendimento = av.id_atendimento
                            WHERE a.data BETWEEN ? AND ?
                        ) t
                        WHERE rn IN (FLOOR((total + 1) / 2), FLOOR((total + 2) / 2))
                        """,
                inicio, fim);

        List<Map<String, Object>> modaResultado = jdbcTemplate.queryForList(
                """
                        SELECT av.nota AS moda_nota, COUNT(*) AS moda_frequencia
                        FROM avaliacao av
                        JOIN atendimento a ON a.id_atendimento = av.id_atendimento
                        WHERE a.data BETWEEN ? AND ?
                        GROUP BY av.nota
                        ORDER BY moda_frequencia DESC, av.nota DESC
                        LIMIT 1
                        """,
                inicio, fim);

        Map<String, Object> resumo = new LinkedHashMap<>();
        resumo.putAll(totais);
        resumo.putAll(periodo);
        resumo.putAll(estatisticas);
        resumo.putAll(mediana);
        if (modaResultado.isEmpty()) {
            resumo.put("moda_nota", 0);
            resumo.put("moda_frequencia", 0);
        } else {
            resumo.putAll(modaResultado.get(0));
        }

        Object atendimentosFinalizados = totais.get("atendimentos_finalizados");
        Object totalAtendimentos = totais.get("total_atendimentos");
        if (atendimentosFinalizados instanceof Number a && totalAtendimentos instanceof Number t && t.longValue() > 0) {
            double taxa = (a.doubleValue() / t.doubleValue()) * 100.0;
            resumo.put("taxa_conclusao", Math.round(taxa * 100.0) / 100.0);
        } else {
            resumo.put("taxa_conclusao", 0);
        }

        return resumo;
    }

    public List<Map<String, Object>> faturamentoPorServico(LocalDate inicio, LocalDate fim, Integer limite) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            s.id_servico,
                            s.nome_servico,
                            COUNT(a.id_atendimento)                                  AS total_atendimentos,
                            COALESCE(ROUND(SUM(p.valor_total - p.descontos), 2), 0) AS faturamento_liquido,
                            COALESCE(ROUND(AVG(p.valor_total - p.descontos), 2), 0) AS ticket_medio
                        FROM servico s
                        JOIN atendimento a   ON a.id_servico = s.id_servico
                        JOIN pagamento  p    ON p.id_atendimento = a.id_atendimento
                        WHERE a.status = 'finalizado'
                          AND a.data BETWEEN ? AND ?
                        GROUP BY s.id_servico, s.nome_servico
                        ORDER BY faturamento_liquido DESC
                        LIMIT ?
                        """,
                inicio, fim, limite);
    }

    public List<Map<String, Object>> atendimentosPorStatus(LocalDate inicio, LocalDate fim) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            a.status                AS status,
                            COUNT(*)                AS quantidade
                        FROM atendimento a
                        WHERE a.data BETWEEN ? AND ?
                        GROUP BY a.status
                        ORDER BY quantidade DESC
                        """,
                inicio, fim);
    }

    public List<Map<String, Object>> tendenciaTemporal(LocalDate inicio, LocalDate fim, String granularidade) {
        String formato;
        if ("mes".equalsIgnoreCase(granularidade)) {
            formato = "%Y-%m";
        } else if ("semana".equalsIgnoreCase(granularidade)) {
            formato = "%x-S%v";
        } else {
            formato = "%Y-%m-%d";
        }

        return jdbcTemplate.queryForList(
                """
                        SELECT
                            DATE_FORMAT(a.data, ?)                                              AS periodo,
                            COUNT(a.id_atendimento)                                             AS total_atendimentos,
                            SUM(CASE WHEN a.status = 'finalizado' THEN 1 ELSE 0 END)            AS finalizados,
                            COALESCE(ROUND(SUM(p.valor_total - p.descontos), 2), 0)             AS faturamento
                        FROM atendimento a
                        LEFT JOIN pagamento p
                            ON p.id_atendimento = a.id_atendimento
                            AND a.status = 'finalizado'
                        WHERE a.data BETWEEN ? AND ?
                        GROUP BY periodo
                        ORDER BY MIN(a.data)
                        """,
                formato, inicio, fim);
    }

    public List<Map<String, Object>> comparativoTopServicos(LocalDate inicio, LocalDate fim, Integer limite) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            s.id_servico,
                            s.nome_servico,
                            COUNT(a.id_atendimento)                                       AS atendimentos,
                            COALESCE(ROUND(SUM(p.valor_total - p.descontos), 2), 0)       AS faturamento,
                            COALESCE(ROUND(AVG(av.nota), 2), 0)                           AS nota_media,
                            COALESCE(ROUND(AVG(s.tempo_min), 2), 0)                       AS tempo_medio
                        FROM servico s
                        JOIN atendimento a    ON a.id_servico = s.id_servico
                        LEFT JOIN pagamento p ON p.id_atendimento = a.id_atendimento
                        LEFT JOIN avaliacao av ON av.id_atendimento = a.id_atendimento
                        WHERE a.data BETWEEN ? AND ?
                        GROUP BY s.id_servico, s.nome_servico
                        ORDER BY atendimentos DESC, faturamento DESC
                        LIMIT ?
                        """,
                inicio, fim, limite);
    }

    public List<Map<String, Object>> distribuicaoNotas(LocalDate inicio, LocalDate fim) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            n.nota                          AS nota,
                            COALESCE(COUNT(av.id_avaliacao), 0) AS frequencia
                        FROM (
                            SELECT 1 AS nota UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                            UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
                            UNION ALL SELECT 9 UNION ALL SELECT 10
                        ) n
                        LEFT JOIN avaliacao av
                            ON av.nota = n.nota
                        LEFT JOIN atendimento a
                            ON a.id_atendimento = av.id_atendimento
                           AND a.data BETWEEN ? AND ?
                        GROUP BY n.nota
                        ORDER BY n.nota
                        """,
                inicio, fim);
    }

    public List<Map<String, Object>> formasPagamento(LocalDate inicio, LocalDate fim) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            p.forma_pagto                                            AS forma,
                            COUNT(*)                                                 AS quantidade,
                            COALESCE(ROUND(SUM(p.valor_total - p.descontos), 2), 0)  AS faturamento
                        FROM pagamento p
                        JOIN atendimento a ON a.id_atendimento = p.id_atendimento
                        WHERE a.data BETWEEN ? AND ?
                        GROUP BY p.forma_pagto
                        ORDER BY faturamento DESC
                        """,
                inicio, fim);
    }

    public List<Map<String, Object>> topClientes(LocalDate inicio, LocalDate fim, Integer limite) {
        return jdbcTemplate.queryForList(
                """
                        SELECT
                            c.id_cliente,
                            c.nome,
                            COUNT(a.id_atendimento)                                       AS atendimentos,
                            COALESCE(ROUND(SUM(p.valor_total - p.descontos), 2), 0)       AS gasto_total,
                            COALESCE(ROUND(AVG(av.nota), 2), 0)                           AS nota_media
                        FROM cliente c
                        JOIN atendimento a      ON a.id_cliente_veiculo = c.id_cliente
                        LEFT JOIN pagamento p   ON p.id_atendimento = a.id_atendimento
                        LEFT JOIN avaliacao av  ON av.id_atendimento = a.id_atendimento
                        WHERE a.data BETWEEN ? AND ?
                        GROUP BY c.id_cliente, c.nome
                        ORDER BY gasto_total DESC, atendimentos DESC
                        LIMIT ?
                        """,
                inicio, fim, limite);
    }
}
