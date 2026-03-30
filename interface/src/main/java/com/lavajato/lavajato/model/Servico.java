package com.lavajato.lavajato.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Integer idServico;

    @Column(name = "nome_servico")
    private String nomeServico;

    private BigDecimal preco;

    @Column(name = "tempo_min")
    private Integer tempoMin;

    private String descricao;

    public Servico() {
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getTempoMin() {
        return tempoMin;
    }

    public void setTempoMin(Integer tempoMin) {
        this.tempoMin = tempoMin;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}