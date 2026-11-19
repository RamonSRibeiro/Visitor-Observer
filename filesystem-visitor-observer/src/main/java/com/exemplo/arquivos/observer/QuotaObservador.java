package com.exemplo.arquivos.observer;

import com.exemplo.arquivos.visitor.TamanhoVisitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Integra OBSERVER + VISITOR: a cada evento, usa o TamanhoVisitor
 * para conferir se o total passou da quota.
 */
@Component
public class QuotaObservador implements ObservadorArquivos {

    private final long limiteKb;
    private final List<String> alertas = new CopyOnWriteArrayList<>();

    public QuotaObservador(@Value("${app.quota-kb:1000}") long limiteKb) {
        this.limiteKb = limiteKb;
    }

    @Override
    public void atualizar(EventoArquivos evento) {
        TamanhoVisitor visitor = new TamanhoVisitor();
        evento.raiz().aceitar(visitor);

        if (visitor.getTotal() > limiteKb) {
            alertas.add("ALERTA: " + visitor.getTotal() + " KB excede a quota de " + limiteKb
                    + " KB (após " + evento.tipo() + " em " + evento.caminho() + ")");
        }
    }

    public List<String> getAlertas() {
        return alertas;
    }
}
