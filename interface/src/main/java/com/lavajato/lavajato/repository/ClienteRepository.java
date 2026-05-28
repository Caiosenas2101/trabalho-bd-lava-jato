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
        c.setTelefone(rs.getString("telefone"));
        return c;
    };

    private final JdbcTemplate jdbcTemplate;

    public ClienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Cliente> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT c.id_cliente, c.nome, c.cpf, c.email, c.endereco_rua, c.endereco_bairro,
                               c.endereco_cidade, MIN(ct.telefone) AS telefone
                        FROM cliente c
                        LEFT JOIN cliente_telefone ct
                            ON ct.id_cliente = c.id_cliente
                        GROUP BY c.id_cliente, c.nome, c.cpf, c.email, c.endereco_rua, c.endereco_bairro, c.endereco_cidade
                        ORDER BY c.id_cliente
                        """,
                ROW_MAPPER);
    }

    public Cliente findById(Integer idCliente) {
        List<Cliente> clientes = jdbcTemplate.query(
                """
                        SELECT c.id_cliente, c.nome, c.cpf, c.email, c.endereco_rua, c.endereco_bairro,
                               c.endereco_cidade, MIN(ct.telefone) AS telefone
                        FROM cliente c
                        LEFT JOIN cliente_telefone ct
                            ON ct.id_cliente = c.id_cliente
                        WHERE c.id_cliente = ?
                        GROUP BY c.id_cliente, c.nome, c.cpf, c.email, c.endereco_rua, c.endereco_bairro, c.endereco_cidade
                        """,
                ROW_MAPPER,
                idCliente);
        if (clientes.isEmpty()) {
            return null;
        }
        return clientes.get(0);
    }

    public Cliente insert(Cliente cliente) {
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
            salvarTelefone(cliente.getIdCliente(), cliente.getTelefone());
        }
        return cliente;
    }

    public boolean update(Cliente cliente) {
        int linhasAfetadas = jdbcTemplate.update(
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
        if (linhasAfetadas > 0) {
            jdbcTemplate.update("DELETE FROM cliente_telefone WHERE id_cliente = ?", cliente.getIdCliente());
            salvarTelefone(cliente.getIdCliente(), cliente.getTelefone());
        }
        return linhasAfetadas > 0;
    }

    public boolean deleteById(Integer idCliente) {
        jdbcTemplate.update(
                """
                        DELETE FROM atendimento
                        WHERE id_cliente_veiculo = ?
                        """,
                idCliente);
        jdbcTemplate.update("DELETE FROM carro WHERE id_cliente = ?", idCliente);
        jdbcTemplate.update("DELETE FROM moto WHERE id_cliente = ?", idCliente);
        jdbcTemplate.update("DELETE FROM veiculo WHERE id_cliente = ?", idCliente);
        jdbcTemplate.update("DELETE FROM cliente_telefone WHERE id_cliente = ?", idCliente);
        int linhasAfetadas = jdbcTemplate.update("DELETE FROM cliente WHERE id_cliente = ?", idCliente);
        return linhasAfetadas > 0;
    }

    private void salvarTelefone(Integer idCliente, String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return;
        }
        jdbcTemplate.update(
                "INSERT INTO cliente_telefone (id_cliente, telefone) VALUES (?, ?)",
                idCliente,
                telefone.trim());
    }
}
