package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Servico;
import com.lavajato.lavajato.repository.ServicoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@CrossOrigin("*")
public class ServicoController {

    private final ServicoRepository servicoRepository;

    public ServicoController(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @GetMapping
    public List<Servico> listar() {
        return servicoRepository.findAll();
    }

    @PostMapping
    public Servico cadastrar(@RequestBody Servico servico) {
        return servicoRepository.save(servico);
    }
}