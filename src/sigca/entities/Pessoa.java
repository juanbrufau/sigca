/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Pessoa — espelha a tabela pessoa do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class Pessoa {

    private int id;
    private Integer municipioId;
    private String nome;
    private String nomeSocial;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String bairro;
    private LocalDateTime criadoEm;

    // Campos de apresentação: não existem como coluna na tabela.
    private String municipioNome = null;

    public Pessoa() {
    }

    public Pessoa(int id, Integer municipioId, String nome, String nomeSocial, LocalDate dataNascimento, String telefone, String email, String bairro, LocalDateTime criadoEm) {
        this.id = id;
        this.municipioId = municipioId;
        this.nome = nome;
        this.nomeSocial = nomeSocial;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.bairro = bairro;
        this.criadoEm = criadoEm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipioId) {
        this.municipioId = municipioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMunicipioNome() {
        return municipioNome;
    }

    public void setMunicipioNome(String municipioNome) {
        this.municipioNome = municipioNome;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | MunicipioId: %s | Nome: %s | NomeSocial: %s | DataNascimento: %s", id, municipioId, nome, nomeSocial, dataNascimento);
    }
}
