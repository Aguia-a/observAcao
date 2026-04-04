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

### Via IDE (Code | IDEA)

Abra o arquivo em:

`src/main/java/org/aep/observacao/ui/Main.java`

Execute o arquivo pela IDE

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
