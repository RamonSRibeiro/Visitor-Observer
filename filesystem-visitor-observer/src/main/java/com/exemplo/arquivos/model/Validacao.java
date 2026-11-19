package com.exemplo.arquivos.model;

final class Validacao {
    private Validacao() {
    }

    static void nome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (nome.contains("/")) {
            throw new IllegalArgumentException("Nome não pode conter '/': " + nome);
        }
    }
}
