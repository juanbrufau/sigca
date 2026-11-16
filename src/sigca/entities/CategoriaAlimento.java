/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Domínio fechado de CategoriaAlimento. Enumeração em vez de texto livre
 * (Linguagem de Programação I, aula 16).
 */
public enum CategoriaAlimento {

    CEREAIS_E_GRAOS("Cereais e grãos"),
    FARINACEOS("Farináceos"),
    ENLATADOS("Enlatados"),
    LATICINIOS_E_BEBIDAS("Laticínios e bebidas"),
    MERCEARIA("Mercearia"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaAlimento(String descricao) {
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
