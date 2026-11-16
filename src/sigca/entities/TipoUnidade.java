/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de TipoUnidade. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum TipoUnidade {

    NACIONAL("Nacional"),
    REGIONAL("Regional"),
    LOCAL("Local");

    private final String descricao;

    TipoUnidade(String descricao) {
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
