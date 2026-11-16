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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import sigca.servico.RegraNegocioException;
import sigca.servico.ServicoAutenticacao;

/**
 * Lembrete de senha — item 4.1 do gabarito.
 *
 * A recuperação é em dois passos: o usuário informa o e-mail, o sistema
 * devolve a pergunta de segurança que ele mesmo escolheu no cadastro, e
 * a nova senha só é aceita se a resposta conferir.
 *
 * NESTA VERSÃO NÃO HÁ ENVIO DE E-MAIL. O MVP roda local e não tem
 * serviço de correio; a pergunta de segurança cumpre o papel de provar
 * que quem pede a troca é o dono da conta. O envio por e-mail está
 * previsto para a versão entregue à Companhia das Obras.
 */
public class DialogoLembreteSenha extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient ServicoAutenticacao autenticacao = new ServicoAutenticacao();

    private final JTextField campoEmail = new JTextField(24);
    private final JLabel rotuloPergunta = new JLabel("—");
    private final JTextField campoResposta = new JTextField(24);
    private final JPasswordField campoNova = new JPasswordField(24);
    private final JPasswordField campoConfirmacao = new JPasswordField(24);

    private final JButton botaoBuscar = new JButton("Buscar pergunta");
    private final JButton botaoRedefinir = new JButton("Redefinir senha");

    // Montar a janela no construtor é o padrão do Swing; o aviso
    // this-escape não se aplica porque esta classe não é estendida.
    @SuppressWarnings("this-escape")
    public DialogoLembreteSenha(JFrame pai) {
        super(pai, "SIGCA — Lembrete de senha", true);
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel explicacao = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        explicacao.add(Telas.subtitulo(
                "A conferência é pela pergunta de segurança escolhida no cadastro."));
        add(explicacao, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        Telas.linhaDeFormulario(formulario, 0, "E-mail de acesso:", campoEmail);
        Telas.linhaDeFormulario(formulario, 1, "Pergunta:", rotuloPergunta);
        Telas.linhaDeFormulario(formulario, 2, "Resposta:", campoResposta);
        Telas.linhaDeFormulario(formulario, 3, "Nova senha:", campoNova);
        Telas.linhaDeFormulario(formulario, 4, "Confirme:", campoConfirmacao);
        add(formulario, BorderLayout.CENTER);

        // o segundo passo só abre depois que a pergunta foi encontrada
        habilitarSegundoPasso(false);

        botaoBuscar.addActionListener(evento -> buscarPergunta());
        botaoRedefinir.addActionListener(evento -> redefinir());
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(evento -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        botoes.add(botaoBuscar);
        botoes.add(botaoRedefinir);
        botoes.add(botaoFechar);
        add(botoes, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botaoBuscar);
        pack();
        Telas.centralizar(this);
    }

    private void habilitarSegundoPasso(boolean habilitado) {
        campoResposta.setEnabled(habilitado);
        campoNova.setEnabled(habilitado);
        campoConfirmacao.setEnabled(habilitado);
        botaoRedefinir.setEnabled(habilitado);
    }

    private void buscarPergunta() {
        try {
            String pergunta = autenticacao.perguntaDe(campoEmail.getText());
            rotuloPergunta.setText(pergunta);
            habilitarSegundoPasso(true);
            campoEmail.setEnabled(false);
            botaoBuscar.setEnabled(false);
            campoResposta.requestFocusInWindow();
            getRootPane().setDefaultButton(botaoRedefinir);
        } catch (RegraNegocioException e) {
            Telas.aviso(this, e.getMessage());
        }
    }

    private void redefinir() {
        String nova = new String(campoNova.getPassword());
        if (!nova.equals(new String(campoConfirmacao.getPassword()))) {
            Telas.aviso(this, "A nova senha e a confirmação não conferem.");
            return;
        }
        try {
            if (autenticacao.redefinirSenha(campoEmail.getText(),
                    campoResposta.getText(), nova)) {
                Telas.informacao(this, "Senha redefinida. "
                        + "Entre no sistema com a nova senha.");
                dispose();
            } else {
                Telas.erro(this, "Não foi possível redefinir a senha.");
            }
        } catch (RegraNegocioException e) {
            Telas.aviso(this, e.getMessage());
        }
    }
}
