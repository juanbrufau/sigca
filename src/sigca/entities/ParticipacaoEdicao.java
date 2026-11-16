/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidade ParticipacaoEdicao — espelha a tabela participacao_edicao do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class ParticipacaoEdicao {

    private int id;
    private int edicaoId;
    private int pessoaId;
    private int unidadeOrgId;
    private Integer pontoColetaEdicaoId;
    private PapelParticipacao papel;
    private FaixaEtaria faixaEtaria;
    private LocalTime disponivelDas;
    private LocalTime disponivelAte;
    private OrigemAlocacao origemAlocacao;
    private LocalDateTime alocadoEm;
    private LocalDateTime inscritoEm;

    // Campos de apresentação: não existem como coluna na tabela.
    private String pessoaNome = null;

    public ParticipacaoEdicao() {
    }

    public ParticipacaoEdicao(int id, int edicaoId, int pessoaId, int unidadeOrgId, Integer pontoColetaEdicaoId, PapelParticipacao papel, FaixaEtaria faixaEtaria, LocalTime disponivelDas, LocalTime disponivelAte, OrigemAlocacao origemAlocacao, LocalDateTime alocadoEm, LocalDateTime inscritoEm) {
        this.id = id;
        this.edicaoId = edicaoId;
        this.pessoaId = pessoaId;
        this.unidadeOrgId = unidadeOrgId;
        this.pontoColetaEdicaoId = pontoColetaEdicaoId;
        this.papel = papel;
        this.faixaEtaria = faixaEtaria;
        this.disponivelDas = disponivelDas;
        this.disponivelAte = disponivelAte;
        this.origemAlocacao = origemAlocacao;
        this.alocadoEm = alocadoEm;
        this.inscritoEm = inscritoEm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEdicaoId() {
        return edicaoId;
    }

    public void setEdicaoId(int edicaoId) {
        this.edicaoId = edicaoId;
    }

    public int getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(int pessoaId) {
        this.pessoaId = pessoaId;
    }

    public int getUnidadeOrgId() {
        return unidadeOrgId;
    }

    public void setUnidadeOrgId(int unidadeOrgId) {
        this.unidadeOrgId = unidadeOrgId;
    }

    public Integer getPontoColetaEdicaoId() {
        return pontoColetaEdicaoId;
    }

    public void setPontoColetaEdicaoId(Integer pontoColetaEdicaoId) {
        this.pontoColetaEdicaoId = pontoColetaEdicaoId;
    }

    public PapelParticipacao getPapel() {
        return papel;
    }

    public void setPapel(PapelParticipacao papel) {
        this.papel = papel;
    }

    public FaixaEtaria getFaixaEtaria() {
        return faixaEtaria;
    }

    public void setFaixaEtaria(FaixaEtaria faixaEtaria) {
        this.faixaEtaria = faixaEtaria;
    }

    public LocalTime getDisponivelDas() {
        return disponivelDas;
    }

    public void setDisponivelDas(LocalTime disponivelDas) {
        this.disponivelDas = disponivelDas;
    }

    public LocalTime getDisponivelAte() {
        return disponivelAte;
    }

    public void setDisponivelAte(LocalTime disponivelAte) {
        this.disponivelAte = disponivelAte;
    }

    public OrigemAlocacao getOrigemAlocacao() {
        return origemAlocacao;
    }

    public void setOrigemAlocacao(OrigemAlocacao origemAlocacao) {
        this.origemAlocacao = origemAlocacao;
    }

    public LocalDateTime getAlocadoEm() {
        return alocadoEm;
    }

    public void setAlocadoEm(LocalDateTime alocadoEm) {
        this.alocadoEm = alocadoEm;
    }

    public LocalDateTime getInscritoEm() {
        return inscritoEm;
    }

    public void setInscritoEm(LocalDateTime inscritoEm) {
        this.inscritoEm = inscritoEm;
    }

    public String getPessoaNome() {
        return pessoaNome;
    }

    public void setPessoaNome(String pessoaNome) {
        this.pessoaNome = pessoaNome;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | EdicaoId: %s | PessoaId: %s | UnidadeOrgId: %s | PontoColetaEdicaoId: %s", id, edicaoId, pessoaId, unidadeOrgId, pontoColetaEdicaoId);
    }
}
