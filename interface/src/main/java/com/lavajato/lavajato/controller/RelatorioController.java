package com.lavajato.lavajato.controller;

import com.lavajato.lavajato.repository.RelatorioRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin("*")
public class RelatorioController {

    private final RelatorioRepository relatorioRepository;

    public RelatorioController(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    @GetMapping("/consulta-faturamento")
    public Object servicosComFaturamentoMinimo(
            @RequestParam(defaultValue = "60") BigDecimal minimo) {
        return relatorioRepository.servicosComFaturamentoMinimo(minimo);
    }

    @GetMapping("/consulta-atendimentos-periodo")
    public Object atendimentosFinalizadosPorPeriodo(
            @RequestParam(defaultValue = "2026-04-10")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-20")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioRepository.atendimentosFinalizadosPorPeriodo(inicio, fim);
    }

    @GetMapping("/consulta-atendimentos-sem-pagamento")
    public Object atendimentosSemPagamento() {
        return relatorioRepository.atendimentosSemPagamento();
    }

    @GetMapping("/consulta-clientes-acima-media")
    public Object clientesComAvaliacaoAcimaDaMedia() {
        return relatorioRepository.clientesComAvaliacaoAcimaDaMedia();
    }

    @GetMapping("/view-atendimentos-finalizados")
    public Object viewAtendimentosFinalizados() {
        return relatorioRepository.viewAtendimentosFinalizados();
    }

    @GetMapping("/view-clientes-boas-avaliacoes")
    public Object viewClientesComBoasAvaliacoes() {
        return relatorioRepository.viewClientesComBoasAvaliacoes();
    }

    @GetMapping("/funcao-valor-liquido")
    public Object valorLiquidoAtendimento(@RequestParam Integer idAtendimento) {
        return Map.of(
                "idAtendimento", idAtendimento,
                "valorLiquido", relatorioRepository.valorLiquidoAtendimento(idAtendimento));
    }

    @GetMapping("/funcao-situacao-avaliacao")
    public Object situacaoAvaliacao(@RequestParam Integer nota) {
        return Map.of(
                "nota", nota,
                "situacao", relatorioRepository.situacaoAvaliacao(nota));
    }

    @GetMapping("/avaliacao-atendimento")
    public Object avaliacaoDoAtendimento(@RequestParam Integer idAtendimento) {
        return relatorioRepository.avaliacaoDoAtendimento(idAtendimento);
    }

    @PostMapping("/procedimento-atualizar-status")
    public Object atualizarStatusAtendimento(
            @RequestParam Integer idAtendimento,
            @RequestParam String status) {
        relatorioRepository.atualizarStatusAtendimento(idAtendimento, status);
        return Map.of("mensagem", "Status atualizado com sucesso.");
    }

    @PostMapping("/procedimento-recalcular-pagamentos")
    public Object recalcularPagamentosFinalizados() {
        relatorioRepository.recalcularPagamentosFinalizados();
        return Map.of("mensagem", "Pagamentos finalizados recalculados com sucesso.");
    }

    @GetMapping("/logs")
    public Object logs() {
        return relatorioRepository.logs();
    }

    @GetMapping("/dashboard")
    public Object indicadoresDashboard() {
        return relatorioRepository.indicadoresDashboard();
    }

    @GetMapping("/opcoes-veiculos")
    public Object veiculosParaAtendimento() {
        return relatorioRepository.veiculosParaAtendimento();
    }

    // =========================================================
    // DASHBOARD ESTATISTICO - ENDPOINTS
    // =========================================================

    @GetMapping("/dashboard-resumo")
    public Object resumoDashboard(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioRepository.resumoDashboard(inicio, fim);
    }

    @GetMapping("/dashboard-faturamento-servico")
    public Object faturamentoPorServico(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "10") Integer limite) {
        return relatorioRepository.faturamentoPorServico(inicio, fim, limite);
    }

    @GetMapping("/dashboard-status-atendimento")
    public Object atendimentosPorStatus(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioRepository.atendimentosPorStatus(inicio, fim);
    }

    @GetMapping("/dashboard-tendencia")
    public Object tendenciaTemporal(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "dia") String granularidade) {
        return relatorioRepository.tendenciaTemporal(inicio, fim, granularidade);
    }

    @GetMapping("/dashboard-distribuicao-notas")
    public Object distribuicaoNotas(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioRepository.distribuicaoNotas(inicio, fim);
    }

    @GetMapping("/dashboard-formas-pagamento")
    public Object formasPagamento(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioRepository.formasPagamento(inicio, fim);
    }

    @GetMapping("/dashboard-top-clientes")
    public Object topClientes(
            @RequestParam(defaultValue = "2026-04-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(defaultValue = "2026-04-30")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "10") Integer limite) {
        return relatorioRepository.topClientes(inicio, fim, limite);
    }
}
