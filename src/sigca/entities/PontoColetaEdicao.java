/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

import java.time.LocalDateTime;

/**
 * Entidade PontoColetaEdicao — espelha a tabela ponto_coleta_edicao do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class PontoColetaEdicao {

    private int id;
    private int edicaoId;
    private int pontoColetaId;
    private String nomeSnapshot;
    private String enderecoSnapshot;
    private int metaVoluntarios;
    private StatusNegociacao statusNegociacao;
    private LocalDateTime resultadoEm;
    private Integer coordenadorParticipacaoId;

    // Campos de apresentação: não existem como coluna na tabela.
    private java.util.List<ResultadoAlimento> resultados = new java.util.ArrayList<>();
    private int alocados = 0;

    public PontoColetaEdicao() {
    }

    public PontoColetaEdicao(int id, int edicaoId, int pontoColetaId, String nomeSnapshot, String enderecoSnapshot, int metaVoluntarios, StatusNegociacao statusNegociacao, LocalDateTime resultadoEm, Integer coordenadorParticipacaoId) {
        this.id = id;
        this.edicaoId = edicaoId;
        this.pontoColetaId = pontoColetaId;
        this.nomeSnapshot = nomeSnapshot;
        this.enderecoSnapshot = enderecoSnapshot;
        this.metaVoluntarios = metaVoluntarios;
        this.statusNegociacao = statusNegociacao;
        this.resultadoEm = resultadoEm;
        this.coordenadorParticipacaoId = coordenadorParticipacaoId;
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

    public int getPontoColetaId() {
        return pontoColetaId;
    }

    public void setPontoColetaId(int pontoColetaId) {
        this.pontoColetaId = pontoColetaId;
    }

    public String getNomeSnapshot() {
        return nomeSnapshot;
    }

    public void setNomeSnapshot(String nomeSnapshot) {
        this.nomeSnapshot = nomeSnapshot;
    }

    public String getEnderecoSnapshot() {
        return enderecoSnapshot;
    }

    public void setEnderecoSnapshot(String enderecoSnapshot) {
        this.enderecoSnapshot = enderecoSnapshot;
    }

    public int getMetaVoluntarios() {
        return metaVoluntarios;
    }

    public void setMetaVoluntarios(int metaVoluntarios) {
        this.metaVoluntarios = metaVoluntarios;
    }

    public StatusNegociacao getStatusNegociacao() {
        return statusNegociacao;
    }

    public void setStatusNegociacao(StatusNegociacao statusNegociacao) {
        this.statusNegociacao = statusNegociacao;
    }

    public LocalDateTime getResultadoEm() {
        return resultadoEm;
    }

    public void setResultadoEm(LocalDateTime resultadoEm) {
        this.resultadoEm = resultadoEm;
    }

    public Integer getCoordenadorParticipacaoId() {
        return coordenadorParticipacaoId;
    }

    public void setCoordenadorParticipacaoId(Integer coordenadorParticipacaoId) {
        this.coordenadorParticipacaoId = coordenadorParticipacaoId;
    }

    public java.util.List<ResultadoAlimento> getResultados() {
        return resultados;
    }

    public void setResultados(java.util.List<ResultadoAlimento> resultados) {
        this.resultados = resultados;
    }

    public int getAlocados() {
        return alocados;
    }

    public void setAlocados(int alocados) {
        this.alocados = alocados;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | EdicaoId: %s | PontoColetaId: %s | NomeSnapshot: %s | EnderecoSnapshot: %s", id, edicaoId, pontoColetaId, nomeSnapshot, enderecoSnapshot);
    }
}
