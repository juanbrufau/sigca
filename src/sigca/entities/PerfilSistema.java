/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de PerfilSistema. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum PerfilSistema {

    ADMINISTRADOR_NACIONAL("Administrador Nacional"),
    GESTOR_REGIONAL("Gestor Regional"),
    COORDENADOR_LOJA("Coordenador de Loja");

    private final String descricao;

    PerfilSistema(String descricao) {
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
