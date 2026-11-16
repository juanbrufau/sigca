/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import sigca.entities.Usuario;
import sigca.repository.PessoaRepository;
import sigca.servico.RegraNegocioException;
import sigca.servico.ServicoAutenticacao;

/**
 * Meu perfil — item 4.1 do gabarito.
 *
 * O usuário altera os próprios dados de contato e troca a senha. O que
 * ele NÃO altera aqui é o perfil de sistema nem a unidade de escopo:
 * quem decide o que uma pessoa pode ver é a gestão, não ela mesma.
 */
public class DialogoMeuPerfil extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient PessoaRepository pessoas = new PessoaRepository();
    private final transient ServicoAutenticacao autenticacao = new ServicoAutenticacao();

    private final JTextField campoTelefone = new JTextField(22);
    private final JTextField campoEmail = new JTextField(22);
    private final JTextField campoBairro = new JTextField(22);

    private final JPasswordField campoSenhaAtual = new JPasswordField(22);
    private final JPasswordField campoSenhaNova = new JPasswordField(22);
    private final JPasswordField campoConfirmacao = new JPasswordField(22);

    // Montar a janela no construtor é o padrão do Swing; o aviso
    // this-escape não se aplica porque esta classe não é estendida.
    @SuppressWarnings("this-escape")
    public DialogoMeuPerfil(JFrame pai) {
        super(pai, "SIGCA — Meu perfil", true);
        montarTela();
        carregarDados();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        Usuario usuario = Sessao.getAtual();

        JPanel identificacao = new JPanel(new GridBagLayout());
        identificacao.setBorder(BorderFactory.createTitledBorder("Identificação"));
        identificacao.add(Telas.subtitulo("Nome: " + usuario.getNomeExibicao()),
                Telas.posicao(0, 0, true));
        identificacao.add(Telas.subtitulo("E-mail de acesso: "
                + usuario.getEmailAcesso()), Telas.posicao(0, 1, true));
        identificacao.add(Telas.subtitulo("Perfil: "
                + usuario.getPerfilSistema().getDescricao()),
                Telas.posicao(0, 2, true));

        JPanel contato = new JPanel(new GridBagLayout());
        contato.setBorder(BorderFactory.createTitledBorder("Dados de contato"));
        Telas.linhaDeFormulario(contato, 0, "Telefone:", campoTelefone);
        Telas.linhaDeFormulario(contato, 1, "E-mail:", campoEmail);
        Telas.linhaDeFormulario(contato, 2, "Bairro:", campoBairro);

        JPanel senha = new JPanel(new GridBagLayout());
        senha.setBorder(BorderFactory.createTitledBorder(
                "Trocar a senha (deixe em branco para manter)"));
        Telas.linhaDeFormulario(senha, 0, "Senha atual:", campoSenhaAtual);
        Telas.linhaDeFormulario(senha, 1, "Nova senha:", campoSenhaNova);
        Telas.linhaDeFormulario(senha, 2, "Confirme:", campoConfirmacao);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 14));
        centro.add(identificacao, BorderLayout.NORTH);
        centro.add(contato, BorderLayout.CENTER);
        centro.add(senha, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoFechar = new JButton("Fechar");
        botaoSalvar.addActionListener(evento -> salvar());
        botaoFechar.addActionListener(evento -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        botoes.add(botaoSalvar);
        botoes.add(botaoFechar);
        add(botoes, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botaoSalvar);
        pack();
        Telas.centralizar(this);
    }

    private void carregarDados() {
        Usuario usuario = Sessao.getAtual();
        campoTelefone.setText(usuario.getTelefone() == null
                ? "" : usuario.getTelefone());
        campoEmail.setText(usuario.getEmail() == null ? "" : usuario.getEmail());
        campoBairro.setText(usuario.getBairro() == null ? "" : usuario.getBairro());
    }

    private void salvar() {
        Usuario usuario = Sessao.getAtual();

        // ---- dados de contato: gravados na tabela pessoa
        usuario.setTelefone(opcional(campoTelefone.getText()));
        usuario.setEmail(opcional(campoEmail.getText()));
        usuario.setBairro(opcional(campoBairro.getText()));

        if (!pessoas.alterar(usuario)) {
            Telas.erro(this, "Não foi possível gravar os dados de contato.");
            return;
        }

        // ---- senha: só quando os campos foram preenchidos
        String nova = new String(campoSenhaNova.getPassword());
        if (!nova.isEmpty()) {
            if (!nova.equals(new String(campoConfirmacao.getPassword()))) {
                Telas.aviso(this, "A nova senha e a confirmação não conferem.");
                return;
            }
            try {
                autenticacao.alterarSenha(usuario,
                        new String(campoSenhaAtual.getPassword()), nova);
            } catch (RegraNegocioException e) {
                Telas.aviso(this, e.getMessage());
                return;
            }
            Telas.informacao(this, "Dados e senha atualizados.");
            dispose();
            return;
        }

        Telas.informacao(this, "Dados de contato atualizados.");
        dispose();
    }

    private String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
