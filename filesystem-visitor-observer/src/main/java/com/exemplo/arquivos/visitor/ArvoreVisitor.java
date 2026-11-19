package com.exemplo.arquivos.visitor;

import com.exemplo.arquivos.model.Arquivo;
import com.exemplo.arquivos.model.Pasta;

/** Monta uma representação em texto da árvore, com indentação. */
public class ArvoreVisitor implements VisitanteArquivos {

    private final StringBuilder texto = new StringBuilder();
    private int nivel = 0;

    @Override
    public void visitarArquivo(Arquivo arquivo) {
        texto.append("  ".repeat(nivel))
             .append(arquivo.getNome())
             .append(" (").append(arquivo.getTamanhoKb()).append(" KB)\n");
    }

    @Override
    public void visitarPasta(Pasta pasta) {
        texto.append("  ".repeat(nivel)).append(pasta.getNome()).append("/\n");
        nivel++;
        pasta.getFilhos().forEach(filho -> filho.aceitar(this));
        nivel--;
    }

    public String getResultado() {
        return texto.toString();
    }
}
