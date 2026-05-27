package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Atendimento;
import com.lavajato.lavajato.repository.AtendimentoRepository;
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
@RequestMapping("/atendimentos")
@CrossOrigin("*")
public class AtendimentoController {

    private final AtendimentoRepository atendimentoRepository;

    public AtendimentoController(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    @GetMapping
    public List<Atendimento> listar() {
        return atendimentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarPorId(@PathVariable Integer id) {
        Atendimento atendimento = atendimentoRepository.findById(id);
        if (atendimento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(atendimento);
    }

    @PostMapping
    public ResponseEntity<Atendimento> cadastrar(@RequestBody Atendimento atendimento) {
        atendimento.setIdAtendimento(null);
        Atendimento atendimentoSalvo = atendimentoRepository.insert(atendimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atendimento> atualizar(@PathVariable Integer id, @RequestBody Atendimento atendimento) {
        atendimento.setIdAtendimento(id);
        boolean atualizado = atendimentoRepository.update(atendimento);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(atendimentoRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        boolean removido = atendimentoRepository.deleteById(id);
        if (!removido) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
