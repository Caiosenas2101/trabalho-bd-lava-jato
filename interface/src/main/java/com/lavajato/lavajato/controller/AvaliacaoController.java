package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Avaliacao;
import com.lavajato.lavajato.repository.AvaliacaoRepository;
import org.springframework.dao.DuplicateKeyException;
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
@RequestMapping("/avaliacoes")
@CrossOrigin("*")
public class AvaliacaoController {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoController(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @GetMapping
    public List<Avaliacao> listar() {
        return avaliacaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscarPorId(@PathVariable Integer id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id);
        if (avaliacao == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avaliacao);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Avaliacao avaliacao) {
        avaliacao.setIdAvaliacao(null);
        try {
            Avaliacao avaliacaoSalva = avaliacaoRepository.insert(avaliacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoSalva);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().body("Este atendimento ja possui avaliacao cadastrada.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizar(@PathVariable Integer id, @RequestBody Avaliacao avaliacao) {
        avaliacao.setIdAvaliacao(id);
        boolean atualizado = avaliacaoRepository.update(avaliacao);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avaliacaoRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        boolean removido = avaliacaoRepository.deleteById(id);
        if (!removido) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
