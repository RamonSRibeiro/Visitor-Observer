# Exercício: Visitor + Observer
Tema: **Sistema de arquivos e pastas** (Java 17 + Spring Boot 3)

## Objetivo
Mostrar como adicionar operações novas a uma estrutura (Visitor) e como reagir
automaticamente a mudanças nela (Observer), com os dois padrões trabalhando juntos.

## Como funciona
```
Requisição ──► GerenciadorArquivos ──► altera a árvore ──► notifica OBSERVADORES
                     │                                          │
                     │                                          ├─ LogObservador   (histórico)
                     ▼                                          └─ QuotaObservador (usa um VISITOR)
              roda VISITANTES para relatórios
              ├─ TamanhoVisitor
              ├─ BuscaExtensaoVisitor
              └─ ArvoreVisitor
```

| Padrão | Pasta | Papel |
|---|---|---|
| Visitor | `visitor/` + `model/` | `Arquivo` e `Pasta` só implementam `aceitar()`. Cada operação nova é uma classe nova |
| Observer | `observer/` | Observadores reagem a eventos de criar/remover. Basta um `@Component` para registrar |
| Integração | `QuotaObservador` | A cada evento, roda o `TamanhoVisitor` e alerta se passar da quota |

## Endpoints
| Método | Rota | O que faz |
|---|---|---|
| POST | `/pastas` | `{"caminhoPai":"raiz","nome":"docs"}` |
| POST | `/arquivos` | `{"caminhoPasta":"raiz/docs","nome":"a.txt","tamanhoKb":40}` |
| DELETE | `/itens?caminho=raiz/docs/a.txt` | remove arquivo ou pasta |
| GET | `/arvore` | árvore em texto (ArvoreVisitor) |
| GET | `/tamanho` | tamanho total (TamanhoVisitor) |
| GET | `/busca?extensao=txt` | caminhos dos arquivos (BuscaExtensaoVisitor) |
| GET | `/eventos` | histórico (LogObservador) |
| GET | `/alertas` | alertas de quota (QuotaObservador) |

A quota é configurada em `application.properties` (`app.quota-kb`, padrão 1000).

## Executar e testar
```
mvn spring-boot:run
mvn test
```

# Visitor-Observer
