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
import sigca.entities.PontoColeta;

/**
 * Repositório de PontoColeta — concentra todas as operações SQL da tabela ponto_coleta.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class PontoColetaRepository extends Conexao {

    public boolean inserir(PontoColeta obj) {
        String sql = "INSERT INTO ponto_coleta (unidade_org_id, municipio_id, nome, endereco, ativo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, obj.getUnidadeOrgId());
            ps.setInt(2, obj.getMunicipioId());
            ps.setString(3, obj.getNome());
            ps.setString(4, obj.getEndereco());
            ps.setBoolean(5, obj.isAtivo());
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
            System.out.println("Erro ao inserir em ponto_coleta: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(PontoColeta obj) {
        String sql = "UPDATE ponto_coleta SET unidade_org_id = ?, municipio_id = ?, nome = ?, endereco = ?, ativo = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getUnidadeOrgId());
            ps.setInt(2, obj.getMunicipioId());
            ps.setString(3, obj.getNome());
            ps.setString(4, obj.getEndereco());
            ps.setBoolean(5, obj.isAtivo());
            ps.setInt(6, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em ponto_coleta: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM ponto_coleta WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de ponto_coleta: " + e.getMessage());
            return false;
        }
    }

    public PontoColeta buscarPorId(int id) {
        String sql = """
                SELECT pc.*, m.nome AS municipio_nome, u.nome AS unidade_nome
                FROM ponto_coleta pc
                     INNER JOIN municipio m   ON m.id = pc.municipio_id
                     INNER JOIN unidade_org u ON u.id = pc.unidade_org_id
                 WHERE pc.id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em ponto_coleta: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<PontoColeta> listarTodos() {
        ArrayList<PontoColeta> lista = new ArrayList<>();
        String sql = """
                SELECT pc.*, m.nome AS municipio_nome, u.nome AS unidade_nome
                FROM ponto_coleta pc
                     INNER JOIN municipio m   ON m.id = pc.municipio_id
                     INNER JOIN unidade_org u ON u.id = pc.unidade_org_id
                 ORDER BY pc.nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar ponto_coleta: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE. O curinga percentual
     * é passado como parte do valor, nunca concatenado na query.
     */
    public ArrayList<PontoColeta> pesquisarPorNome(String textoPesquisa) {
        ArrayList<PontoColeta> lista = new ArrayList<>();
        String sql = """
                SELECT pc.*, m.nome AS municipio_nome, u.nome AS unidade_nome
                FROM ponto_coleta pc
                     INNER JOIN municipio m   ON m.id = pc.municipio_id
                     INNER JOIN unidade_org u ON u.id = pc.unidade_org_id
                 WHERE pc.nome ILIKE ?
                 ORDER BY pc.nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, textoPesquisa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar em ponto_coleta: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private PontoColeta ler(ResultSet rs) throws SQLException {
        PontoColeta obj = new PontoColeta(
                    rs.getInt("id"),
                    rs.getInt("unidade_org_id"),
                    rs.getInt("municipio_id"),
                    rs.getString("nome"),
                    rs.getString("endereco"),
                    rs.getBoolean("ativo"));
        obj.setMunicipioNome(rs.getString("municipio_nome"));
        obj.setUnidadeNome(rs.getString("unidade_nome"));
        return obj;
    }
}
