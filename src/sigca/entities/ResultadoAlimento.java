/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade ResultadoAlimento — espelha a tabela resultado_alimento do banco de dados.
 *
 * Segue a metodologia da disciplina: atributos privados equivalentes aos
 * campos da tabela, construtor vazio e construtor completo, getters e
 * setters, e toString() sobrescrito para exibição no console.
 */
public class ResultadoAlimento {

    private int pontoColetaEdicaoId;
    private int tipoAlimentoId;
    private BigDecimal quantidadeKg;
    private LocalDateTime registradoEm;

    // Campos de apresentação: não existem como coluna na tabela.
    private String alimentoNome = null;

    public ResultadoAlimento() {
    }

    public ResultadoAlimento(int pontoColetaEdicaoId, int tipoAlimentoId, BigDecimal quantidadeKg, LocalDateTime registradoEm) {
        this.pontoColetaEdicaoId = pontoColetaEdicaoId;
        this.tipoAlimentoId = tipoAlimentoId;
        this.quantidadeKg = quantidadeKg;
        this.registradoEm = registradoEm;
    }

    public int getPontoColetaEdicaoId() {
        return pontoColetaEdicaoId;
    }

    public void setPontoColetaEdicaoId(int pontoColetaEdicaoId) {
        this.pontoColetaEdicaoId = pontoColetaEdicaoId;
    }

    public int getTipoAlimentoId() {
        return tipoAlimentoId;
    }

    public void setTipoAlimentoId(int tipoAlimentoId) {
        this.tipoAlimentoId = tipoAlimentoId;
    }

    public BigDecimal getQuantidadeKg() {
        return quantidadeKg;
    }

    public void setQuantidadeKg(BigDecimal quantidadeKg) {
        this.quantidadeKg = quantidadeKg;
    }

    public LocalDateTime getRegistradoEm() {
        return registradoEm;
    }

    public void setRegistradoEm(LocalDateTime registradoEm) {
        this.registradoEm = registradoEm;
    }

    public String getAlimentoNome() {
        return alimentoNome;
    }

    public void setAlimentoNome(String alimentoNome) {
        this.alimentoNome = alimentoNome;
    }

    @Override
    public String toString() {
        return String.format("PontoColetaEdicaoId: %s | TipoAlimentoId: %s | QuantidadeKg: %s | RegistradoEm: %s", pontoColetaEdicaoId, tipoAlimentoId, quantidadeKg, registradoEm);
    }
}
