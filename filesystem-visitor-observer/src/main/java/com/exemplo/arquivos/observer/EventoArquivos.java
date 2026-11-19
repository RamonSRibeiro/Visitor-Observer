package com.exemplo.arquivos.observer;

import com.exemplo.arquivos.model.Pasta;

/** O evento carrega a raiz para que os observadores possam inspecionar a estrutura (com visitantes). */
public record EventoArquivos(TipoEvento tipo, String caminho, Pasta raiz) {
}
