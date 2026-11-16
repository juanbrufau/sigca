/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de FaixaEtaria. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum FaixaEtaria {

    CRIANCA("Criança"),
    ADOLESCENTE("Adolescente"),
    ADULTO("Adulto");

    private final String descricao;

    FaixaEtaria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
