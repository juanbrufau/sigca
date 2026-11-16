/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de StatusEdicao. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum StatusEdicao {

    PLANEJAMENTO("Em planejamento"),
    INSCRICOES_ABERTAS("Inscrições abertas"),
    EM_EXECUCAO("Em execução"),
    ENCERRADA("Encerrada");

    private final String descricao;

    StatusEdicao(String descricao) {
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
