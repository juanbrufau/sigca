/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

/**
 * Entidade TipoAlimento — espelha a tabela tipo_alimento do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class TipoAlimento {

    private int id;
    private String nome;
    private CategoriaAlimento categoria;
    private int ordem;
    private boolean ativo;

    public TipoAlimento() {
    }

    public TipoAlimento(int id, String nome, CategoriaAlimento categoria, int ordem, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.ordem = ordem;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CategoriaAlimento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaAlimento categoria) {
        this.categoria = categoria;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | Nome: %s | Categoria: %s | Ordem: %s | Ativo: %s", id, nome, categoria, ordem, ativo);
    }
}
