package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Avaliacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AvaliacaoRepository {

    private static final RowMapper<Avaliacao> ROW_MAPPER = (rs, rowNum) -> {
        Avaliacao a = new Avaliacao();
        a.setIdAvaliacao(rs.getInt("id_avaliacao"));
        a.setIdCliente(rs.getInt("id_cliente"));
        a.setIdAtendimento(rs.getInt("id_atendimento"));
        a.setNota(rs.getInt("nota"));
        a.setComentarios(rs.getString("comentarios"));
        Date data = rs.getDate("data");
        a.setData(data == null ? null : data.toLocalDate());
        return a;
    };

    private final JdbcTemplate jdbcTemplate;

    public AvaliacaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Avaliacao> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT id_avaliacao, id_cliente, id_atendimento, nota, comentarios, data
                        FROM avaliacao
                        ORDER BY data DESC, id_avaliacao DESC
                        """,
                ROW_MAPPER);
    }

    public Avaliacao findById(Integer idAvaliacao) {
        List<Avaliacao> avaliacoes = jdbcTemplate.query(
                """
                        SELECT id_avaliacao, id_cliente, id_atendimento, nota, comentarios, data
                        FROM avaliacao
                        WHERE id_avaliacao = ?
                        """,
                ROW_MAPPER,
                idAvaliacao);
        return avaliacoes.isEmpty() ? null : avaliacoes.get(0);
    }

    public Avaliacao insert(Avaliacao avaliacao) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO avaliacao (id_cliente, id_atendimento, nota, comentarios, data)
                            VALUES (?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, avaliacao.getIdCliente());
            ps.setInt(2, avaliacao.getIdAtendimento());
            ps.setInt(3, avaliacao.getNota());
            ps.setString(4, avaliacao.getComentarios());
            ps.setDate(5, avaliacao.getData() == null ? null : Date.valueOf(avaliacao.getData()));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            avaliacao.setIdAvaliacao(key.intValue());
        }
        return avaliacao;
    }

    public boolean update(Avaliacao avaliacao) {
        int linhasAfetadas = jdbcTemplate.update(
                """
                        UPDATE avaliacao
                        SET id_cliente = ?, id_atendimento = ?, nota = ?, comentarios = ?, data = ?
                        WHERE id_avaliacao = ?
                        """,
                avaliacao.getIdCliente(),
                avaliacao.getIdAtendimento(),
                avaliacao.getNota(),
                avaliacao.getComentarios(),
                avaliacao.getData(),
                avaliacao.getIdAvaliacao());
        return linhasAfetadas > 0;
    }

    public boolean deleteById(Integer idAvaliacao) {
        int linhasAfetadas = jdbcTemplate.update(
                "DELETE FROM avaliacao WHERE id_avaliacao = ?",
                idAvaliacao);
        return linhasAfetadas > 0;
    }
}
