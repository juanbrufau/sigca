/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import sigca.conexao.Conexao;
import sigca.entities.ResultadoAlimento;

/**
 * Repositório de ResultadoAlimento — a tabela que resolve o N:N entre o
 * ponto de coleta da edição e o tipo de alimento.
 *
 * A chave primária é composta pelas duas chaves estrangeiras, então as
 * operações de alteração e exclusão dependem das duas colunas, e não de
 * um id próprio.
 */
public class ResultadoAlimentoRepository extends Conexao {

    private static final String SELECT_BASE = """
            SELECT ra.*, ta.nome AS alimento_nome
              FROM resultado_alimento ra
                   INNER JOIN tipo_alimento ta ON ta.id = ra.tipo_alimento_id""";

    /**
     * Grava o lançamento. Se já existir lançamento daquele alimento
     * naquela loja, a quantidade é substituída — é o comportamento que o
     * coordenador espera ao corrigir um número digitado errado.
     */
    public boolean inserir(ResultadoAlimento obj) {
        String sql = """
                INSERT INTO resultado_alimento
                    (ponto_coleta_edicao_id, tipo_alimento_id, quantidade_kg)
                VALUES (?, ?, ?)
                ON CONFLICT (ponto_coleta_edicao_id, tipo_alimento_id)
                DO UPDATE SET quantidade_kg = EXCLUDED.quantidade_kg,
                              registrado_em = CURRENT_TIMESTAMP""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getPontoColetaEdicaoId());
            ps.setInt(2, obj.getTipoAlimentoId());
            ps.setBigDecimal(3, obj.getQuantidadeKg());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao lançar o resultado: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(ResultadoAlimento obj) {
        String sql = """
                UPDATE resultado_alimento
                   SET quantidade_kg = ?, registrado_em = CURRENT_TIMESTAMP
                 WHERE ponto_coleta_edicao_id = ? AND tipo_alimento_id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setBigDecimal(1, obj.getQuantidadeKg());
            ps.setInt(2, obj.getPontoColetaEdicaoId());
            ps.setInt(3, obj.getTipoAlimentoId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar o resultado: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int pontoColetaEdicaoId, int tipoAlimentoId) {
        String sql = """
                DELETE FROM resultado_alimento
                 WHERE ponto_coleta_edicao_id = ? AND tipo_alimento_id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pontoColetaEdicaoId);
            ps.setInt(2, tipoAlimentoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir o resultado: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<ResultadoAlimento> listarPorPontoColetaEdicao(int pontoColetaEdicaoId) {
        ArrayList<ResultadoAlimento> lista = new ArrayList<>();
        String sql = SELECT_BASE + """

                 WHERE ra.ponto_coleta_edicao_id = ?
                 ORDER BY ta.ordem""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pontoColetaEdicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar o resultado: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Total arrecadado por tipo de alimento numa edição.
     *
     * A soma é feita pelo banco, com junção e agrupamento (Banco de
     * Dados, aulas 13 e 14). O Map preserva a ordem do ORDER BY e é a
     * estrutura natural para um totalizador por chave (Estrutura de
     * Dados, aula 16).
     */
    public Map<String, BigDecimal> somarPorAlimento(int edicaoId) {
        Map<String, BigDecimal> totais = new LinkedHashMap<>();
        String sql = """
                SELECT ta.nome AS alimento, SUM(ra.quantidade_kg) AS total_kg
                  FROM resultado_alimento ra
                       INNER JOIN tipo_alimento ta ON ta.id = ra.tipo_alimento_id
                       INNER JOIN ponto_coleta_edicao pce
                               ON pce.id = ra.ponto_coleta_edicao_id
                 WHERE pce.edicao_id = ?
                 GROUP BY ta.id, ta.nome
                 ORDER BY total_kg DESC""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, edicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totais.put(rs.getString("alimento"),
                               rs.getBigDecimal("total_kg"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao somar por alimento: " + e.getMessage());
        }
        return totais;
    }

    /**
     * Total de uma loja na edição. Não existe coluna de total: ele é
     * sempre a soma do detalhamento.
     */
    public BigDecimal totalDoPonto(int pontoColetaEdicaoId) {
        String sql = """
                SELECT COALESCE(SUM(quantidade_kg), 0) AS total_kg
                  FROM resultado_alimento
                 WHERE ponto_coleta_edicao_id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pontoColetaEdicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_kg");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao somar o total da loja: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private ResultadoAlimento ler(ResultSet rs) throws SQLException {
        ResultadoAlimento obj = new ResultadoAlimento(
                    rs.getInt("ponto_coleta_edicao_id"),
                    rs.getInt("tipo_alimento_id"),
                    rs.getBigDecimal("quantidade_kg"),
                    rs.getObject("registrado_em", LocalDateTime.class));
        obj.setAlimentoNome(rs.getString("alimento_nome"));
        return obj;
    }
}
