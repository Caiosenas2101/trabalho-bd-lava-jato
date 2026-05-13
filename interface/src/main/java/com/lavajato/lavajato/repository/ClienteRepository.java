package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ClienteRepository {

    private static final RowMapper<Cliente> ROW_MAPPER = (rs, rowNum) -> {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        c.setEnderecoRua(rs.getString("endereco_rua"));
        c.setEnderecoBairro(rs.getString("endereco_bairro"));
        c.setEnderecoCidade(rs.getString("endereco_cidade"));
        return c;
    };

    private final JdbcTemplate jdbcTemplate;

    public ClienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Cliente> findAll() {
        return jdbcTemplate.query(
                "SELECT id_cliente, nome, cpf, email, endereco_rua, endereco_bairro, endereco_cidade FROM cliente ORDER BY id_cliente",
                ROW_MAPPER);
    }

    public Cliente save(Cliente cliente) {
        if (cliente.getIdCliente() == null) {
            insert(cliente);
        } else {
            update(cliente);
        }
        return cliente;
    }

    private void insert(Cliente cliente) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO cliente (nome, cpf, email, endereco_rua, endereco_bairro, endereco_cidade)
                            VALUES (?, ?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getEnderecoRua());
            ps.setString(5, cliente.getEnderecoBairro());
            ps.setString(6, cliente.getEnderecoCidade());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            cliente.setIdCliente(key.intValue());
        }
    }

    private void update(Cliente cliente) {
        jdbcTemplate.update(
                """
                        UPDATE cliente SET nome = ?, cpf = ?, email = ?, endereco_rua = ?, endereco_bairro = ?, endereco_cidade = ?
                        WHERE id_cliente = ?
                        """,
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getEnderecoRua(),
                cliente.getEnderecoBairro(),
                cliente.getEnderecoCidade(),
                cliente.getIdCliente());
    }
}
