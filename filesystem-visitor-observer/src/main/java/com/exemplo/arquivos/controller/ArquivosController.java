package com.exemplo.arquivos.controller;

import com.exemplo.arquivos.observer.LogObservador;
import com.exemplo.arquivos.observer.QuotaObservador;
import com.exemplo.arquivos.service.GerenciadorArquivos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ArquivosController {

    private final GerenciadorArquivos gerenciador;
    private final LogObservador log;
    private final QuotaObservador quota;

    public ArquivosController(GerenciadorArquivos gerenciador, LogObservador log, QuotaObservador quota) {
        this.gerenciador = gerenciador;
        this.log = log;
        this.quota = quota;
    }

    // ---- operações (geram eventos) ----

    @PostMapping("/pastas")
    @ResponseStatus(HttpStatus.CREATED)
    public void criarPasta(@RequestBody CriarPastaRequest req) {
        gerenciador.criarPasta(req.caminhoPai(), req.nome());
    }

    @PostMapping("/arquivos")
    @ResponseStatus(HttpStatus.CREATED)
    public void criarArquivo(@RequestBody CriarArquivoRequest req) {
        gerenciador.criarArquivo(req.caminhoPasta(), req.nome(), req.tamanhoKb());
    }

    @DeleteMapping("/itens")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@RequestParam String caminho) {
        gerenciador.remover(caminho);
    }

    // ---- relatórios (visitantes) ----

    @GetMapping(value = "/arvore", produces = "text/plain;charset=UTF-8")
    public String arvore() {
        return gerenciador.arvore();
    }

    @GetMapping("/tamanho")
    public Map<String, Long> tamanho() {
        return Map.of("tamanhoKb", gerenciador.tamanhoTotal());
    }

    @GetMapping("/busca")
    public List<String> buscar(@RequestParam String extensao) {
        return gerenciador.buscarPorExtensao(extensao);
    }

    // ---- dados coletados pelos observadores ----

    @GetMapping("/eventos")
    public List<String> eventos() {
        return log.getEventos();
    }

    @GetMapping("/alertas")
    public List<String> alertas() {
        return quota.getAlertas();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> erro(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}
