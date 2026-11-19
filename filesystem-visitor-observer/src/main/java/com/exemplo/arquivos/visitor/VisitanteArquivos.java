package com.exemplo.arquivos.visitor;

import com.exemplo.arquivos.model.Arquivo;
import com.exemplo.arquivos.model.Pasta;

/**
 * VISITOR
 * Cada operação nova sobre a estrutura vira um novo visitante,
 * sem precisar alterar as classes Arquivo e Pasta.
 */
public interface VisitanteArquivos {
    void visitarArquivo(Arquivo arquivo);

    void visitarPasta(Pasta pasta);
}
