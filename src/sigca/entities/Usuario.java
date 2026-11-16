/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Usuario — espelha a tabela usuario do banco de dados.
 *
 * É a especialização de Pessoa: quem tem credencial de acesso ao sistema.
 * O voluntário comum é Pessoa e não é Usuario. No banco, a chave primária
 * de usuario é a própria chave de pessoa — é isso que torna a herança
 * total, e é o que a aula 6 de Banco de Dados chama de especialização.
 *
 * Em Java a mesma ideia aparece como herança (extends), conteúdo da
 * aula 9 de Linguagem de Programação I.
 */
public class Usuario extends Pessoa {

    private int unidadeOrgId;
    private String emailAcesso;
    private String senhaHash;
    private String senhaSal;
    private String perguntaSeguranca;
    private String respostaHash;
    private PerfilSistema perfilSistema;
    private boolean ativo;
    private LocalDateTime ultimoAcessoEm;

    // Campo de apresentação: não existe como coluna na tabela.
    private String unidadeNome = null;

    public Usuario() {
        super();
    }

    public Usuario(int id, Integer municipioId, String nome, String nomeSocial,
                   LocalDate dataNascimento, String telefone, String email,
                   String bairro, LocalDateTime criadoEm, int unidadeOrgId,
                   String emailAcesso, String senhaHash, String senhaSal,
                   String perguntaSeguranca, String respostaHash,
                   PerfilSistema perfilSistema, boolean ativo,
                   LocalDateTime ultimoAcessoEm) {
        super(id, municipioId, nome, nomeSocial, dataNascimento, telefone,
              email, bairro, criadoEm);
        this.unidadeOrgId = unidadeOrgId;
        this.emailAcesso = emailAcesso;
        this.senhaHash = senhaHash;
        this.senhaSal = senhaSal;
        this.perguntaSeguranca = perguntaSeguranca;
        this.respostaHash = respostaHash;
        this.perfilSistema = perfilSistema;
        this.ativo = ativo;
        this.ultimoAcessoEm = ultimoAcessoEm;
    }

    /**
     * Nome que deve aparecer nas telas: o social quando houver.
     */
    public String getNomeExibicao() {
        if (getNomeSocial() != null && !getNomeSocial().isBlank()) {
            return getNomeSocial();
        }
        return getNome();
    }

    public boolean podeGerenciarRegional() {
        return perfilSistema == PerfilSistema.ADMINISTRADOR_NACIONAL
            || perfilSistema == PerfilSistema.GESTOR_REGIONAL;
    }

    public int getUnidadeOrgId() {
        return unidadeOrgId;
    }

    public void setUnidadeOrgId(int unidadeOrgId) {
        this.unidadeOrgId = unidadeOrgId;
    }

    public String getEmailAcesso() {
        return emailAcesso;
    }

    public void setEmailAcesso(String emailAcesso) {
        this.emailAcesso = emailAcesso;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getSenhaSal() {
        return senhaSal;
    }

    public void setSenhaSal(String senhaSal) {
        this.senhaSal = senhaSal;
    }

    public String getPerguntaSeguranca() {
        return perguntaSeguranca;
    }

    public void setPerguntaSeguranca(String perguntaSeguranca) {
        this.perguntaSeguranca = perguntaSeguranca;
    }

    public String getRespostaHash() {
        return respostaHash;
    }

    public void setRespostaHash(String respostaHash) {
        this.respostaHash = respostaHash;
    }

    public PerfilSistema getPerfilSistema() {
        return perfilSistema;
    }

    public void setPerfilSistema(PerfilSistema perfilSistema) {
        this.perfilSistema = perfilSistema;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getUltimoAcessoEm() {
        return ultimoAcessoEm;
    }

    public void setUltimoAcessoEm(LocalDateTime ultimoAcessoEm) {
        this.ultimoAcessoEm = ultimoAcessoEm;
    }

    public String getUnidadeNome() {
        return unidadeNome;
    }

    public void setUnidadeNome(String unidadeNome) {
        this.unidadeNome = unidadeNome;
    }

    /**
     * Sobrescrita da sobrescrita: Pessoa já define toString(), e Usuario
     * acrescenta o perfil e a unidade de escopo (polimorfismo, aula 16).
     */
    @Override
    public String toString() {
        return String.format("Id: %d | Nome: %s | E-mail: %s | Perfil: %s | Unidade: %s | Ativo: %s",
                getId(), getNomeExibicao(), emailAcesso, perfilSistema,
                unidadeNome == null ? unidadeOrgId : unidadeNome,
                ativo ? "sim" : "não");
    }
}
