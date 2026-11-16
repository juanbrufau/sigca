/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import sigca.conexao.Conexao;
import sigca.entities.PerfilSistema;
import sigca.entities.Usuario;

/**
 * Repositório de Usuario — concentra as operações SQL da tabela usuario.
 *
 * Usuario é a especialização de Pessoa, e no banco são duas tabelas
 * ligadas pela mesma chave. Por isso toda leitura aqui faz a junção das
 * duas, e a inserção grava primeiro a pessoa e depois o usuário.
 */
public class UsuarioRepository extends Conexao {

    private static final String SELECT_BASE = """
            SELECT u.pessoa_id, u.unidade_org_id, u.email_acesso, u.senha_hash,
                   u.senha_sal, u.pergunta_seguranca, u.resposta_hash,
                   u.perfil_sistema, u.ativo, u.ultimo_acesso_em,
                   p.municipio_id, p.nome, p.nome_social, p.data_nascimento,
                   p.telefone, p.email, p.bairro, p.criado_em,
                   o.nome AS unidade_nome
              FROM usuario u
                   INNER JOIN pessoa p      ON p.id = u.pessoa_id
                   INNER JOIN unidade_org o ON o.id = u.unidade_org_id""";

    /**
     * Grava o usuário de uma pessoa já cadastrada. A pessoa precisa
     * existir: é ela quem fornece a chave primária.
     */
    public boolean inserir(Usuario obj) {
        String sql = """
                INSERT INTO usuario
                    (pessoa_id, unidade_org_id, email_acesso, senha_hash,
                     senha_sal, pergunta_seguranca, resposta_hash,
                     perfil_sistema, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getId());
            ps.setInt(2, obj.getUnidadeOrgId());
            ps.setString(3, obj.getEmailAcesso());
            ps.setString(4, obj.getSenhaHash());
            ps.setString(5, obj.getSenhaSal());
            ps.setString(6, obj.getPerguntaSeguranca());
            ps.setString(7, obj.getRespostaHash());
            ps.setString(8, obj.getPerfilSistema() == null ? null
                    : obj.getPerfilSistema().name());
            ps.setBoolean(9, obj.isAtivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(Usuario obj) {
        String sql = """
                UPDATE usuario
                   SET unidade_org_id = ?, email_acesso = ?,
                       pergunta_seguranca = ?, perfil_sistema = ?, ativo = ?
                 WHERE pessoa_id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getUnidadeOrgId());
            ps.setString(2, obj.getEmailAcesso());
            ps.setString(3, obj.getPerguntaSeguranca());
            ps.setString(4, obj.getPerfilSistema() == null ? null
                    : obj.getPerfilSistema().name());
            ps.setBoolean(5, obj.isAtivo());
            ps.setInt(6, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Troca a senha. O hash e o sal vêm prontos do ServicoSeguranca: o
     * repositório nunca vê a senha em texto.
     */
    public boolean alterarSenha(int pessoaId, String senhaHash, String senhaSal) {
        String sql = "UPDATE usuario SET senha_hash = ?, senha_sal = ? WHERE pessoa_id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, senhaHash);
            ps.setString(2, senhaSal);
            ps.setInt(3, pessoaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar a senha: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarAcesso(int pessoaId) {
        String sql = "UPDATE usuario SET ultimo_acesso_em = CURRENT_TIMESTAMP WHERE pessoa_id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pessoaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao registrar o acesso: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int pessoaId) {
        String sql = "DELETE FROM usuario WHERE pessoa_id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pessoaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorEmail(String emailAcesso) {
        String sql = SELECT_BASE + "\n WHERE u.email_acesso = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, emailAcesso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorId(int pessoaId) {
        String sql = SELECT_BASE + "\n WHERE u.pessoa_id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pessoaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Usuario> listarTodos() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = SELECT_BASE + "\n ORDER BY p.nome";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Pesquisa delegada ao banco pela cláusula LIKE.
     */
    public ArrayList<Usuario> pesquisarPorNome(String textoPesquisa) {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = SELECT_BASE + "\n WHERE p.nome ILIKE ?\n ORDER BY p.nome";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, textoPesquisa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar usuários: " + e.getMessage());
        }
        return lista;
    }

    private Usuario ler(ResultSet rs) throws SQLException {
        Usuario obj = new Usuario(
                    rs.getInt("pessoa_id"),
                    rs.getObject("municipio_id", Integer.class),
                    rs.getString("nome"),
                    rs.getString("nome_social"),
                    rs.getObject("data_nascimento", LocalDate.class),
                    rs.getString("telefone"),
                    rs.getString("email"),
                    rs.getString("bairro"),
                    rs.getObject("criado_em", LocalDateTime.class),
                    rs.getInt("unidade_org_id"),
                    rs.getString("email_acesso"),
                    rs.getString("senha_hash"),
                    rs.getString("senha_sal"),
                    rs.getString("pergunta_seguranca"),
                    rs.getString("resposta_hash"),
                    Mapeador.paraEnum(PerfilSistema.class, rs.getString("perfil_sistema")),
                    rs.getBoolean("ativo"),
                    rs.getObject("ultimo_acesso_em", LocalDateTime.class));
        obj.setUnidadeNome(rs.getString("unidade_nome"));
        return obj;
    }
}
