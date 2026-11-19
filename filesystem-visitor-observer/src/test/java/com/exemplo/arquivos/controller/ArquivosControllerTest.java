package com.exemplo.arquivos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// O gerenciador é um singleton com estado: recria o contexto a cada teste para começar do zero.
@SpringBootTest(properties = "app.quota-kb=100")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ArquivosControllerTest {

    @Autowired
    private MockMvc mvc;

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private void pasta(String pai, String nome) throws Exception {
        postJson("/pastas", "{\"caminhoPai\":\"" + pai + "\",\"nome\":\"" + nome + "\"}")
                .andExpect(status().isCreated());
    }

    private void arquivo(String pasta, String nome, long kb) throws Exception {
        postJson("/arquivos", "{\"caminhoPasta\":\"" + pasta + "\",\"nome\":\"" + nome + "\",\"tamanhoKb\":" + kb + "}")
                .andExpect(status().isCreated());
    }

    @Test
    void fluxoCompletoComVisitantesEObservadores() throws Exception {
        pasta("raiz", "docs");
        arquivo("raiz/docs", "a.txt", 40);

        mvc.perform(get("/tamanho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tamanhoKb").value(40));

        mvc.perform(get("/busca").param("extensao", "txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("raiz/docs/a.txt"));

        mvc.perform(get("/arvore"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("a.txt (40 KB)")));

        mvc.perform(get("/eventos"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("PASTA_CRIADA -> raiz/docs"))
                .andExpect(jsonPath("$[1]").value("ARQUIVO_CRIADO -> raiz/docs/a.txt"));
    }

    @Test
    void quotaGeraAlertaAoUltrapassarOLimite() throws Exception {
        arquivo("raiz", "a.bin", 80);
        mvc.perform(get("/alertas")).andExpect(jsonPath("$", hasSize(0)));

        arquivo("raiz", "b.bin", 50);
        mvc.perform(get("/alertas"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(containsString("130 KB")));
    }

    @Test
    void removerAtualizaRelatoriosEGeraEvento() throws Exception {
        arquivo("raiz", "a.txt", 30);

        mvc.perform(delete("/itens").param("caminho", "raiz/a.txt"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/tamanho")).andExpect(jsonPath("$.tamanhoKb").value(0));
        mvc.perform(get("/eventos"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1]").value("ITEM_REMOVIDO -> raiz/a.txt"));
    }

    @Test
    void pastaPaiInexistenteRetorna400() throws Exception {
        postJson("/pastas", "{\"caminhoPai\":\"raiz/nada\",\"nome\":\"x\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Pasta não encontrada: raiz/nada"));
    }

    @Test
    void itemDuplicadoRetorna400() throws Exception {
        pasta("raiz", "docs");
        postJson("/pastas", "{\"caminhoPai\":\"raiz\",\"nome\":\"docs\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void removerRaizRetorna400() throws Exception {
        mvc.perform(delete("/itens").param("caminho", "raiz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Não é possível remover a raiz."));
    }

    @Test
    void erroNaoGeraEventoNemAlerta() throws Exception {
        postJson("/pastas", "{\"caminhoPai\":\"raiz/nada\",\"nome\":\"x\"}").andExpect(status().isBadRequest());

        mvc.perform(get("/eventos")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/alertas")).andExpect(jsonPath("$", hasSize(0)));
    }
}
