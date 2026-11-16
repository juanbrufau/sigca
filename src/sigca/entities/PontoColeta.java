/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Entidade PontoColeta — espelha a tabela ponto_coleta do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class PontoColeta {

    private int id;
    private int unidadeOrgId;
    private int municipioId;
    private String nome;
    private String endereco;
    private boolean ativo;

    // Campos de apresentação: não existem como coluna na tabela.
    private String municipioNome = null;
    private String unidadeNome = null;

    public PontoColeta() {
    }

    public PontoColeta(int id, int unidadeOrgId, int municipioId, String nome, String endereco, boolean ativo) {
        this.id = id;
        this.unidadeOrgId = unidadeOrgId;
        this.municipioId = municipioId;
        this.nome = nome;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUnidadeOrgId() {
        return unidadeOrgId;
    }

    public void setUnidadeOrgId(int unidadeOrgId) {
        this.unidadeOrgId = unidadeOrgId;
    }

    public int getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(int municipioId) {
        this.municipioId = municipioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getMunicipioNome() {
        return municipioNome;
    }

    public void setMunicipioNome(String municipioNome) {
        this.municipioNome = municipioNome;
    }

    public String getUnidadeNome() {
        return unidadeNome;
    }

    public void setUnidadeNome(String unidadeNome) {
        this.unidadeNome = unidadeNome;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | UnidadeOrgId: %s | MunicipioId: %s | Nome: %s | Endereco: %s", id, unidadeOrgId, municipioId, nome, endereco);
    }
}
