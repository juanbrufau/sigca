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
import java.time.LocalDate;
import java.util.ArrayList;
import sigca.conexao.Conexao;
import sigca.entities.Edicao;
import sigca.entities.StatusEdicao;

/**
 * Repositório de Edicao — concentra todas as operações SQL da tabela edicao.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class EdicaoRepository extends Conexao {

    public boolean inserir(Edicao obj) {
        String sql = "INSERT INTO edicao (ano, data_coleta, inscricoes_inicio, inscricoes_fim, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, obj.getAno());
            ps.setObject(2, obj.getDataColeta());
            ps.setObject(3, obj.getInscricoesInicio());
            ps.setObject(4, obj.getInscricoesFim());
            ps.setString(5, obj.getStatus() == null ? null : obj.getStatus().name());
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
            System.out.println("Erro ao inserir em edicao: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(Edicao obj) {
        String sql = "UPDATE edicao SET ano = ?, data_coleta = ?, inscricoes_inicio = ?, inscricoes_fim = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getAno());
            ps.setObject(2, obj.getDataColeta());
            ps.setObject(3, obj.getInscricoesInicio());
            ps.setObject(4, obj.getInscricoesFim());
            ps.setString(5, obj.getStatus() == null ? null : obj.getStatus().name());
            ps.setInt(6, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em edicao: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM edicao WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de edicao: " + e.getMessage());
            return false;
        }
    }

    public Edicao buscarPorId(int id) {
        String sql = """
                SELECT * FROM edicao
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em edicao: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Edicao> listarTodos() {
        ArrayList<Edicao> lista = new ArrayList<>();
        String sql = """
                SELECT * FROM edicao
                 ORDER BY id""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar edicao: " + e.getMessage());
        }
        return lista;
    }


    /**
     * Edição corrente: a mais recente que ainda não foi encerrada.
     */
    public Edicao buscarCorrente() {
        String sql = """
                SELECT * FROM edicao
                 WHERE status <> 'ENCERRADA'
                 ORDER BY ano DESC
                 LIMIT 1""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return ler(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar a edição corrente: " + e.getMessage());
        }
        return null;
    }

    public Edicao buscarPorAno(int ano) {
        String sql = "SELECT * FROM edicao WHERE ano = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, ano);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar a edição: " + e.getMessage());
        }
        return null;
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private Edicao ler(ResultSet rs) throws SQLException {
        Edicao obj = new Edicao(
                    rs.getInt("id"),
                    rs.getInt("ano"),
                    rs.getObject("data_coleta", LocalDate.class),
                    rs.getObject("inscricoes_inicio", LocalDate.class),
                    rs.getObject("inscricoes_fim", LocalDate.class),
                    Mapeador.paraEnum(StatusEdicao.class, rs.getString("status")));
        return obj;
    }
}
