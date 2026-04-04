package org.aep.observacao.model;

public class Categoria {
    private String nome;
    private int slaDias; // SLA in days based on priority, but perhaps per category

    public Categoria(String nome, int slaDias) {
        this.nome = nome;
        this.slaDias = slaDias;
    }

    public String getNome() {
        return nome;
    }

    public int getSlaDias() {
        return slaDias;
    }

    @Override
    public String toString() {
        return nome;
    }
}