/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de PapelParticipacao. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum PapelParticipacao {

    VOLUNTARIO("Voluntário"),
    COORDENADOR_LOJA("Coordenador de Loja"),
    COORDENADOR_REGIONAL("Coordenador Regional"),
    ADMINISTRADOR_NACIONAL("Administrador Nacional");

    private final String descricao;

    PapelParticipacao(String descricao) {
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
