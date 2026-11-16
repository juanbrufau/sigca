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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import sigca.entities.Usuario;
import sigca.servico.RegraNegocioException;
import sigca.servico.ServicoAutenticacao;

/**
 * Tela de entrada do sistema.
 *
 * Atende o item 4.1 do gabarito: login do usuário, com acesso ao
 * cadastre-se e ao lembrete de senha.
 *
 * A tela não sabe conferir senha — ela coleta o que foi digitado e
 * entrega ao ServicoAutenticacao. Se a regra recusar, vem uma
 * RegraNegocioException e a mensagem é mostrada ao usuário. É a mesma
 * separação do menu de console: interface coleta, serviço decide.
 */
public class TelaLogin extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient ServicoAutenticacao autenticacao = new ServicoAutenticacao();

    private final JTextField campoEmail = new JTextField(22);
    private final JPasswordField campoSenha = new JPasswordField(22);

    // Montar a janela no construtor é o padrão do Swing; o aviso
    // this-escape não se aplica porque esta classe não é estendida.
    @SuppressWarnings("this-escape")
    public TelaLogin() {
        super("SIGCA — Acesso");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(18, 20, 6, 20));
        cabecalho.add(Telas.titulo("SIGCA"));
        cabecalho.add(Box.createVerticalStrut(4));
        cabecalho.add(Telas.subtitulo("Sistema de Gestão da Coleta de Alimentos"));
        cabecalho.add(Telas.subtitulo("Companhia das Obras"));
        add(cabecalho, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        Telas.linhaDeFormulario(formulario, 0, "E-mail:", campoEmail);
        Telas.linhaDeFormulario(formulario, 1, "Senha:", campoSenha);
        add(formulario, BorderLayout.CENTER);

        JButton botaoEntrar = new JButton("Entrar");
        JButton botaoCadastro = new JButton("Cadastre-se");
        JButton botaoSenha = new JButton("Esqueci minha senha");

        botaoEntrar.addActionListener(evento -> entrar());
        botaoCadastro.addActionListener(evento -> abrirCadastro());
        botaoSenha.addActionListener(evento -> abrirLembreteDeSenha());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        botoes.add(botaoEntrar);
        botoes.add(botaoCadastro);
        botoes.add(botaoSenha);
        add(botoes, BorderLayout.SOUTH);

        // a tecla Enter aciona o botão Entrar
        getRootPane().setDefaultButton(botaoEntrar);

        pack();
        setResizable(false);
        Telas.centralizar(this);
    }

    private void entrar() {
        String email = campoEmail.getText();
        String senha = new String(campoSenha.getPassword());
        try {
            Usuario usuario = autenticacao.autenticar(email, senha);
            Sessao.iniciar(usuario);
            new TelaPrincipal().setVisible(true);
            dispose();
        } catch (RegraNegocioException e) {
            Telas.erro(this, e.getMessage());
            campoSenha.setText("");
            campoSenha.requestFocusInWindow();
        }
    }

    private void abrirCadastro() {
        new DialogoCadastro(this).setVisible(true);
    }

    private void abrirLembreteDeSenha() {
        new DialogoLembreteSenha(this).setVisible(true);
    }
}
