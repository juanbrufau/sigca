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
import java.time.LocalDateTime;
import java.util.ArrayList;
import sigca.conexao.Conexao;
import sigca.entities.Pessoa;

/**
 * Repositório de Pessoa — concentra todas as operações SQL da tabela pessoa.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class PessoaRepository extends Conexao {

    public boolean inserir(Pessoa obj) {
        String sql = "INSERT INTO pessoa (municipio_id, nome, nome_social, data_nascimento, telefone, email, bairro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            if (obj.getMunicipioId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, obj.getMunicipioId());
            }
            ps.setString(2, obj.getNome());
            ps.setString(3, obj.getNomeSocial());
            ps.setObject(4, obj.getDataNascimento());
            ps.setString(5, obj.getTelefone());
            ps.setString(6, obj.getEmail());
            ps.setString(7, obj.getBairro());
            boolean gravou = ps.executeUpdate() > 0;
            if (gravou) {
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        obj.setId(chaves.getInt(1));
                        try {
                            obj.setCriadoEm(chaves.getObject("criado_em", LocalDateTime.class));
                        } catch (SQLException ignorada) {
                            // nem todo driver devolve as demais colunas
                        }
                    }
                }
            }
            return gravou;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir em pessoa: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(Pessoa obj) {
        String sql = "UPDATE pessoa SET municipio_id = ?, nome = ?, nome_social = ?, data_nascimento = ?, telefone = ?, email = ?, bairro = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (obj.getMunicipioId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, obj.getMunicipioId());
            }
            ps.setString(2, obj.getNome());
            ps.setString(3, obj.getNomeSocial());
            ps.setObject(4, obj.getDataNascimento());
            ps.setString(5, obj.getTelefone());
            ps.setString(6, obj.getEmail());
            ps.setString(7, obj.getBairro());
            ps.setInt(8, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em pessoa: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM pessoa WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de pessoa: " + e.getMessage());
            return false;
        }
    }

    public Pessoa buscarPorId(int id) {
        String sql = """
                SELECT p.*, m.nome AS municipio_nome
                FROM pessoa p
                     LEFT JOIN municipio m ON m.id = p.municipio_id
                 WHERE p.id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em pessoa: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Pessoa> listarTodos() {
        ArrayList<Pessoa> lista = new ArrayList<>();
        String sql = """
                SELECT p.*, m.nome AS municipio_nome
                FROM pessoa p
                     LEFT JOIN municipio m ON m.id = p.municipio_id
                 ORDER BY p.nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pessoa: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE. O curinga percentual
     * é passado como parte do valor, nunca concatenado na query.
     */
    public ArrayList<Pessoa> pesquisarPorNome(String textoPesquisa) {
        ArrayList<Pessoa> lista = new ArrayList<>();
        String sql = """
                SELECT p.*, m.nome AS municipio_nome
                FROM pessoa p
                     LEFT JOIN municipio m ON m.id = p.municipio_id
                 WHERE p.nome ILIKE ?
                 ORDER BY p.nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, textoPesquisa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar em pessoa: " + e.getMessage());
        }
        return lista;
    }


    /**
     * Candidatos a cadastro duplicado: mesmo telefone, ou nome parecido.
     * A comparação é feita pelo banco, nunca iterando a lista em Java.
     */
    public ArrayList<Pessoa> listarSemelhantes(String nome, String telefone) {
        ArrayList<Pessoa> lista = new ArrayList<>();
        String sql = """
                SELECT p.*, m.nome AS municipio_nome
                  FROM pessoa p
                       LEFT JOIN municipio m ON m.id = p.municipio_id
                 WHERE p.telefone = ?
                    OR p.nome ILIKE ?
                 ORDER BY p.nome""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, telefone);
            ps.setString(2, nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao procurar semelhantes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private Pessoa ler(ResultSet rs) throws SQLException {
        Pessoa obj = new Pessoa(
                    rs.getInt("id"),
                    rs.getObject("municipio_id", Integer.class),
                    rs.getString("nome"),
                    rs.getString("nome_social"),
                    rs.getObject("data_nascimento", LocalDate.class),
                    rs.getString("telefone"),
                    rs.getString("email"),
                    rs.getString("bairro"),
                    rs.getObject("criado_em", LocalDateTime.class));
        obj.setMunicipioNome(rs.getString("municipio_nome"));
        return obj;
    }
}
