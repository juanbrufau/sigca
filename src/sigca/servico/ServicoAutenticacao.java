/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.servico;

import sigca.entities.Usuario;
import sigca.repository.UsuarioRepository;

/**
 * Entrada e saída do sistema, e recuperação de senha.
 *
 * A recuperação é feita por pergunta de segurança, escolhida no momento
 * do cadastre-se. O MVP roda local e não envia correio eletrônico; o
 * serviço de e-mail entra na versão entregue ao cliente.
 */
public class ServicoAutenticacao {

    private final UsuarioRepository repositorio = new UsuarioRepository();
    private final ServicoSeguranca seguranca = new ServicoSeguranca();

    /**
     * Autentica pelo e-mail de acesso e pela senha.
     *
     * A mensagem de erro é a mesma para e-mail inexistente e senha
     * errada, de propósito: dizer qual dos dois falhou entregaria a
     * quem tenta adivinhar a informação de que aquele e-mail existe.
     */
    public Usuario autenticar(String emailAcesso, String senha)
            throws RegraNegocioException {
        if (emailAcesso == null || emailAcesso.isBlank()
                || senha == null || senha.isBlank()) {
            throw new RegraNegocioException("Informe o e-mail e a senha.");
        }
        Usuario usuario = repositorio.buscarPorEmail(emailAcesso.trim().toLowerCase());
        if (usuario == null
                || !seguranca.conferir(usuario.getSenhaSal(),
                                       usuario.getSenhaHash(), senha)) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }
        if (!usuario.isAtivo()) {
            throw new RegraNegocioException(
                    "Este usuário está inativo. Procure o gestor da sua regional.");
        }
        repositorio.registrarAcesso(usuario.getId());
        return usuario;
    }

    /**
     * Primeiro passo do lembrete de senha: devolve a pergunta cadastrada.
     */
    public String perguntaDe(String emailAcesso) throws RegraNegocioException {
        Usuario usuario = repositorio.buscarPorEmail(
                emailAcesso == null ? "" : emailAcesso.trim().toLowerCase());
        if (usuario == null) {
            throw new RegraNegocioException(
                    "Não há usuário cadastrado com este e-mail.");
        }
        return usuario.getPerguntaSeguranca();
    }

    /**
     * Segundo passo: confere a resposta e troca a senha.
     */
    public boolean redefinirSenha(String emailAcesso, String resposta,
                                  String novaSenha) throws RegraNegocioException {
        Usuario usuario = repositorio.buscarPorEmail(
                emailAcesso == null ? "" : emailAcesso.trim().toLowerCase());
        if (usuario == null) {
            throw new RegraNegocioException(
                    "Não há usuário cadastrado com este e-mail.");
        }
        String normalizada = seguranca.normalizar(resposta);
        if (!seguranca.conferir(usuario.getSenhaSal(),
                                usuario.getRespostaHash(), normalizada)) {
            throw new RegraNegocioException("Resposta incorreta.");
        }
        validarSenha(novaSenha);
        String sal = seguranca.gerarSal();
        return repositorio.alterarSenha(usuario.getId(),
                seguranca.gerarHash(sal, novaSenha), sal);
    }

    /**
     * Troca de senha pelo próprio usuário, na tela "meu perfil".
     */
    public boolean alterarSenha(Usuario usuario, String senhaAtual,
                                String novaSenha) throws RegraNegocioException {
        if (!seguranca.conferir(usuario.getSenhaSal(),
                                usuario.getSenhaHash(), senhaAtual)) {
            throw new RegraNegocioException("A senha atual não confere.");
        }
        validarSenha(novaSenha);
        String sal = seguranca.gerarSal();
        String hash = seguranca.gerarHash(sal, novaSenha);
        boolean trocou = repositorio.alterarSenha(usuario.getId(), hash, sal);
        if (trocou) {
            usuario.setSenhaSal(sal);
            usuario.setSenhaHash(hash);
        }
        return trocou;
    }

    /**
     * Prepara o hash da senha e o da resposta de segurança para um
     * usuário novo. Os dois usam o mesmo sal.
     */
    public void prepararCredenciais(Usuario usuario, String senha,
                                    String resposta) throws RegraNegocioException {
        validarSenha(senha);
        if (resposta == null || resposta.isBlank()) {
            throw new RegraNegocioException(
                    "A resposta da pergunta de segurança é obrigatória.");
        }
        String sal = seguranca.gerarSal();
        usuario.setSenhaSal(sal);
        usuario.setSenhaHash(seguranca.gerarHash(sal, senha));
        usuario.setRespostaHash(seguranca.gerarHash(sal,
                seguranca.normalizar(resposta)));
    }

    private void validarSenha(String senha) throws RegraNegocioException {
        if (senha == null || senha.length() < 8) {
            throw new RegraNegocioException(
                    "A senha precisa ter pelo menos oito caracteres.");
        }
    }
}
