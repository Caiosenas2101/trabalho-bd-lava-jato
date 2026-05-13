package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Servico;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ServicoRepository {

    private static final RowMapper<Servico> ROW_MAPPER = (rs, rowNum) -> {
        Servico s = new Servico();
        s.setIdServico(rs.getInt("id_servico"));
        s.setNomeServico(rs.getString("nome_servico"));
        s.setPreco(rs.getBigDecimal("preco"));
        int tempo = rs.getInt("tempo_min");
        if (rs.wasNull()) {
            s.setTempoMin(null);
        } else {
            s.setTempoMin(tempo);
        }
        s.setDescricao(rs.getString("descricao"));
        return s;
    };

    private final JdbcTemplate jdbcTemplate;

    public ServicoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Servico> findAll() {
        return jdbcTemplate.query(
                "SELECT id_servico, nome_servico, preco, tempo_min, descricao FROM servico ORDER BY id_servico",
                ROW_MAPPER);
    }

    public Servico findById(Integer idServico) {
        List<Servico> servicos = jdbcTemplate.query(
                """
                        SELECT id_servico, nome_servico, preco, tempo_min, descricao
                        FROM servico
                        WHERE id_servico = ?
                        """,
                ROW_MAPPER,
                idServico);
        if (servicos.isEmpty()) {
            return null;
        }
        return servicos.get(0);
    }

    public Servico insert(Servico servico) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO servico (nome_servico, preco, tempo_min, descricao)
                            VALUES (?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, servico.getNomeServico());
            ps.setBigDecimal(2, servico.getPreco() != null ? servico.getPreco() : BigDecimal.ZERO);
            if (servico.getTempoMin() != null) {
                ps.setInt(3, servico.getTempoMin());
            } else {
                ps.setObject(3, null);
            }
            ps.setString(4, servico.getDescricao());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            servico.setIdServico(key.intValue());
        }
        return servico;
    }

    public boolean update(Servico servico) {
        int linhasAfetadas = jdbcTemplate.update(
                """
                        UPDATE servico SET nome_servico = ?, preco = ?, tempo_min = ?, descricao = ?
                        WHERE id_servico = ?
                        """,
                servico.getNomeServico(),
                servico.getPreco(),
                servico.getTempoMin(),
                servico.getDescricao(),
                servico.getIdServico());
        return linhasAfetadas > 0;
    }

    public boolean deleteById(Integer idServico) {
        jdbcTemplate.update("DELETE FROM atendimento WHERE id_servico = ?", idServico);
        int linhasAfetadas = jdbcTemplate.update("DELETE FROM servico WHERE id_servico = ?", idServico);
        return linhasAfetadas > 0;
    }
}
