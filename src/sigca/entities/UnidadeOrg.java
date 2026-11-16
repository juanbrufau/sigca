/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Entidade UnidadeOrg — espelha a tabela unidade_org do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class UnidadeOrg {

    private int id;
    private Integer unidadeOrgPaiId;
    private TipoUnidade tipo;
    private String nome;
    private boolean ativo;

    // Campos de apresentação: não existem como coluna na tabela.
    private java.util.List<UnidadeOrg> filhas = new java.util.ArrayList<>();

    public UnidadeOrg() {
    }

    public UnidadeOrg(int id, Integer unidadeOrgPaiId, TipoUnidade tipo, String nome, boolean ativo) {
        this.id = id;
        this.unidadeOrgPaiId = unidadeOrgPaiId;
        this.tipo = tipo;
        this.nome = nome;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUnidadeOrgPaiId() {
        return unidadeOrgPaiId;
    }

    public void setUnidadeOrgPaiId(Integer unidadeOrgPaiId) {
        this.unidadeOrgPaiId = unidadeOrgPaiId;
    }

    public TipoUnidade getTipo() {
        return tipo;
    }

    public void setTipo(TipoUnidade tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public java.util.List<UnidadeOrg> getFilhas() {
        return filhas;
    }

    public void setFilhas(java.util.List<UnidadeOrg> filhas) {
        this.filhas = filhas;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | UnidadeOrgPaiId: %s | Tipo: %s | Nome: %s | Ativo: %s", id, unidadeOrgPaiId, tipo, nome, ativo);
    }
}
