package org.aep.observacao.ui;

import org.aep.observacao.model.*;
import org.aep.observacao.service.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static ServicoSolicitacoes servico = new ServicoSolicitacoes();
    private static FilaAtendimento fila = new FilaAtendimento(servico);
    private static List<Categoria> categorias = Arrays.asList(
            new Categoria("Iluminação", 7),
            new Categoria("Buraco", 5),
            new Categoria("Limpeza", 3),
            new Categoria("Saúde", 1),
            new Categoria("Segurança Escolar", 2)
    );
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarDados();
        while (true) {
            System.out.println("=== ObservAção ===");
            System.out.println("1. Seção Cliente");
            System.out.println("2. Seção Servidor Público");
            System.out.println("0. Sair");
            System.out.print("Escolha > ");
            String input = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número.");
                continue;
            }
            switch (opcao) {
                case 1:
                    menuCliente();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void inicializarDados() {
        // Dados iniciais podem ser adicionados aqui se necessário
    }

    private static void menuCliente() {
        while (true) {
            System.out.println("\n=== Seção Cliente ===");
            System.out.println("1. Cadastrar Solicitação");
            System.out.println("2. Consultar Minhas Solicitações");
            System.out.println("0. Voltar");
            System.out.print("Escolha > ");
            String input = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número.");
                continue;
            }
            switch (opcao) {
                case 1:
                    cadastrarSolicitacao();
                    break;
                case 2:
                    consultarSolicitacoes();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarSolicitacao() {
        System.out.println("Cadastrar Solicitação");
        System.out.println("Categorias:");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i));
        }
        System.out.print("Escolha categoria > ");
        String input = scanner.nextLine();
        int catIndex;
        try {
            catIndex = Integer.parseInt(input) - 1;
            if (catIndex < 0 || catIndex >= categorias.size()) {
                System.out.println("Categoria inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        Categoria categoria = categorias.get(catIndex);

        System.out.print("Descrição > ");
        String descricao = scanner.nextLine();

        System.out.print("Localização > ");
        String localizacao = scanner.nextLine();

        System.out.println("Prioridade: 1. Baixa, 2. Média, 3. Alta");
        System.out.print("Escolha prioridade > ");
        input = scanner.nextLine();
        int prio;
        try {
            prio = Integer.parseInt(input);
            if (prio < 1 || prio > 3) {
                System.out.println("Prioridade inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        Prioridade prioridade = Prioridade.values()[prio - 1];

        System.out.print("Anônimo? (s/n) > ");
        boolean anonimo = scanner.nextLine().equalsIgnoreCase("s");

        Usuario usuario = null;
        if (!anonimo) {
            System.out.print("Nome > ");
            String nome = scanner.nextLine();
            System.out.print("Email > ");
            String email = scanner.nextLine();
            System.out.print("Telefone > ");
            String telefone = scanner.nextLine();
            usuario = new Usuario(1, nome, email, telefone); // simples, sem gerenciamento de id
        }

        Solicitacao solicitacao = servico.criarSolicitacao(categoria, descricao, localizacao, prioridade, usuario, anonimo);
        System.out.println("Solicitação criada com protocolo: " + solicitacao.getProtocolo());
    }

    private static void consultarSolicitacoes() {
        // Para solicitações não anônimas, mostrar todas
        List<Solicitacao> minhasSolicitacoes = servico.listarSolicitacoes(null, null, null).stream()
                .filter(s -> !s.isAnonimo())
                .collect(Collectors.toList());
        if (minhasSolicitacoes.isEmpty()) {
            System.out.println("Nenhuma solicitação encontrada. Para solicitações anônimas, use a seção de servidor ou lembre-se do protocolo.");
            return;
        }
        System.out.println("Suas Solicitações:");
        for (int i = 0; i < minhasSolicitacoes.size(); i++) {
            Solicitacao s = minhasSolicitacoes.get(i);
            System.out.println((i + 1) + ". Protocolo: " + s.getProtocolo() + " - Status: " + s.getStatus() + " - Categoria: " + s.getCategoria().getNome());
        }
        System.out.print("Escolha o número da solicitação para ver detalhes (ou 0 para voltar) > ");
        String input = scanner.nextLine();
        try {
            int escolha = Integer.parseInt(input);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > minhasSolicitacoes.size()) {
                System.out.println("Escolha inválida.");
                return;
            }
            Solicitacao sol = minhasSolicitacoes.get(escolha - 1);
            System.out.println(sol);
            System.out.println("Histórico:");
            servico.getHistorico(sol.getId()).forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
    }
}