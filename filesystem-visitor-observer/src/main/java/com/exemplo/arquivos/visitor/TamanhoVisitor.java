package com.exemplo.arquivos.visitor;

import com.exemplo.arquivos.model.Arquivo;
import com.exemplo.arquivos.model.Pasta;

/** Soma o tamanho de todos os arquivos da estrutura. */
public class TamanhoVisitor implements VisitanteArquivos {

    private long total;

    @Override
    public void visitarArquivo(Arquivo arquivo) {
        total += arquivo.getTamanhoKb();
    }

    @Override
    public void visitarPasta(Pasta pasta) {
        pasta.getFilhos().forEach(filho -> filho.aceitar(this));
    }

    public long getTotal() {
        return total;
    }
}
