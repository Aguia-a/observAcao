# Sobre o ObservAção

## O que é?

O **ObservAção** é um sistema de gerenciamento de solicitações urbanas desenvolvido como projeto acadêmico (AEP) do 5º semestre do curso de Engenharia de Software da UniCesumar.

Trata-se de uma aplicação de linha de comando (CLI) que conecta **cidadãos** a **servidores públicos**, permitindo o registro, acompanhamento e gestão de ocorrências urbanas no município.

## Objetivo

O objetivo do sistema é facilitar a comunicação entre a população e o poder público, proporcionando um canal organizado para que os cidadãos possam:

- Reportar problemas urbanos (buracos, iluminação, limpeza, saúde, segurança escolar, entre outros)
- Acompanhar o status das solicitações por meio de um protocolo único
- Registrar solicitações de forma anônima ou identificada

E para que os servidores públicos possam:

- Visualizar e priorizar as solicitações recebidas
- Filtrar ocorrências por prioridade, bairro ou categoria
- Atualizar o status de atendimento de cada solicitação
- Registrar responsável e comentários em cada atualização de status

## Problema que resolve

Muitas vezes, problemas urbanos como buracos nas vias, falhas na iluminação pública ou questões de limpeza demoram a ser corrigidos por falta de um canal eficiente de comunicação entre cidadão e prefeitura. O **ObservAção** propõe uma solução estruturada para esse fluxo, com categorização, priorização e rastreabilidade das solicitações.

## Fluxo principal

```
Cidadão
  └─> Cadastra solicitação (categoria, descrição, localização, prioridade)
  └─> Recebe protocolo único
  └─> Pode consultar o status a qualquer momento pelo protocolo

Servidor Público
  └─> Visualiza fila de solicitações (por prioridade, bairro ou categoria)
  └─> Atualiza o status da solicitação (Triagem → Em Execução → Resolvido → Encerrado)
  └─> Registra responsável e comentário em cada atualização
```

## Tecnologias utilizadas

| Tecnologia | Versão | Uso |
|---|---|---|
| Java (JDK Temurin) | 21 | Linguagem principal |
| Apache Maven | 3.9.6 | Build e gerenciamento de dependências |
| H2 Database | 2.2.224 | Persistência de dados local (pasta `persistence/`) |
| JUnit | 4.13.2 | Testes automatizados |
