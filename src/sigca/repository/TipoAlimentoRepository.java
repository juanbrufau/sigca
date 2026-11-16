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
import sigca.entities.CategoriaAlimento;
import sigca.entities.TipoAlimento;

/**
 * Repositório de TipoAlimento — concentra todas as operações SQL da tabela tipo_alimento.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class TipoAlimentoRepository extends Conexao {

    public boolean inserir(TipoAlimento obj) {
        String sql = "INSERT INTO tipo_alimento (nome, categoria, ordem, ativo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, obj.getNome());
            ps.setString(2, obj.getCategoria() == null ? null : obj.getCategoria().name());
            ps.setInt(3, obj.getOrdem());
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
            System.out.println("Erro ao inserir em tipo_alimento: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(TipoAlimento obj) {
        String sql = "UPDATE tipo_alimento SET nome = ?, categoria = ?, ordem = ?, ativo = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, obj.getNome());
            ps.setString(2, obj.getCategoria() == null ? null : obj.getCategoria().name());
            ps.setInt(3, obj.getOrdem());
            ps.setBoolean(4, obj.isAtivo());
            ps.setInt(5, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em tipo_alimento: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM tipo_alimento WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de tipo_alimento: " + e.getMessage());
            return false;
        }
    }

    public TipoAlimento buscarPorId(int id) {
        String sql = """
                SELECT * FROM tipo_alimento
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em tipo_alimento: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<TipoAlimento> listarTodos() {
        ArrayList<TipoAlimento> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM tipo_alimento
                 ORDER BY nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar tipo_alimento: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE. O curinga percentual
     * é passado como parte do valor, nunca concatenado na query.
     */
    public ArrayList<TipoAlimento> pesquisarPorNome(String textoPesquisa) {
        ArrayList<TipoAlimento> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM tipo_alimento
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
            System.out.println("Erro ao pesquisar em tipo_alimento: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private TipoAlimento ler(ResultSet rs) throws SQLException {
        TipoAlimento obj = new TipoAlimento(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    Mapeador.paraEnum(CategoriaAlimento.class, rs.getString("categoria")),
                    rs.getInt("ordem"),
                    rs.getBoolean("ativo"));
        return obj;
    }
}
