package org.aep.observacao.service;

import org.aep.observacao.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicoSolicitacoes {
    private List<Solicitacao> solicitacoes = new ArrayList<>();
    private List<HistoricoStatus> historico = new ArrayList<>();
    private int nextId = 1;
    private int nextHistoricoId = 1;

    public Solicitacao criarSolicitacao(Categoria categoria, String descricao, String localizacao, Prioridade prioridade, Usuario usuario, boolean anonimo) {
        String protocolo = "SOL-" + String.format("%06d", nextId);
        Solicitacao solicitacao = new Solicitacao(nextId++, protocolo, categoria, descricao, localizacao, prioridade, Status.ABERTO, LocalDateTime.now(), usuario, anonimo);
        solicitacoes.add(solicitacao);
        // Add initial history
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
            adicionarHistorico(solicitacaoId, novoStatus, responsavel, comentario);
            return true;
        }
        return false;
    }

    private void adicionarHistorico(int solicitacaoId, Status status, String responsavel, String comentario) {
        HistoricoStatus hist = new HistoricoStatus(nextHistoricoId++, solicitacaoId, status, LocalDateTime.now(), responsavel, comentario);
        historico.add(hist);
    }

    public List<HistoricoStatus> getHistorico(int solicitacaoId) {
        return historico.stream()
                .filter(h -> h.getSolicitacaoId() == solicitacaoId)
                .collect(Collectors.toList());
    }

    // For client to see their requests
    public List<Solicitacao> getSolicitacoesPorUsuario(Usuario usuario) {
        return solicitacoes.stream()
                .filter(s -> !s.isAnonimo() && s.getUsuario() != null && s.getUsuario().getId() == usuario.getId())
                .collect(Collectors.toList());
    }
}