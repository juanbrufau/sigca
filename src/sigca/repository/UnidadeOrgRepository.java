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
import java.util.ArrayList;
import sigca.conexao.Conexao;
import sigca.entities.TipoUnidade;
import sigca.entities.UnidadeOrg;

/**
 * Repositório de UnidadeOrg — concentra todas as operações SQL da tabela unidade_org.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class UnidadeOrgRepository extends Conexao {

    public boolean inserir(UnidadeOrg obj) {
        String sql = "INSERT INTO unidade_org (unidade_org_pai_id, tipo, nome, ativo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            if (obj.getUnidadeOrgPaiId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, obj.getUnidadeOrgPaiId());
            }
            ps.setString(2, obj.getTipo() == null ? null : obj.getTipo().name());
            ps.setString(3, obj.getNome());
            ps.setBoolean(4, obj.isAtivo());
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
            System.out.println("Erro ao inserir em unidade_org: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(UnidadeOrg obj) {
        String sql = "UPDATE unidade_org SET unidade_org_pai_id = ?, tipo = ?, nome = ?, ativo = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (obj.getUnidadeOrgPaiId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, obj.getUnidadeOrgPaiId());
            }
            ps.setString(2, obj.getTipo() == null ? null : obj.getTipo().name());
            ps.setString(3, obj.getNome());
            ps.setBoolean(4, obj.isAtivo());
            ps.setInt(5, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em unidade_org: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM unidade_org WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de unidade_org: " + e.getMessage());
            return false;
        }
    }

    public UnidadeOrg buscarPorId(int id) {
        String sql = """
                SELECT * FROM unidade_org
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em unidade_org: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<UnidadeOrg> listarTodos() {
        ArrayList<UnidadeOrg> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM unidade_org
                 ORDER BY nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar unidade_org: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE. O curinga percentual
     * é passado como parte do valor, nunca concatenado na query.
     */
    public ArrayList<UnidadeOrg> pesquisarPorNome(String textoPesquisa) {
        ArrayList<UnidadeOrg> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM unidade_org
                 WHERE nome ILIKE ?
                 ORDER BY nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, textoPesquisa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar em unidade_org: " + e.getMessage());
        }
        return lista;
    }


    /**
     * Subunidades diretas de uma unidade. É a consulta que o ServicoEscopo
     * chama recursivamente para percorrer a árvore inteira.
     */
    public ArrayList<UnidadeOrg> listarPorPai(int unidadeOrgPaiId) {
        ArrayList<UnidadeOrg> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM unidade_org
                 WHERE unidade_org_pai_id = ?
                 ORDER BY nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, unidadeOrgPaiId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar subunidades: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<UnidadeOrg> listarRaizes() {
        ArrayList<UnidadeOrg> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM unidade_org
                 WHERE unidade_org_pai_id IS NULL
                 ORDER BY nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar raízes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private UnidadeOrg ler(ResultSet rs) throws SQLException {
        UnidadeOrg obj = new UnidadeOrg(
                    rs.getInt("id"),
                    rs.getObject("unidade_org_pai_id", Integer.class),
                    Mapeador.paraEnum(TipoUnidade.class, rs.getString("tipo")),
                    rs.getString("nome"),
                    rs.getBoolean("ativo"));
        return obj;
    }
}
