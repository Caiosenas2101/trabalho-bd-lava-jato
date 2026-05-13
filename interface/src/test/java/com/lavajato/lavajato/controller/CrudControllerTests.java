package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.model.Cliente;
import com.lavajato.lavajato.model.Servico;
import com.lavajato.lavajato.repository.ClienteRepository;
import com.lavajato.lavajato.repository.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrudControllerTests {

    @Test
    void deveBuscarClientePorId() throws Exception {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.findById(1)).thenReturn(criarCliente());

        MockMvc mockMvc = criarMockMvc(new ClienteController(repository));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void deveCadastrarCliente() throws Exception {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.insert(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(criarCliente());

        MockMvc mockMvc = criarMockMvc(new ClienteController(repository));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria",
                                  "cpf": "12345678901",
                                  "email": "maria@email.com",
                                  "enderecoRua": "Rua A",
                                  "enderecoBairro": "Centro",
                                  "enderecoCidade": "Recife"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(1));
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.update(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(true);
        when(repository.findById(1)).thenReturn(criarCliente());

        MockMvc mockMvc = criarMockMvc(new ClienteController(repository));

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria",
                                  "cpf": "12345678901",
                                  "email": "maria@email.com",
                                  "enderecoRua": "Rua A",
                                  "enderecoBairro": "Centro",
                                  "enderecoCidade": "Recife"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1));
    }

    @Test
    void deveRemoverCliente() throws Exception {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.deleteById(1)).thenReturn(true);

        MockMvc mockMvc = criarMockMvc(new ClienteController(repository));

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarServicoPorId() throws Exception {
        ServicoRepository repository = mock(ServicoRepository.class);
        when(repository.findById(1)).thenReturn(criarServico());

        MockMvc mockMvc = criarMockMvc(new ServicoController(repository));

        mockMvc.perform(get("/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idServico").value(1))
                .andExpect(jsonPath("$.nomeServico").value("Lavagem Completa"));
    }

    @Test
    void deveCadastrarServico() throws Exception {
        ServicoRepository repository = mock(ServicoRepository.class);
        when(repository.insert(org.mockito.ArgumentMatchers.any(Servico.class))).thenReturn(criarServico());

        MockMvc mockMvc = criarMockMvc(new ServicoController(repository));

        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeServico": "Lavagem Completa",
                                  "preco": 55.00,
                                  "tempoMin": 60,
                                  "descricao": "Lavagem detalhada"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idServico").value(1));
    }

    @Test
    void deveAtualizarServico() throws Exception {
        ServicoRepository repository = mock(ServicoRepository.class);
        when(repository.update(org.mockito.ArgumentMatchers.any(Servico.class))).thenReturn(true);
        when(repository.findById(1)).thenReturn(criarServico());

        MockMvc mockMvc = criarMockMvc(new ServicoController(repository));

        mockMvc.perform(put("/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeServico": "Lavagem Completa",
                                  "preco": 55.00,
                                  "tempoMin": 60,
                                  "descricao": "Lavagem detalhada"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idServico").value(1));
    }

    @Test
    void deveRemoverServico() throws Exception {
        ServicoRepository repository = mock(ServicoRepository.class);
        when(repository.deleteById(1)).thenReturn(true);

        MockMvc mockMvc = criarMockMvc(new ServicoController(repository));

        mockMvc.perform(delete("/servicos/1"))
                .andExpect(status().isNoContent());
    }

    private MockMvc criarMockMvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    private Cliente criarCliente() {
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

    private Servico criarServico() {
        Servico servico = new Servico();
        servico.setIdServico(1);
        servico.setNomeServico("Lavagem Completa");
        servico.setPreco(new BigDecimal("55.00"));
        servico.setTempoMin(60);
        servico.setDescricao("Lavagem detalhada");
        return servico;
    }
}
