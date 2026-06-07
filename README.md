# ObservAção

Projeto da AEP referente ao 5o semestre do curso de Engenharia de Software da UniCesumar.

## Pré-requisitos

> [!WARNING]
> **Requisitos obrigatórios**
> 
> _*_ indica um requisito obrigatório.

- **Java JDK 21** *
  - Preferencialmente a JDK 21 Temurin (Da qual foi feita o projeto)
  - Configure a variável de ambiente `JAVA_HOME` apontando para a instalação JDK 21
  - Ou certifique-se de que o comando `java` na linha de comando seja do JDK 21
- **Apache Maven 3.9.6**

## Como executar

### Via Spring Boot

```bash
mvn spring-boot:run
```

### Via IDE (Code | IDEA)

Abra o arquivo em:

`src/main/java/org/aep/observacao/ObservacaoApplication.java`

Execute o arquivo pela IDE para subir a API REST.

Se quiser usar a interface CLI antiga, ainda é possível executar:

`src/main/java/org/aep/observacao/ui/Main.java`

## API REST

- `POST /api/solicitacoes` para criar solicitação
- `GET /api/solicitacoes` para listar e filtrar solicitações
- `GET /api/solicitacoes/{protocolo}` para buscar por protocolo
- `PATCH /api/solicitacoes/{id}/status` para atualizar status
- `GET /api/solicitacoes/{id}/historico` para consultar histórico

> [!NOTE]
> **Sobre a execução**
> 
> Pode demorar alguns segundos para a execução, pois a IDE irá fazer o download das dependências e depois fazer a build do projeto.

### Via terminal

> [!IMPORTANT]
> **Maven obrigatório**
> 
> Para a execução via terminal é obrigatório a instalação ou os binários do Maven.

```bash
# Compilar
mvn clean compile

# Testar
mvn test

# Executar
mvn exec:java
```

## Funcionalidades

- **Cliente**: Cadastrar solicitações e consultar histórico
- **Servidor Público**: Gerenciar status das solicitações
- **Persistência**: Dados salvos em banco H2 (pasta `persistence/`)

## Estrutura do Projeto

```text
observAcao/
├── src/main/java/org/aep/observacao/
│   ├── ui/Main.java          # Interface CLI
│   ├── service/              # Lógica de negócio
│   └── model/                # Classes de domínio
├── persistence/              # Banco H2
└── .mvn/jvm.config           # Configuração Maven
```
