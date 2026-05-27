package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Funcionario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FuncionarioRepository {

    private static final RowMapper<Funcionario> ROW_MAPPER = (rs, rowNum) -> {
        Funcionario f = new Funcionario();
        f.setIdFuncionario(rs.getInt("id_funcionario"));
        f.setNome(rs.getString("nome"));
        f.setCargo(rs.getString("cargo"));
        int idSupervisor = rs.getInt("id_supervisor");
        f.setIdSupervisor(rs.wasNull() ? null : idSupervisor);
        return f;
    };

    private final JdbcTemplate jdbcTemplate;

    public FuncionarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Funcionario> findAll() {
        return jdbcTemplate.query(
                "SELECT id_funcionario, nome, cargo, id_supervisor FROM funcionario ORDER BY id_funcionario",
                ROW_MAPPER);
    }

    public Funcionario findById(Integer idFuncionario) {
        List<Funcionario> funcionarios = jdbcTemplate.query(
                """
                        SELECT id_funcionario, nome, cargo, id_supervisor
                        FROM funcionario
                        WHERE id_funcionario = ?
                        """,
                ROW_MAPPER,
                idFuncionario);
        return funcionarios.isEmpty() ? null : funcionarios.get(0);
    }

    public Funcionario insert(Funcionario funcionario) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO funcionario (nome, cargo, id_supervisor)
                            VALUES (?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getCargo());
            ps.setObject(3, funcionario.getIdSupervisor());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            funcionario.setIdFuncionario(key.intValue());
        }
        return funcionario;
    }

    public boolean update(Funcionario funcionario) {
        int linhasAfetadas = jdbcTemplate.update(
                """
                        UPDATE funcionario
                        SET nome = ?, cargo = ?, id_supervisor = ?
                        WHERE id_funcionario = ?
                        """,
                funcionario.getNome(),
                funcionario.getCargo(),
                funcionario.getIdSupervisor(),
                funcionario.getIdFuncionario());
        return linhasAfetadas > 0;
    }

    public boolean deleteById(Integer idFuncionario) {
        jdbcTemplate.update("DELETE FROM realiza WHERE id_funcionario = ?", idFuncionario);
        jdbcTemplate.update("DELETE FROM lavador WHERE id_funcionario = ?", idFuncionario);
        jdbcTemplate.update("DELETE FROM gerente WHERE id_funcionario = ?", idFuncionario);
        jdbcTemplate.update("DELETE FROM funcionario_telefone WHERE id_funcionario = ?", idFuncionario);
        jdbcTemplate.update(
                "UPDATE funcionario SET id_supervisor = NULL WHERE id_supervisor = ?",
                idFuncionario);
        int linhasAfetadas = jdbcTemplate.update(
                "DELETE FROM funcionario WHERE id_funcionario = ?",
                idFuncionario);
        return linhasAfetadas > 0;
    }
}
