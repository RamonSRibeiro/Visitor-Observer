package com.exemplo.arquivos.model;

import com.exemplo.arquivos.visitor.VisitanteArquivos;

public class Arquivo implements ItemArquivo {

    private final String nome;
    private final long tamanhoKb;

    public Arquivo(String nome, long tamanhoKb) {
        Validacao.nome(nome);
        if (tamanhoKb < 0) {
            throw new IllegalArgumentException("Tamanho não pode ser negativo.");
        }
        this.nome = nome;
        this.tamanhoKb = tamanhoKb;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public long getTamanhoKb() {
        return tamanhoKb;
    }

    /** Extensão em minúsculas, sem o ponto. Vazia se não houver. */
    public String getExtensao() {
        int i = nome.lastIndexOf('.');
        return (i < 0 || i == nome.length() - 1) ? "" : nome.substring(i + 1).toLowerCase();
    }

    @Override
    public void aceitar(VisitanteArquivos visitante) {
        visitante.visitarArquivo(this);
    }
}
