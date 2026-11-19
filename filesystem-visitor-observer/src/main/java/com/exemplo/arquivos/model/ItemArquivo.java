package com.exemplo.arquivos.model;

import com.exemplo.arquivos.visitor.VisitanteArquivos;

/**
 * Elemento visitável (VISITOR).
 * aceitar() é o primeiro "despacho"; o visitante escolhe o método certo (segundo despacho).
 */
public interface ItemArquivo {
    String getNome();

    void aceitar(VisitanteArquivos visitante);
}
