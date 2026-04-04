package org.aep.observacao.service;

import org.aep.observacao.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicoSolicitacoes {
    private List<Solicitacao> solicitacoes = new ArrayList<>();
    private List<HistoricoStatus> historico = new ArrayList<>();
    private int nextId = 1;
    private int nextHistoricoId = 1;

    public ServicoSolicitacoes() {
        DatabaseManager.initializeDatabase();
        loadSolicitacoes();
        loadHistorico();
    }

    public Solicitacao criarSolicitacao(Categoria categoria, String descricao, String localizacao, Prioridade prioridade, Usuario usuario, boolean anonimo) {
        String protocolo = "SOL-" + String.format("%06d", nextId);
        Solicitacao solicitacao = new Solicitacao(nextId++, protocolo, categoria, descricao, localizacao, prioridade, Status.ABERTO, LocalDateTime.now(), usuario, anonimo);
        solicitacoes.add(solicitacao);
        inserirSolicitacaoDb(solicitacao);
        adicionarHistorico(solicitacao.getId(), Status.ABERTO, "Sistema", "Solicitação criada");
        return solicitacao;
    }

    public List<Solicitacao> listarSolicitacoes(Prioridade prioridade, String bairro, Categoria categoria) {
        return solicitacoes.stream()
                .filter(s -> prioridade == null || s.getPrioridade() == prioridade)
                .filter(s -> bairro == null || s.getLocalizacao().contains(bairro))
                .filter(s -> categoria == null || s.getCategoria().equals(categoria))
                .collect(Collectors.toList());
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {
        return solicitacoes.stream()
                .filter(s -> s.getProtocolo().equals(protocolo))
                .findFirst()
                .orElse(null);
    }

    public boolean atualizarStatus(int solicitacaoId, Status novoStatus, String responsavel, String comentario) {
        Solicitacao solicitacao = solicitacoes.stream()
                .filter(s -> s.getId() == solicitacaoId)
                .findFirst()
                .orElse(null);
        if (solicitacao != null) {
            solicitacao.setStatus(novoStatus);
            atualizarSolicitacaoStatusDb(solicitacao);
            adicionarHistorico(solicitacaoId, novoStatus, responsavel, comentario);
            return true;
        }
        return false;
    }

    private void adicionarHistorico(int solicitacaoId, Status status, String responsavel, String comentario) {
        HistoricoStatus hist = new HistoricoStatus(nextHistoricoId++, solicitacaoId, status, LocalDateTime.now(), responsavel, comentario);
        historico.add(hist);
        inserirHistoricoDb(hist);
    }

    public List<HistoricoStatus> getHistorico(int solicitacaoId) {
        return historico.stream()
                .filter(h -> h.getSolicitacaoId() == solicitacaoId)
                .collect(Collectors.toList());
    }

    public List<Solicitacao> getSolicitacoesPorUsuario(Usuario usuario) {
        return solicitacoes.stream()
                .filter(s -> !s.isAnonimo() && s.getUsuario() != null && s.getUsuario().getId() == usuario.getId())
                .collect(Collectors.toList());
    }

    private void loadSolicitacoes() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM solicitacao")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String protocolo = resultSet.getString("protocolo");
                String categoriaNome = resultSet.getString("categoria");
                int categoriaSla = resultSet.getInt("categoria_sla");
                String descricao = resultSet.getString("descricao");
                String localizacao = resultSet.getString("localizacao");
                Prioridade prioridade = Prioridade.valueOf(resultSet.getString("prioridade"));
                Status status = Status.valueOf(resultSet.getString("status"));
                LocalDateTime dataCriacao = LocalDateTime.parse(resultSet.getString("data_criacao"));
                boolean anonimo = resultSet.getBoolean("anonimo");
                Usuario usuario = null;
                if (!anonimo) {
                    int usuarioId = resultSet.getInt("usuario_id");
                    String usuarioNome = resultSet.getString("usuario_nome");
                    String usuarioEmail = resultSet.getString("usuario_email");
                    String usuarioTelefone = resultSet.getString("usuario_telefone");
                    usuario = new Usuario(usuarioId, usuarioNome, usuarioEmail, usuarioTelefone);
                }
                Categoria categoria = new Categoria(categoriaNome, categoriaSla);
                Solicitacao solicitacao = new Solicitacao(id, protocolo, categoria, descricao, localizacao, prioridade, status, dataCriacao, usuario, anonimo);
                solicitacoes.add(solicitacao);
                nextId = Math.max(nextId, id + 1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar solicitações do banco de dados", e);
        }
    }

    private void loadHistorico() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM historico_status ORDER BY id")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int solicitacaoId = resultSet.getInt("solicitacao_id");
                Status status = Status.valueOf(resultSet.getString("status"));
                LocalDateTime data = LocalDateTime.parse(resultSet.getString("data"));
                String responsavel = resultSet.getString("responsavel");
                String comentario = resultSet.getString("comentario");
                HistoricoStatus hist = new HistoricoStatus(id, solicitacaoId, status, data, responsavel, comentario);
                historico.add(hist);
                nextHistoricoId = Math.max(nextHistoricoId, id + 1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar histórico do banco de dados", e);
        }
    }

    private void inserirSolicitacaoDb(Solicitacao solicitacao) {
        String sql = "INSERT INTO solicitacao (id, protocolo, categoria, categoria_sla, descricao, localizacao, prioridade, status, data_criacao, usuario_id, usuario_nome, usuario_email, usuario_telefone, anonimo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, solicitacao.getId());
            statement.setString(2, solicitacao.getProtocolo());
            statement.setString(3, solicitacao.getCategoria().getNome());
            statement.setInt(4, solicitacao.getCategoria().getSlaDias());
            statement.setString(5, solicitacao.getDescricao());
            statement.setString(6, solicitacao.getLocalizacao());
            statement.setString(7, solicitacao.getPrioridade().name());
            statement.setString(8, solicitacao.getStatus().name());
            statement.setString(9, solicitacao.getDataCriacao().toString());
            if (solicitacao.getUsuario() != null) {
                statement.setInt(10, solicitacao.getUsuario().getId());
                statement.setString(11, solicitacao.getUsuario().getNome());
                statement.setString(12, solicitacao.getUsuario().getEmail());
                statement.setString(13, solicitacao.getUsuario().getTelefone());
            } else {
                statement.setNull(10, java.sql.Types.INTEGER);
                statement.setNull(11, java.sql.Types.VARCHAR);
                statement.setNull(12, java.sql.Types.VARCHAR);
                statement.setNull(13, java.sql.Types.VARCHAR);
            }
            statement.setBoolean(14, solicitacao.isAnonimo());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir solicitação no banco de dados", e);
        }
    }

    private void inserirHistoricoDb(HistoricoStatus hist) {
        String sql = "INSERT INTO historico_status (id, solicitacao_id, status, data, responsavel, comentario) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hist.getId());
            statement.setInt(2, hist.getSolicitacaoId());
            statement.setString(3, hist.getStatus().name());
            statement.setString(4, hist.getData().toString());
            statement.setString(5, hist.getResponsavel());
            statement.setString(6, hist.getComentario());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir histórico no banco de dados", e);
        }
    }

    private void atualizarSolicitacaoStatusDb(Solicitacao solicitacao) {
        String sql = "UPDATE solicitacao SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, solicitacao.getStatus().name());
            statement.setInt(2, solicitacao.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status no banco de dados", e);
        }
    }
}
