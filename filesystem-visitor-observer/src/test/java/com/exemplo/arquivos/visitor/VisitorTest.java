package com.exemplo.arquivos.visitor;

import com.exemplo.arquivos.model.Arquivo;
import com.exemplo.arquivos.model.Pasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {

    private Pasta raiz;

    /*
     * raiz/
     *   docs/
     *     a.txt (100)
     *     b.pdf (200)
     *   c.txt (50)
     */
    @BeforeEach
    void montarEstrutura() {
        raiz = new Pasta("raiz");
        Pasta docs = new Pasta("docs");
        docs.adicionar(new Arquivo("a.txt", 100));
        docs.adicionar(new Arquivo("b.pdf", 200));
        raiz.adicionar(docs);
        raiz.adicionar(new Arquivo("c.txt", 50));
    }

    // ---------- TamanhoVisitor ----------

    @Test
    void tamanhoSomaTodosOsArquivosDaArvore() {
        TamanhoVisitor v = new TamanhoVisitor();
        raiz.aceitar(v);
        assertEquals(350, v.getTotal());
    }

    @Test
    void tamanhoDePastaVaziaEhZero() {
        TamanhoVisitor v = new TamanhoVisitor();
        new Pasta("vazia").aceitar(v);
        assertEquals(0, v.getTotal());
    }

    @Test
    void tamanhoDeUmArquivoSoEhOTamanhoDele() {
        TamanhoVisitor v = new TamanhoVisitor();
        new Arquivo("x.bin", 42).aceitar(v);
        assertEquals(42, v.getTotal());
    }

    // ---------- BuscaExtensaoVisitor ----------

    @Test
    void buscaRetornaCaminhoCompletoDosArquivos() {
        BuscaExtensaoVisitor v = new BuscaExtensaoVisitor("txt");
        raiz.aceitar(v);
        assertEquals(List.of("raiz/docs/a.txt", "raiz/c.txt"), v.getResultados());
    }

    @Test
    void buscaIgnoraMaiusculasEPontoInicial() {
        BuscaExtensaoVisitor v = new BuscaExtensaoVisitor(".TXT");
        raiz.aceitar(v);
        assertEquals(2, v.getResultados().size());
    }

    @Test
    void buscaSemResultadosRetornaListaVazia() {
        BuscaExtensaoVisitor v = new BuscaExtensaoVisitor("zip");
        raiz.aceitar(v);
        assertTrue(v.getResultados().isEmpty());
    }

    // ---------- ArvoreVisitor ----------

    @Test
    void arvoreEhIndentadaPorNivel() {
        ArvoreVisitor v = new ArvoreVisitor();
        raiz.aceitar(v);

        String esperado = String.join("\n",
                "raiz/",
                "  docs/",
                "    a.txt (100 KB)",
                "    b.pdf (200 KB)",
                "  c.txt (50 KB)") + "\n";
        assertEquals(esperado, v.getResultado());
    }

    // ---------- Visitantes independentes sobre a mesma estrutura ----------

    @Test
    void variosVisitantesPodemPercorrerAMesmaEstruturaSemAltera_la() {
        TamanhoVisitor tamanho = new TamanhoVisitor();
        ArvoreVisitor arvore = new ArvoreVisitor();
        raiz.aceitar(tamanho);
        raiz.aceitar(arvore);

        assertEquals(350, tamanho.getTotal());
        assertTrue(arvore.getResultado().contains("b.pdf"));
        assertEquals(2, raiz.getFilhos().size()); // estrutura intacta
    }

    // ---------- Modelo ----------

    @Test
    void extensaoEhCalculadaDoNome() {
        assertEquals("gz", new Arquivo("backup.tar.gz", 1).getExtensao());
        assertEquals("", new Arquivo("README", 1).getExtensao());
        assertEquals("", new Arquivo("arquivo.", 1).getExtensao());
        assertEquals("txt", new Arquivo("NOTAS.TXT", 1).getExtensao());
    }

    @Test
    void nomesInvalidosSaoRejeitados() {
        assertThrows(IllegalArgumentException.class, () -> new Arquivo("a/b.txt", 1));
        assertThrows(IllegalArgumentException.class, () -> new Arquivo(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> new Pasta(""));
        assertThrows(IllegalArgumentException.class, () -> new Pasta(null));
    }

    @Test
    void tamanhoNegativoEhRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> new Arquivo("a.txt", -1));
    }

    @Test
    void nomeDuplicadoNaMesmaPastaEhRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> raiz.adicionar(new Arquivo("c.txt", 1)));
    }
}
