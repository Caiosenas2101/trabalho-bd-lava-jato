package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Veiculo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VeiculoRepository {

    private static final RowMapper<Veiculo> ROW_MAPPER = (rs, rowNum) -> {
        Veiculo v = new Veiculo();
        v.setPlaca(rs.getString("placa"));
        v.setIdCliente(rs.getInt("id_cliente"));
        v.setModelo(rs.getString("modelo"));
        v.setCor(rs.getString("cor"));
        int ano = rs.getInt("ano");
        v.setAno(rs.wasNull() ? null : ano);
        return v;
    };

    private final JdbcTemplate jdbcTemplate;

    public VeiculoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Veiculo> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT placa, id_cliente, modelo, cor, ano
                        FROM veiculo
                        ORDER BY id_cliente DESC, placa
                        """,
                ROW_MAPPER);
    }

    public Veiculo findById(String placa, Integer idCliente) {
        List<Veiculo> veiculos = jdbcTemplate.query(
                """
                        SELECT placa, id_cliente, modelo, cor, ano
                        FROM veiculo
                        WHERE placa = ?
                          AND id_cliente = ?
                        """,
                ROW_MAPPER,
                placa,
                idCliente);
        return veiculos.isEmpty() ? null : veiculos.get(0);
    }

    public Veiculo insert(Veiculo veiculo) {
        jdbcTemplate.update(
                """
                        INSERT INTO veiculo (placa, id_cliente, modelo, cor, ano)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                veiculo.getPlaca(),
                veiculo.getIdCliente(),
                veiculo.getModelo(),
                veiculo.getCor(),
                veiculo.getAno());
        return veiculo;
    }

    public boolean update(String placa, Integer idCliente, Veiculo veiculo) {
        int linhasAfetadas = jdbcTemplate.update(
                """
                        UPDATE veiculo
                        SET modelo = ?, cor = ?, ano = ?
                        WHERE placa = ?
                          AND id_cliente = ?
                        """,
                veiculo.getModelo(),
                veiculo.getCor(),
                veiculo.getAno(),
                placa,
                idCliente);
        return linhasAfetadas > 0;
    }

    public boolean deleteById(String placa, Integer idCliente) {
        jdbcTemplate.update(
                """
                        DELETE FROM atendimento
                        WHERE placa_veiculo = ?
                          AND id_cliente_veiculo = ?
                        """,
                placa,
                idCliente);
        jdbcTemplate.update("DELETE FROM carro WHERE placa = ? AND id_cliente = ?", placa, idCliente);
        jdbcTemplate.update("DELETE FROM moto WHERE placa = ? AND id_cliente = ?", placa, idCliente);
        int linhasAfetadas = jdbcTemplate.update(
                "DELETE FROM veiculo WHERE placa = ? AND id_cliente = ?",
                placa,
                idCliente);
        return linhasAfetadas > 0;
    }
}
