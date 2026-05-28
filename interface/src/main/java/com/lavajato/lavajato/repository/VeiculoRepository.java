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
        v.setTipo(rs.getString("tipo"));
        v.setTipoCombustivel(rs.getString("tipo_combustivel"));
        v.setCilindrada(rs.getString("cilindrada"));
        return v;
    };

    private final JdbcTemplate jdbcTemplate;

    public VeiculoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Veiculo> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT v.placa, v.id_cliente, v.modelo, v.cor, v.ano,
                               CASE
                                   WHEN c.placa IS NOT NULL THEN 'Carro'
                                   WHEN m.placa IS NOT NULL THEN 'Moto'
                                   ELSE NULL
                               END AS tipo,
                               c.tipo_combustivel,
                               m.cilindrada
                        FROM veiculo v
                        LEFT JOIN carro c
                            ON c.placa = v.placa
                           AND c.id_cliente = v.id_cliente
                        LEFT JOIN moto m
                            ON m.placa = v.placa
                           AND m.id_cliente = v.id_cliente
                        ORDER BY v.id_cliente DESC, v.placa
                        """,
                ROW_MAPPER);
    }

    public Veiculo findById(String placa, Integer idCliente) {
        List<Veiculo> veiculos = jdbcTemplate.query(
                """
                        SELECT v.placa, v.id_cliente, v.modelo, v.cor, v.ano,
                               CASE
                                   WHEN c.placa IS NOT NULL THEN 'Carro'
                                   WHEN m.placa IS NOT NULL THEN 'Moto'
                                   ELSE NULL
                               END AS tipo,
                               c.tipo_combustivel,
                               m.cilindrada
                        FROM veiculo v
                        LEFT JOIN carro c
                            ON c.placa = v.placa
                           AND c.id_cliente = v.id_cliente
                        LEFT JOIN moto m
                            ON m.placa = v.placa
                           AND m.id_cliente = v.id_cliente
                        WHERE v.placa = ?
                          AND v.id_cliente = ?
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
        sincronizarEspecializacao(veiculo);
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
        if (linhasAfetadas > 0) {
            veiculo.setPlaca(placa);
            veiculo.setIdCliente(idCliente);
            sincronizarEspecializacao(veiculo);
        }
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

    private void sincronizarEspecializacao(Veiculo veiculo) {
        String tipo = normalizarTipo(veiculo.getTipo());
        jdbcTemplate.update(
                "DELETE FROM carro WHERE placa = ? AND id_cliente = ?",
                veiculo.getPlaca(),
                veiculo.getIdCliente());
        jdbcTemplate.update(
                "DELETE FROM moto WHERE placa = ? AND id_cliente = ?",
                veiculo.getPlaca(),
                veiculo.getIdCliente());

        if ("Carro".equals(tipo)) {
            jdbcTemplate.update(
                    "INSERT INTO carro (placa, id_cliente, tipo_combustivel) VALUES (?, ?, ?)",
                    veiculo.getPlaca(),
                    veiculo.getIdCliente(),
                    veiculo.getTipoCombustivel());
            veiculo.setTipo(tipo);
            return;
        }

        jdbcTemplate.update(
                "INSERT INTO moto (placa, id_cliente, cilindrada) VALUES (?, ?, ?)",
                veiculo.getPlaca(),
                veiculo.getIdCliente(),
                veiculo.getCilindrada());
        veiculo.setTipo(tipo);
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Veiculo deve ser Carro ou Moto.");
        }
        if ("carro".equalsIgnoreCase(tipo.trim())) {
            return "Carro";
        }
        if ("moto".equalsIgnoreCase(tipo.trim())) {
            return "Moto";
        }
        throw new IllegalArgumentException("Veiculo deve ser Carro ou Moto.");
    }
}
