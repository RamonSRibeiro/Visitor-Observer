package com.exemplo.arquivos.service;

import com.exemplo.arquivos.observer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GerenciadorObserverTest {

    private LogObservador log;
    private QuotaObservador quota;
    private GerenciadorArquivos gerenciador;

    @BeforeEach
    void montar() {
        log = new LogObservador();
        quota = new QuotaObservador(100);
        gerenciador = new GerenciadorArquivos(List.of(log, quota));
    }

    // ---------- OBSERVER ----------

    @Test
    void cadaOperacaoGeraUmEventoNaOrdem() {
        gerenciador.criarPasta("raiz", "docs");
        gerenciador.criarArquivo("raiz/docs", "a.txt", 10);
        gerenciador.remover("raiz/docs/a.txt");

        assertEquals(List.of(
                "PASTA_CRIADA -> raiz/docs",
                "ARQUIVO_CRIADO -> raiz/docs/a.txt",
                "ITEM_REMOVIDO -> raiz/docs/a.txt"), log.getEventos());
    }

    @Test
    void todosOsObservadoresSaoNotificados() {
        List<EventoArquivos> a = new ArrayList<>();
        List<EventoArquivos> b = new ArrayList<>();
        ObservadorArquivos obsA = a::add;
        ObservadorArquivos obsB = b::add;
        GerenciadorArquivos g = new GerenciadorArquivos(List.of(obsA, obsB));

        g.criarPasta("raiz", "docs");

        assertEquals(1, a.size());
        assertEquals(1, b.size());
        assertEquals(TipoEvento.PASTA_CRIADA, a.get(0).tipo());
        assertEquals("raiz/docs", b.get(0).caminho());
    }

    @Test
    void eventoCarregaARaizParaInspecao() {
        List<EventoArquivos> recebidos = new ArrayList<>();
        ObservadorArquivos obs = recebidos::add;
        new GerenciadorArquivos(List.of(obs)).criarPasta("raiz", "docs");

        assertEquals("raiz", recebidos.get(0).raiz().getNome());
    }

    @Test
    void operacaoQueFalhaNaoGeraEvento() {
        assertThrows(IllegalArgumentException.class, () -> gerenciador.criarPasta("raiz/nada", "x"));
        assertTrue(log.getEventos().isEmpty());
    }

    // ---------- OBSERVER + VISITOR (quota) ----------

    @Test
    void quotaNaoAlertaAbaixoDoLimite() {
        gerenciador.criarArquivo("raiz", "a.bin", 80);
        assertTrue(quota.getAlertas().isEmpty());
    }

    @Test
    void quotaAlertaQuandoUltrapassaOLimite() {
        gerenciador.criarArquivo("raiz", "a.bin", 80);
        gerenciador.criarArquivo("raiz", "b.bin", 50);

        assertEquals(1, quota.getAlertas().size());
        assertTrue(quota.getAlertas().get(0).contains("130 KB"));
        assertTrue(quota.getAlertas().get(0).contains("quota de 100 KB"));
    }

    @Test
    void quotaNoLimiteExatoNaoAlerta() {
        gerenciador.criarArquivo("raiz", "a.bin", 100);
        assertTrue(quota.getAlertas().isEmpty());
    }

    // ---------- Relatórios (VISITOR) via gerenciador ----------

    @Test
    void relatoriosRefletemAEstruturaAtual() {
        gerenciador.criarPasta("raiz", "docs");
        gerenciador.criarArquivo("raiz/docs", "a.txt", 30);
        gerenciador.criarArquivo("raiz", "b.txt", 20);

        assertEquals(50, gerenciador.tamanhoTotal());
        assertEquals(List.of("raiz/docs/a.txt", "raiz/b.txt"), gerenciador.buscarPorExtensao("txt"));
        assertTrue(gerenciador.arvore().contains("  docs/"));

        gerenciador.remover("raiz/docs");
        assertEquals(20, gerenciador.tamanhoTotal());
    }

    // ---------- Erros ----------

    @Test
    void naoPodeRemoverARaiz() {
        var ex = assertThrows(IllegalArgumentException.class, () -> gerenciador.remover("raiz"));
        assertEquals("Não é possível remover a raiz.", ex.getMessage());
    }

    @Test
    void removerItemInexistenteLancaErro() {
        var ex = assertThrows(IllegalArgumentException.class, () -> gerenciador.remover("raiz/nada.txt"));
        assertEquals("Item não encontrado: raiz/nada.txt", ex.getMessage());
    }

    @Test
    void pastaPaiInexistenteLancaErro() {
        var ex = assertThrows(IllegalArgumentException.class, () -> gerenciador.criarPasta("raiz/nada", "x"));
        assertEquals("Pasta não encontrada: raiz/nada", ex.getMessage());
    }

    @Test
    void caminhoQueNaoComecaNaRaizLancaErro() {
        assertThrows(IllegalArgumentException.class, () -> gerenciador.criarPasta("outra", "x"));
    }

    @Test
    void usarArquivoComoPastaLancaErro() {
        gerenciador.criarArquivo("raiz", "a.txt", 1);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> gerenciador.criarArquivo("raiz/a.txt", "b.txt", 1));
        assertEquals("Não é uma pasta: raiz/a.txt", ex.getMessage());
    }

    @Test
    void itemDuplicadoLancaErro() {
        gerenciador.criarPasta("raiz", "docs");
        assertThrows(IllegalArgumentException.class, () -> gerenciador.criarPasta("raiz", "docs"));
    }
}
