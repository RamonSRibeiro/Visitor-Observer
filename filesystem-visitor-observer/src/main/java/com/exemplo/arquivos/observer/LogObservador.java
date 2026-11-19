package com.exemplo.arquivos.observer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Registra cada evento em um histórico. */
@Component
public class LogObservador implements ObservadorArquivos {

    private final List<String> eventos = new CopyOnWriteArrayList<>();

    @Override
    public void atualizar(EventoArquivos evento) {
        eventos.add(evento.tipo() + " -> " + evento.caminho());
    }

    public List<String> getEventos() {
        return eventos;
    }
}
