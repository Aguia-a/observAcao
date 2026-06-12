# ObservAção
Sistema de abertura e acompanhamento de solicitações de serviços públicos.  
Projeto da AEP - 5º semestre de Engenharia de Software, UniCesumar.

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 21 · Spring Boot 3.4.5 |
| Persistência | H2 (arquivo, pasta `persistence/`) |
| Frontend | HTML · CSS · JavaScript (sem frameworks) |
| Documentação | SpringDoc OpenAPI 3 (Swagger UI) |

---

## Pré-requisitos

> [!WARNING]
> **Requisitos obrigatórios**

- **Java JDK 21** - preferencialmente Temurin 21. Configure `JAVA_HOME` ou certifique-se de que `java` na linha de comando aponte para o JDK 21.
- **Apache Maven 3.9.6+**

---

## Como executar

### Via Maven (terminal)

```bash
# Compilar e executar
mvn spring-boot:run

# Apenas compilar
mvn clean compile

# Executar testes
mvn test
```

### Via IDE (VS Code / IntelliJ)

Abra e execute:

```
src/main/java/org/aep/observacao/ObservacaoApplication.java
```

A aplicação sobe em **`http://localhost:8080`**.

> [!NOTE]
> Na primeira execução a IDE fará download das dependências Maven. Pode demorar alguns minutos.

### Interface CLI (legado)

A interface de linha de comando original ainda está disponível:

```
src/main/java/org/aep/observacao/ui/Main.java
```

---

## Interface Web

Após subir a aplicação, acesse `http://localhost:8080/`.

| Página | Arquivo | Descrição |
|--------|---------|-----------|
| Início | `index.html` | Seleção de perfil (Cidadão / Servidor) |
| Área do Cidadão | `cidadao.html` | Menu do cidadão |
| Novo Chamado | `novo-chamado.html` | Abertura de solicitação |
| Consultar Chamado | `consultar.html` | Busca por protocolo + histórico de status |
| Meus Chamados | `meus-chamados.html` | Lista de chamados do cidadão por e-mail |
| Área do Servidor | `servidor.html` | Menu do servidor público |
| Painel de Chamados | `painel.html` | Lista e filtragem de todas as solicitações |
| Detalhes / Atualizar | `detalhes.html` | Detalhes completos + atualização de status |
| Histórico | `historico.html` | Chamados concluídos (Resolvido / Encerrado) |

---

## API REST

**Base URL:** `http://localhost:8080/api`

### Enumerações

| Tipo | Valores |
|------|---------|
| `Prioridade` | `BAIXA` · `MEDIA` · `ALTA` |
| `Status` | `ABERTO` · `TRIAGEM` · `EM_EXECUCAO` · `RESOLVIDO` · `ENCERRADO` |

### Categorias padrão

| Nome | SLA (dias) |
|------|-----------|
| Iluminação | 7 |
| Buraco | 5 |
| Limpeza | 3 |
| Saúde | 1 |
| Segurança Escolar | 2 |

---

### `POST /api/solicitacoes`

Cria uma nova solicitação.

**Request body** (`application/json`):

```json
{
  "categoriaNome": "Iluminação",
  "categoriaSlaDias": 7,
  "descricao": "Poste apagado na Rua das Flores",
  "localizacao": "Jardim América",
  "prioridade": "ALTA",
  "anonimo": false,
  "usuario": {
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "(44) 99999-0000"
  }
}
```

> Para envio anônimo, use `"anonimo": true` e omita ou envie `"usuario": null`.

**Validações:**
- `categoriaNome`, `descricao`, `localizacao` - obrigatórios e não-vazios
- `categoriaSlaDias` - mínimo 1
- `prioridade` - obrigatório; valor do enum `Prioridade`
- Se `anonimo: false`, `usuario.nome`, `usuario.email` e `usuario.telefone` são obrigatórios

**Responses:**

| Status | Descrição |
|--------|-----------|
| `201 Created` | Solicitação criada; retorna `SolicitacaoResponse` |
| `400 Bad Request` | Corpo inválido ou campos obrigatórios ausentes |

**Response body (`201`):**

```json
{
  "id": 1,
  "protocolo": "SOL-123456",
  "categoria": "Iluminação",
  "descricao": "Poste apagado na Rua das Flores",
  "localizacao": "Jardim América",
  "prioridade": "ALTA",
  "status": "ABERTO",
  "dataCriacao": "2026-06-11T10:30:00",
  "anonimo": false,
  "usuario": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "(44) 99999-0000"
  }
}
```

---

### `GET /api/solicitacoes`

Lista solicitações com filtros opcionais.

