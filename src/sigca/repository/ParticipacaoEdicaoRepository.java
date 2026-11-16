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
import java.time.LocalTime;
import java.util.ArrayList;
import sigca.conexao.Conexao;
import sigca.entities.FaixaEtaria;
import sigca.entities.OrigemAlocacao;
import sigca.entities.PapelParticipacao;
import sigca.entities.ParticipacaoEdicao;

/**
 * Repositório de ParticipacaoEdicao — concentra todas as operações SQL da tabela participacao_edicao.
 *
 * Herda de Conexao: a conexão é estabelecida implicitamente ao instanciar
 * o repositório. Nenhuma query concatena texto; todos os valores entram
 * por PreparedStatement, o que previne SQL Injection.
 */
public class ParticipacaoEdicaoRepository extends Conexao {

    public boolean inserir(ParticipacaoEdicao obj) {
        String sql = "INSERT INTO participacao_edicao (edicao_id, pessoa_id, unidade_org_id, ponto_coleta_edicao_id, papel, faixa_etaria, disponivel_das, disponivel_ate, origem_alocacao, alocado_em) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, obj.getEdicaoId());
            ps.setInt(2, obj.getPessoaId());
            ps.setInt(3, obj.getUnidadeOrgId());
            if (obj.getPontoColetaEdicaoId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, obj.getPontoColetaEdicaoId());
            }
            ps.setString(5, obj.getPapel() == null ? null : obj.getPapel().name());
            ps.setString(6, obj.getFaixaEtaria() == null ? null : obj.getFaixaEtaria().name());
            ps.setObject(7, obj.getDisponivelDas());
            ps.setObject(8, obj.getDisponivelAte());
            ps.setString(9, obj.getOrigemAlocacao() == null ? null : obj.getOrigemAlocacao().name());
            ps.setObject(10, obj.getAlocadoEm());
            boolean gravou = ps.executeUpdate() > 0;
            if (gravou) {
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        obj.setId(chaves.getInt(1));
                        try {
                            obj.setInscritoEm(chaves.getObject("inscrito_em", LocalDateTime.class));
                        } catch (SQLException ignorada) {
                            // nem todo driver devolve as demais colunas
                        }
                    }
                }
            }
            return gravou;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir em participacao_edicao: " + e.getMessage());
            return false;
        }
    }

    public boolean alterar(ParticipacaoEdicao obj) {
        String sql = "UPDATE participacao_edicao SET edicao_id = ?, pessoa_id = ?, unidade_org_id = ?, ponto_coleta_edicao_id = ?, papel = ?, faixa_etaria = ?, disponivel_das = ?, disponivel_ate = ?, origem_alocacao = ?, alocado_em = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, obj.getEdicaoId());
            ps.setInt(2, obj.getPessoaId());
            ps.setInt(3, obj.getUnidadeOrgId());
            if (obj.getPontoColetaEdicaoId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, obj.getPontoColetaEdicaoId());
            }
            ps.setString(5, obj.getPapel() == null ? null : obj.getPapel().name());
            ps.setString(6, obj.getFaixaEtaria() == null ? null : obj.getFaixaEtaria().name());
            ps.setObject(7, obj.getDisponivelDas());
            ps.setObject(8, obj.getDisponivelAte());
            ps.setString(9, obj.getOrigemAlocacao() == null ? null : obj.getOrigemAlocacao().name());
            ps.setObject(10, obj.getAlocadoEm());
            ps.setInt(11, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar em participacao_edicao: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM participacao_edicao WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir de participacao_edicao: " + e.getMessage());
            return false;
        }
    }

    public ParticipacaoEdicao buscarPorId(int id) {
        String sql = """
                SELECT pa.*, COALESCE(p.nome_social, p.nome) AS pessoa_nome
                FROM participacao_edicao pa
                     INNER JOIN pessoa p ON p.id = pa.pessoa_id
                 WHERE pa.id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar em participacao_edicao: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<ParticipacaoEdicao> listarTodos() {
        ArrayList<ParticipacaoEdicao> lista = new ArrayList<>();
        String sql = """
                SELECT pa.*, COALESCE(p.nome_social, p.nome) AS pessoa_nome
                FROM participacao_edicao pa
                     INNER JOIN pessoa p ON p.id = pa.pessoa_id
                 ORDER BY pa.id""";
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar participacao_edicao: " + e.getMessage());
        }
        return lista;
    }


    /**
     * Inscritos da edição que ainda não foram alocados a nenhum ponto de
     * coleta — o pool da regional.
     */
    public ArrayList<ParticipacaoEdicao> listarNaoAlocados(int edicaoId) {
        return listarPorFiltro("""
                SELECT pa.*, COALESCE(p.nome_social, p.nome) AS pessoa_nome
                  FROM participacao_edicao pa
                       INNER JOIN pessoa p ON p.id = pa.pessoa_id
                 WHERE pa.edicao_id = ?
                   AND pa.ponto_coleta_edicao_id IS NULL
                 ORDER BY pa.inscrito_em""", edicaoId);
    }

    public ArrayList<ParticipacaoEdicao> listarPorPontoColetaEdicao(int pontoColetaEdicaoId) {
        return listarPorFiltro("""
                SELECT pa.*, COALESCE(p.nome_social, p.nome) AS pessoa_nome
                  FROM participacao_edicao pa
                       INNER JOIN pessoa p ON p.id = pa.pessoa_id
                 WHERE pa.ponto_coleta_edicao_id = ?
                 ORDER BY pessoa_nome""", pontoColetaEdicaoId);
    }

    private ArrayList<ParticipacaoEdicao> listarPorFiltro(String sql, int parametro) {
        ArrayList<ParticipacaoEdicao> lista = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar participações: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Uma pessoa se inscreve uma única vez por edição. A regra também está
     * no banco, como restrição UNIQUE; esta consulta existe para que a
     * mensagem ao usuário seja clara antes de o erro acontecer.
     */
    public boolean jaInscrito(int edicaoId, int pessoaId) {
        String sql = """
                SELECT 1 FROM participacao_edicao
                 WHERE edicao_id = ? AND pessoa_id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, edicaoId);
            ps.setInt(2, pessoaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar a inscrição: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aloca a participação a um ponto de coleta. Um voluntário atua numa
     * única loja por edição: alocar de novo substitui a alocação anterior.
     */
    public boolean alocar(int participacaoId, int pontoColetaEdicaoId,
                          String origem) {
        String sql = """
                UPDATE participacao_edicao
                   SET ponto_coleta_edicao_id = ?,
                       origem_alocacao = ?,
                       alocado_em = CURRENT_TIMESTAMP
                 WHERE id = ?""";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, pontoColetaEdicaoId);
            ps.setString(2, origem);
            ps.setInt(3, participacaoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alocar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Instancia a entidade a partir da linha corrente do ResultSet.
     */
    private ParticipacaoEdicao ler(ResultSet rs) throws SQLException {
        ParticipacaoEdicao obj = new ParticipacaoEdicao(
                    rs.getInt("id"),
                    rs.getInt("edicao_id"),
                    rs.getInt("pessoa_id"),
                    rs.getInt("unidade_org_id"),
                    rs.getObject("ponto_coleta_edicao_id", Integer.class),
                    Mapeador.paraEnum(PapelParticipacao.class, rs.getString("papel")),
                    Mapeador.paraEnum(FaixaEtaria.class, rs.getString("faixa_etaria")),
                    rs.getObject("disponivel_das", LocalTime.class),
                    rs.getObject("disponivel_ate", LocalTime.class),
                    Mapeador.paraEnum(OrigemAlocacao.class, rs.getString("origem_alocacao")),
                    rs.getObject("alocado_em", LocalDateTime.class),
                    rs.getObject("inscrito_em", LocalDateTime.class));
        obj.setPessoaNome(rs.getString("pessoa_nome"));
        return obj;
    }
}
