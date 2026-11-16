/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de OrigemAlocacao. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum OrigemAlocacao {

    DISTRIBUIDA("Distribuída pelo coordenador"),
    ESPONTANEA("Voluntário espontâneo");

    private final String descricao;

    OrigemAlocacao(String descricao) {
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
