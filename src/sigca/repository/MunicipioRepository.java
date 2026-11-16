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
import sigca.entities.Municipio;

/**
 * Repositório de Municipio — concentra todas as operações SQL da tabela municipio.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class MunicipioRepository extends Conexao {

    public boolean inserir(Municipio obj) {
        String sql = "INSERT INTO municipio (nome, uf) VALUES (?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, obj.getNome());
            ps.setString(2, obj.getUf());
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
            System.out.println("Erro ao inserir em municipio: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(Municipio obj) {
        String sql = "UPDATE municipio SET nome = ?, uf = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, obj.getNome());
            ps.setString(2, obj.getUf());
            ps.setInt(3, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em municipio: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM municipio WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de municipio: " + e.getMessage());
            return false;
        }
    }

    public Municipio buscarPorId(int id) {
        String sql = """
                SELECT * FROM municipio
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em municipio: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Municipio> listarTodos() {
        ArrayList<Municipio> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM municipio
                 ORDER BY nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar municipio: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE. O curinga percentual
     * é passado como parte do valor, nunca concatenado na query.
     */
    public ArrayList<Municipio> pesquisarPorNome(String textoPesquisa) {
        ArrayList<Municipio> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM municipio
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
            System.out.println("Erro ao pesquisar em municipio: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private Municipio ler(ResultSet rs) throws SQLException {
        Municipio obj = new Municipio(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("uf"));
        return obj;
    }
}
