package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Cliente;
import com.lavajato.lavajato.model.Servico;
import com.lavajato.lavajato.repository.ClienteRepository;
import com.lavajato.lavajato.repository.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrudControllerTests {

    @Test
    void deveBuscarClientePorId() {
        ClienteController controller = new ClienteController(new ClienteRepositoryFake());

        ResponseEntity<Cliente> resposta = controller.buscarPorId(1);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().getIdCliente());
        assertEquals("Maria", resposta.getBody().getNome());
    }

    @Test
    void deveCadastrarCliente() {
        ClienteController controller = new ClienteController(new ClienteRepositoryFake());

        ResponseEntity<Cliente> resposta = controller.cadastrar(criarCliente());

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().getIdCliente());
    }

    @Test
    void deveAtualizarCliente() {
        ClienteController controller = new ClienteController(new ClienteRepositoryFake());

        ResponseEntity<Cliente> resposta = controller.atualizar(1, criarCliente());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().getIdCliente());
    }

    @Test
    void deveRemoverCliente() {
        ClienteController controller = new ClienteController(new ClienteRepositoryFake());

        ResponseEntity<Void> resposta = controller.remover(1);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
    }

    @Test
    void deveBuscarServicoPorId() {
        ServicoController controller = new ServicoController(new ServicoRepositoryFake());

        ResponseEntity<Servico> resposta = controller.buscarPorId(1);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().getIdServico());
        assertEquals("Lavagem Completa", resposta.getBody().getNomeServico());
    }

    @Test
    void deveCadastrarServico() {
        ServicoController controller = new ServicoController(new ServicoRepositoryFake());

        ResponseEntity<Servico> resposta = controller.cadastrar(criarServico());

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().getIdServico());
    }

    @Test
    void deveAtualizarServico() {
        ServicoController controller = new ServicoController(new ServicoRepositoryFake());

        ResponseEntity<Servico> resposta = controller.atualizar(1, criarServico());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().getIdServico());
    }

    @Test
    void deveRemoverServico() {
        ServicoController controller = new ServicoController(new ServicoRepositoryFake());

        ResponseEntity<Void> resposta = controller.remover(1);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
    }

    private static Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Maria");
        cliente.setCpf("12345678901");
        cliente.setEmail("maria@email.com");
        cliente.setEnderecoRua("Rua A");
        cliente.setEnderecoBairro("Centro");
        cliente.setEnderecoCidade("Recife");
        return cliente;
    }

    private static Servico criarServico() {
        Servico servico = new Servico();
        servico.setIdServico(1);
        servico.setNomeServico("Lavagem Completa");
        servico.setPreco(new BigDecimal("55.00"));
        servico.setTempoMin(60);
        servico.setDescricao("Lavagem detalhada");
        return servico;
    }

    private static class ClienteRepositoryFake extends ClienteRepository {

        ClienteRepositoryFake() {
            super(null);
        }

        @Override
        public List<Cliente> findAll() {
            return List.of(criarCliente());
        }

        @Override
        public Cliente findById(Integer idCliente) {
            return criarCliente();
        }

        @Override
        public Cliente insert(Cliente cliente) {
            return criarCliente();
        }

        @Override
        public boolean update(Cliente cliente) {
            return true;
        }

        @Override
        public boolean deleteById(Integer idCliente) {
            return true;
        }
    }

    private static class ServicoRepositoryFake extends ServicoRepository {

        ServicoRepositoryFake() {
            super(null);
        }

        @Override
        public List<Servico> findAll() {
            return List.of(criarServico());
        }

        @Override
        public Servico findById(Integer idServico) {
            return criarServico();
        }

        @Override
        public Servico insert(Servico servico) {
            return criarServico();
        }

        @Override
        public boolean update(Servico servico) {
            return true;
        }

        @Override
        public boolean deleteById(Integer idServico) {
            return true;
        }
    }
}
