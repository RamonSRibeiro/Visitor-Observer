package com.exemplo.arquivos.visitor;

import com.exemplo.arquivos.model.Arquivo;
import com.exemplo.arquivos.model.Pasta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Encontra o caminho completo de todos os arquivos com determinada extensão. */
public class BuscaExtensaoVisitor implements VisitanteArquivos {

    private final String extensao;
    private final Deque<String> caminho = new ArrayDeque<>();
    private final List<String> resultados = new ArrayList<>();

    public BuscaExtensaoVisitor(String extensao) {
        String e = extensao == null ? "" : extensao.toLowerCase();
        this.extensao = e.startsWith(".") ? e.substring(1) : e;
    }

    @Override
    public void visitarArquivo(Arquivo arquivo) {
        if (arquivo.getExtensao().equals(extensao)) {
            resultados.add(String.join("/", caminho) + "/" + arquivo.getNome());
        }
    }

    @Override
    public void visitarPasta(Pasta pasta) {
        caminho.addLast(pasta.getNome());
        pasta.getFilhos().forEach(filho -> filho.aceitar(this));
        caminho.removeLast();
    }

    public List<String> getResultados() {
        return resultados;
    }
}
