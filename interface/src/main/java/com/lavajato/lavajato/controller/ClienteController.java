package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Cliente;
import com.lavajato.lavajato.repository.ClienteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin("*")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @PostMapping
    public Cliente cadastrar(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}