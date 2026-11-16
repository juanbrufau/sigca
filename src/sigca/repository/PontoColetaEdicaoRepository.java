/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import sigca.conexao.Conexao;
import sigca.entities.PontoColetaEdicao;
import sigca.entities.StatusNegociacao;

/**
 * Repositório de PontoColetaEdicao — concentra todas as operações SQL da tabela ponto_coleta_edicao.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class PontoColetaEdicaoRepository extends Conexao {

    public boolean inserir(PontoColetaEdicao obj) {
        String sql = "INSERT INTO ponto_coleta_edicao (edicao_id, ponto_coleta_id, nome_snapshot, endereco_snapshot, meta_voluntarios, status_negociacao, resultado_em, coordenador_participacao_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, obj.getEdicaoId());
            ps.setInt(2, obj.getPontoColetaId());
            ps.setString(3, obj.getNomeSnapshot());
            ps.setString(4, obj.getEnderecoSnapshot());
            ps.setInt(5, obj.getMetaVoluntarios());
            ps.setString(6, obj.getStatusNegociacao() == null ? null : obj.getStatusNegociacao().name());
            ps.setObject(7, obj.getResultadoEm());
            if (obj.getCoordenadorParticipacaoId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, obj.getCoordenadorParticipacaoId());
            }
            boolean gravou = ps.executeUpdate() > 0;
            if (gravou) {
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        obj.setId(chaves.getInt(1));
                    }
                }
            }
            return gravou;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir em ponto_coleta_edicao: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(PontoColetaEdicao obj) {
        String sql = "UPDATE ponto_coleta_edicao SET edicao_id = ?, ponto_coleta_id = ?, nome_snapshot = ?, endereco_snapshot = ?, meta_voluntarios = ?, status_negociacao = ?, resultado_em = ?, coordenador_participacao_id = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getEdicaoId());
            ps.setInt(2, obj.getPontoColetaId());
            ps.setString(3, obj.getNomeSnapshot());
            ps.setString(4, obj.getEnderecoSnapshot());
            ps.setInt(5, obj.getMetaVoluntarios());
            ps.setString(6, obj.getStatusNegociacao() == null ? null : obj.getStatusNegociacao().name());
            ps.setObject(7, obj.getResultadoEm());
            if (obj.getCoordenadorParticipacaoId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, obj.getCoordenadorParticipacaoId());
            }
            ps.setInt(9, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em ponto_coleta_edicao: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM ponto_coleta_edicao WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de ponto_coleta_edicao: " + e.getMessage());
            return false;
        }
    }

    public PontoColetaEdicao buscarPorId(int id) {
        String sql = """
                SELECT * FROM ponto_coleta_edicao
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em ponto_coleta_edicao: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<PontoColetaEdicao> listarTodos() {
        ArrayList<PontoColetaEdicao> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM ponto_coleta_edicao
                 ORDER BY nome_snapshot""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar ponto_coleta_edicao: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE. O curinga percentual
     * é passado como parte do valor, nunca concatenado na query.
     */
    public ArrayList<PontoColetaEdicao> pesquisarPorNome(String textoPesquisa) {
        ArrayList<PontoColetaEdicao> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM ponto_coleta_edicao
                 WHERE nome_snapshot ILIKE ?
                 ORDER BY nome_snapshot""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, textoPesquisa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar em ponto_coleta_edicao: " + e.getMessage());
        }
        return lista;
    }


    /**
     * Pontos de coleta de uma edição, já com o número de voluntários
     * alocados a cada um — é a consulta de cobertura do painel de
     * distribuição. A contagem é feita pelo banco, com junção externa e
     * agrupamento (Banco de Dados, aulas 13 e 14).
     */
    public ArrayList<PontoColetaEdicao> listarCobertura(int edicaoId) {
        ArrayList<PontoColetaEdicao> lista = new ArrayList<>();
        String sql = """
                SELECT pce.*, COUNT(pa.id) AS alocados
                  FROM ponto_coleta_edicao pce
                       LEFT JOIN participacao_edicao pa
                              ON pa.ponto_coleta_edicao_id = pce.id
                 WHERE pce.edicao_id = ?
                 GROUP BY pce.id
                 ORDER BY pce.nome_snapshot""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, edicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PontoColetaEdicao pce = ler(rs);
                    pce.setAlocados(rs.getInt("alocados"));
                    lista.add(pce);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar a cobertura: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<PontoColetaEdicao> listarPorEdicao(int edicaoId) {
        ArrayList<PontoColetaEdicao> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM ponto_coleta_edicao
                 WHERE edicao_id = ?
                 ORDER BY nome_snapshot""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, edicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pontos da edição: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Designa o coordenador da loja. É um UPDATE isolado porque a coluna
     * só pode ser preenchida depois de a participação existir.
     */
    public boolean designarCoordenador(int pontoColetaEdicaoId, int participacaoId) {
        String sql = """
                UPDATE ponto_coleta_edicao
                   SET coordenador_participacao_id = ?
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, participacaoId);
            ps.setInt(2, pontoColetaEdicaoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao designar o coordenador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private PontoColetaEdicao ler(ResultSet rs) throws SQLException {
        PontoColetaEdicao obj = new PontoColetaEdicao(
                    rs.getInt("id"),
                    rs.getInt("edicao_id"),
                    rs.getInt("ponto_coleta_id"),
                    rs.getString("nome_snapshot"),
                    rs.getString("endereco_snapshot"),
                    rs.getInt("meta_voluntarios"),
                    Mapeador.paraEnum(StatusNegociacao.class, rs.getString("status_negociacao")),
                    rs.getObject("resultado_em", LocalDateTime.class),
                    rs.getObject("coordenador_participacao_id", Integer.class));
        return obj;
    }
}
