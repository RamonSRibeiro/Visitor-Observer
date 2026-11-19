package com.exemplo.arquivos.model;

import com.exemplo.arquivos.visitor.VisitanteArquivos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Pasta implements ItemArquivo {

    private final String nome;
    private final List<ItemArquivo> filhos = new ArrayList<>();

    public Pasta(String nome) {
        Validacao.nome(nome);
        this.nome = nome;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public List<ItemArquivo> getFilhos() {
        return Collections.unmodifiableList(filhos);
    }

    public void adicionar(ItemArquivo item) {
        if (buscarFilho(item.getNome()).isPresent()) {
            throw new IllegalArgumentException("Já existe um item chamado '" + item.getNome() + "' em '" + nome + "'.");
        }
        filhos.add(item);
    }

    public boolean remover(String nomeFilho) {
        return filhos.removeIf(f -> f.getNome().equals(nomeFilho));
    }

    public Optional<ItemArquivo> buscarFilho(String nomeFilho) {
        return filhos.stream().filter(f -> f.getNome().equals(nomeFilho)).findFirst();
    }

    @Override
    public void aceitar(VisitanteArquivos visitante) {
        visitante.visitarPasta(this);
    }
}
