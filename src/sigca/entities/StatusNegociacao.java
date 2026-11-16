/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de StatusNegociacao. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum StatusNegociacao {

    PROSPECCAO("Prospecção"),
    NEGOCIANDO("Negociando"),
    CONFIRMADO("Confirmado"),
    RECUSADO("Recusado");

    private final String descricao;

    StatusNegociacao(String descricao) {
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
