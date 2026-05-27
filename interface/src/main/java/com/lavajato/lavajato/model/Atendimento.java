package com.lavajato.lavajato.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Atendimento {

    private Integer idAtendimento;
    private String placaVeiculo;
    private Integer idClienteVeiculo;
    private Integer idServico;
    private Integer idFuncionario;
    private LocalDate data;
    private LocalTime hora;
    private String status;

    public Atendimento() {
    }

    public Integer getIdAtendimento() {
        return idAtendimento;
    }

    public void setIdAtendimento(Integer idAtendimento) {
        this.idAtendimento = idAtendimento;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public Integer getIdClienteVeiculo() {
        return idClienteVeiculo;
    }

    public void setIdClienteVeiculo(Integer idClienteVeiculo) {
        this.idClienteVeiculo = idClienteVeiculo;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
