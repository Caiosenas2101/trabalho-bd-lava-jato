package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Atendimento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.util.List;

@Repository
public class AtendimentoRepository {

    private static final RowMapper<Atendimento> ROW_MAPPER = (rs, rowNum) -> {
        Atendimento a = new Atendimento();
        a.setIdAtendimento(rs.getInt("id_atendimento"));
        a.setPlacaVeiculo(rs.getString("placa_veiculo"));
        a.setIdClienteVeiculo(rs.getInt("id_cliente_veiculo"));
        a.setIdServico(rs.getInt("id_servico"));
        a.setIdFuncionario(rs.getInt("id_funcionario"));
        a.setData(rs.getDate("data").toLocalDate());
        a.setHora(rs.getTime("hora").toLocalTime());
        a.setStatus(rs.getString("status"));
        return a;
    };

    private final JdbcTemplate jdbcTemplate;

    public AtendimentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Atendimento> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT id_atendimento, placa_veiculo, id_cliente_veiculo, id_servico, id_funcionario, data, hora, status
                        FROM atendimento
                        ORDER BY data DESC, hora DESC, id_atendimento DESC
                        """,
                ROW_MAPPER);
    }

    public Atendimento findById(Integer idAtendimento) {
        List<Atendimento> atendimentos = jdbcTemplate.query(
                """
                        SELECT id_atendimento, placa_veiculo, id_cliente_veiculo, id_servico, id_funcionario, data, hora, status
                        FROM atendimento
                        WHERE id_atendimento = ?
                        """,
                ROW_MAPPER,
                idAtendimento);
        return atendimentos.isEmpty() ? null : atendimentos.get(0);
    }

    public Atendimento insert(Atendimento atendimento) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO atendimento (placa_veiculo, id_cliente_veiculo, id_servico, id_funcionario, data, hora, status)
                            VALUES (?, ?, ?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, atendimento.getPlacaVeiculo());
            ps.setInt(2, atendimento.getIdClienteVeiculo());
            ps.setInt(3, atendimento.getIdServico());
            ps.setInt(4, atendimento.getIdFuncionario());
            ps.setDate(5, Date.valueOf(atendimento.getData()));
            ps.setTime(6, Time.valueOf(atendimento.getHora()));
            ps.setString(7, atendimento.getStatus());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            atendimento.setIdAtendimento(key.intValue());
            jdbcTemplate.update(
                    """
                            INSERT IGNORE INTO realiza (id_funcionario, id_atendimento)
                            VALUES (?, ?)
                            """,
                    atendimento.getIdFuncionario(),
                    atendimento.getIdAtendimento());
        }
        return atendimento;
    }

    public boolean update(Atendimento atendimento) {
        int linhasAfetadas = jdbcTemplate.update(
                """
                        UPDATE atendimento
                        SET placa_veiculo = ?, id_cliente_veiculo = ?, id_servico = ?, id_funcionario = ?, data = ?, hora = ?, status = ?
                        WHERE id_atendimento = ?
                        """,
                atendimento.getPlacaVeiculo(),
                atendimento.getIdClienteVeiculo(),
                atendimento.getIdServico(),
                atendimento.getIdFuncionario(),
                atendimento.getData(),
                atendimento.getHora(),
                atendimento.getStatus(),
                atendimento.getIdAtendimento());
        if (linhasAfetadas > 0) {
            jdbcTemplate.update("DELETE FROM realiza WHERE id_atendimento = ?", atendimento.getIdAtendimento());
            jdbcTemplate.update(
                    """
                            INSERT IGNORE INTO realiza (id_funcionario, id_atendimento)
                            VALUES (?, ?)
                            """,
                    atendimento.getIdFuncionario(),
                    atendimento.getIdAtendimento());
        }
        return linhasAfetadas > 0;
    }

    public boolean deleteById(Integer idAtendimento) {
        int linhasAfetadas = jdbcTemplate.update(
                "DELETE FROM atendimento WHERE id_atendimento = ?",
                idAtendimento);
        return linhasAfetadas > 0;
    }
}