**Query parameters (todos opcionais):**

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `prioridade` | `Prioridade` | Filtra por prioridade (`BAIXA`, `MEDIA`, `ALTA`) |
| `bairro` | `string` | Filtra por correspondência parcial na localização |
| `categoriaNome` | `string` | Filtra por nome exato da categoria |

**Exemplos:**

```
GET /api/solicitacoes
GET /api/solicitacoes?prioridade=ALTA
GET /api/solicitacoes?bairro=Jardim América
GET /api/solicitacoes?categoriaNome=Iluminação
GET /api/solicitacoes?prioridade=MEDIA&categoriaNome=Buraco
```

**Response `200 OK`:** array de `SolicitacaoResponse` (ver schema acima).

---

### `GET /api/solicitacoes/{protocolo}`

Busca uma solicitação pelo número de protocolo.

**Path parameter:**

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `protocolo` | `string` | Ex: `SOL-123456` |

**Responses:**

| Status | Descrição |
|--------|-----------|
| `200 OK` | Retorna `SolicitacaoResponse` |
| `404 Not Found` | Protocolo não encontrado |

---

### `GET /api/solicitacoes/{id}/historico`

Retorna o histórico completo de atualizações de uma solicitação.

**Path parameter:**

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `int` | ID numérico da solicitação (presente em `SolicitacaoResponse.id`) |

**Response `200 OK`:** array de `HistoricoStatusResponse`:

```json
[
  {
    "id": 1,
    "solicitacaoId": 1,
    "status": "TRIAGEM",
    "data": "2026-06-11T11:00:00",
    "responsavel": "Maria Servidora",
    "comentario": "Solicitação recebida e em triagem."
  }
]
```

**Responses:**

| Status | Descrição |
|--------|-----------|
| `200 OK` | Array de entradas do histórico (pode ser vazio `[]`) |

---

### `PATCH /api/solicitacoes/{id}/status`

Registra uma atualização de status em uma solicitação.

**Path parameter:**

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | `int` | ID numérico da solicitação |

**Request body** (`application/json`):

```json
{
  "status": "EM_EXECUCAO",
  "responsavel": "Carlos Técnico",
  "comentario": "Equipe deslocada para o local."
}
```

**Validações:**
- `status` - obrigatório; valor do enum `Status`
- `responsavel` - obrigatório e não-vazio
- `comentario` - obrigatório e não-vazio

**Responses:**

| Status | Descrição |
|--------|-----------|
| `204 No Content` | Status atualizado com sucesso |
| `400 Bad Request` | Corpo inválido |
| `404 Not Found` | ID não encontrado |

---

## Documentação interativa (Swagger UI)

Com a aplicação rodando, acesse:

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## Estrutura do projeto

```
observAcao/
├── src/
│   └── main/
│       ├── java/org/aep/observacao/
│       │   ├── ObservacaoApplication.java      # Entry point Spring Boot
│       │   ├── controller/
│       │   │   ├── SolicitacaoController.java  # Endpoints REST
│       │   │   └── dto/                        # Records de request/response
│       │   ├── service/
│       │   │   ├── ServicoSolicitacoes.java    # Lógica de negócio
│       │   │   ├── FilaAtendimento.java        # Fila por prioridade/bairro/categoria
│       │   │   └── DatabaseManager.java        # Inicialização do H2
│       │   ├── repository/
│       │   │   ├── SolicitacaoRepository.java  # Interface
│       │   │   ├── JdbcSolicitacaoRepository.java
│       │   │   ├── HistoricoStatusRepository.java
│       │   │   └── JdbcHistoricoStatusRepository.java
│       │   ├── model/                          # Entidades de domínio
│       │   ├── config/
│       │   │   └── OpenApiConfig.java          # Configuração Swagger
│       │   └── ui/
│       │       └── Main.java                   # Interface CLI (legado)
│       └── resources/
│           ├── application.properties
│           └── static/                         # Frontend (servido em /)
│               ├── css/style.css
│               ├── js/
│               │   ├── api.js                  # Camada de chamadas REST
│               │   └── utils.js                # Utilitários (formato, toast, params)
│               ├── index.html
│               ├── cidadao.html
│               ├── novo-chamado.html
│               ├── consultar.html
│               ├── meus-chamados.html
│               ├── servidor.html
│               ├── painel.html
│               ├── detalhes.html
│               └── historico.html
└── persistence/                                # Arquivo do banco H2
persistence/              # Banco H2
└── .mvn/jvm.config           # Configuração Maven
persistence/              # Banco H2
└── .mvn/jvm.config           # Configuração Maven
```
