# ObservAção

Projeto da AEP referente ao 5o semestre do curso de Engenharia de Software da UniCesumar.

## Pré-requisitos

- **Java JDK 21**
  - Configure a variável de ambiente `JAVA_HOME` apontando para a instalação JDK 21
  - Ou certifique-se de que o comando `java` na linha de comando seja do JDK 21
- **Apache Maven 3.9.6**

## Como executar

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
├── src/test/                 # Testes unitários
├── persistence/              # Banco H2
└── .mvn/jvm.config          # Configuração Maven
```
