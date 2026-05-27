package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Funcionario;
import com.lavajato.lavajato.repository.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin("*")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping
    public List<Funcionario> listar() {
        return funcionarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Integer id) {
        Funcionario funcionario = funcionarioRepository.findById(id);
        if (funcionario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(funcionario);
    }

    @PostMapping
    public ResponseEntity<Funcionario> cadastrar(@RequestBody Funcionario funcionario) {
        funcionario.setIdFuncionario(null);
        Funcionario funcionarioSalvo = funcionarioRepository.insert(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(@PathVariable Integer id, @RequestBody Funcionario funcionario) {
        funcionario.setIdFuncionario(id);
        boolean atualizado = funcionarioRepository.update(funcionario);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(funcionarioRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        boolean removido = funcionarioRepository.deleteById(id);
        if (!removido) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
