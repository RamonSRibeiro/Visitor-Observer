package com.exemplo.arquivos.observer;

/**
 * OBSERVER
 * Quem quiser reagir a mudanças no sistema de arquivos implementa esta interface.
 * Com Spring, basta anotar a classe com @Component: ela é registrada automaticamente.
 */
@FunctionalInterface
public interface ObservadorArquivos {
    void atualizar(EventoArquivos evento);
}
