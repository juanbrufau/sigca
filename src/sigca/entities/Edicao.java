/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

import java.time.LocalDate;

/**
 * Entidade Edicao — espelha a tabela edicao do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class Edicao {

    private int id;
    private int ano;
    private LocalDate dataColeta;
    private LocalDate inscricoesInicio;
    private LocalDate inscricoesFim;
    private StatusEdicao status;

    public Edicao() {
    }

    public Edicao(int id, int ano, LocalDate dataColeta, LocalDate inscricoesInicio, LocalDate inscricoesFim, StatusEdicao status) {
        this.id = id;
        this.ano = ano;
        this.dataColeta = dataColeta;
        this.inscricoesInicio = inscricoesInicio;
        this.inscricoesFim = inscricoesFim;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public LocalDate getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(LocalDate dataColeta) {
        this.dataColeta = dataColeta;
    }

    public LocalDate getInscricoesInicio() {
        return inscricoesInicio;
    }

    public void setInscricoesInicio(LocalDate inscricoesInicio) {
        this.inscricoesInicio = inscricoesInicio;
    }

    public LocalDate getInscricoesFim() {
        return inscricoesFim;
    }

    public void setInscricoesFim(LocalDate inscricoesFim) {
        this.inscricoesFim = inscricoesFim;
    }

    public StatusEdicao getStatus() {
        return status;
    }

    public void setStatus(StatusEdicao status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Id: %s | Ano: %s | DataColeta: %s | InscricoesInicio: %s | InscricoesFim: %s", id, ano, dataColeta, inscricoesInicio, inscricoesFim);
    }
}
