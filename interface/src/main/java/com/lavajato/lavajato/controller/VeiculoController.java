package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Veiculo;
import com.lavajato.lavajato.repository.VeiculoRepository;
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
@RequestMapping("/veiculos")
@CrossOrigin("*")
public class VeiculoController {

    private final VeiculoRepository veiculoRepository;

    public VeiculoController(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @GetMapping
    public List<Veiculo> listar() {
        return veiculoRepository.findAll();
    }

    @GetMapping("/{placa}/{idCliente}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable String placa, @PathVariable Integer idCliente) {
        Veiculo veiculo = veiculoRepository.findById(placa, idCliente);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(veiculo);
    }

    @PostMapping
    public ResponseEntity<Veiculo> cadastrar(@RequestBody Veiculo veiculo) {
        veiculo.setPlaca(veiculo.getPlaca().toUpperCase());
        Veiculo veiculoSalvo = veiculoRepository.insert(veiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoSalvo);
    }

    @PutMapping("/{placa}/{idCliente}")
    public ResponseEntity<Veiculo> atualizar(
            @PathVariable String placa,
            @PathVariable Integer idCliente,
            @RequestBody Veiculo veiculo) {
        boolean atualizado = veiculoRepository.update(placa.toUpperCase(), idCliente, veiculo);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(veiculoRepository.findById(placa.toUpperCase(), idCliente));
    }

    @DeleteMapping("/{placa}/{idCliente}")
    public ResponseEntity<Void> remover(@PathVariable String placa, @PathVariable Integer idCliente) {
        boolean removido = veiculoRepository.deleteById(placa.toUpperCase(), idCliente);
        if (!removido) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
