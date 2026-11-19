package com.exemplo.arquivos.service;

import com.exemplo.arquivos.model.Arquivo;
import com.exemplo.arquivos.model.ItemArquivo;
import com.exemplo.arquivos.model.Pasta;
import com.exemplo.arquivos.observer.EventoArquivos;
import com.exemplo.arquivos.observer.ObservadorArquivos;
import com.exemplo.arquivos.observer.TipoEvento;
import com.exemplo.arquivos.visitor.ArvoreVisitor;
import com.exemplo.arquivos.visitor.BuscaExtensaoVisitor;
import com.exemplo.arquivos.visitor.TamanhoVisitor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * - É o SUBJECT do Observer: notifica os observadores a cada mudança.
 * - É o cliente do Visitor: roda visitantes sobre a árvore para gerar relatórios.
 */
@Service
public class GerenciadorArquivos {

    private final Pasta raiz = new Pasta("raiz");
    private final List<ObservadorArquivos> observadores; // o Spring injeta todos os @Component que implementam a interface

    public GerenciadorArquivos(List<ObservadorArquivos> observadores) {
        this.observadores = observadores;
    }

    // ---------- operações que disparam eventos (OBSERVER) ----------

    public synchronized void criarPasta(String caminhoPai, String nome) {
        Pasta pai = buscarPasta(caminhoPai);
        pai.adicionar(new Pasta(nome));
        notificar(TipoEvento.PASTA_CRIADA, caminhoPai + "/" + nome);
    }

    public synchronized void criarArquivo(String caminhoPasta, String nome, long tamanhoKb) {
        Pasta pasta = buscarPasta(caminhoPasta);
        pasta.adicionar(new Arquivo(nome, tamanhoKb));
        notificar(TipoEvento.ARQUIVO_CRIADO, caminhoPasta + "/" + nome);
    }

    public synchronized void remover(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("Caminho é obrigatório.");
        }
        if (caminho.equals(raiz.getNome())) {
            throw new IllegalArgumentException("Não é possível remover a raiz.");
        }
        int i = caminho.lastIndexOf('/');
        if (i < 0) {
            throw new IllegalArgumentException("Caminho inválido: " + caminho);
        }
        Pasta pai = buscarPasta(caminho.substring(0, i));
        if (!pai.remover(caminho.substring(i + 1))) {
            throw new IllegalArgumentException("Item não encontrado: " + caminho);
        }
        notificar(TipoEvento.ITEM_REMOVIDO, caminho);
    }

    // ---------- relatórios (VISITOR) ----------

    public synchronized long tamanhoTotal() {
        TamanhoVisitor visitor = new TamanhoVisitor();
        raiz.aceitar(visitor);
        return visitor.getTotal();
    }

    public synchronized String arvore() {
        ArvoreVisitor visitor = new ArvoreVisitor();
        raiz.aceitar(visitor);
        return visitor.getResultado();
    }

    public synchronized List<String> buscarPorExtensao(String extensao) {
        BuscaExtensaoVisitor visitor = new BuscaExtensaoVisitor(extensao);
        raiz.aceitar(visitor);
        return visitor.getResultados();
    }

    // ---------- internos ----------

    private void notificar(TipoEvento tipo, String caminho) {
        EventoArquivos evento = new EventoArquivos(tipo, caminho, raiz);
        observadores.forEach(o -> o.atualizar(evento));
    }

    private Pasta buscarPasta(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("Caminho é obrigatório.");
        }
        String[] partes = caminho.split("/");
        if (!partes[0].equals(raiz.getNome())) {
            throw new IllegalArgumentException("Pasta não encontrada: " + caminho);
        }
        Pasta atual = raiz;
        for (int i = 1; i < partes.length; i++) {
            ItemArquivo filho = atual.buscarFilho(partes[i])
                    .orElseThrow(() -> new IllegalArgumentException("Pasta não encontrada: " + caminho));
            if (!(filho instanceof Pasta pasta)) {
                throw new IllegalArgumentException("Não é uma pasta: " + caminho);
            }
            atual = pasta;
        }
        return atual;
    }
}
