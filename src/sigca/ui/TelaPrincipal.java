/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import sigca.entities.Usuario;

/**
 * Tela principal do sistema, aberta logo após o login.
 *
 * Atende o item 4.1 do gabarito. O menu é montado conforme o perfil do
 * usuário: o Coordenador de Loja não enxerga as funções de gestão, e
 * essa é a diferença de visualização que o enunciado pede entre os dois
 * tipos de usuário.
 */
public class TelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    // Montar a janela no construtor é o padrão do Swing; o aviso
    // this-escape não se aplica porque esta classe não é estendida.
    @SuppressWarnings("this-escape")
    public TelaPrincipal() {
        super("SIGCA — Menu principal");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        Usuario usuario = Sessao.getAtual();

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        cabecalho.add(Telas.titulo("SIGCA — Sistema de Gestão da Coleta de Alimentos"));
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(Telas.subtitulo("Usuário: " + usuario.getNomeExibicao()));
        cabecalho.add(Telas.subtitulo("Perfil: "
                + usuario.getPerfilSistema().getDescricao()
                + "   ·   Unidade: "
                + (usuario.getUnidadeNome() == null
                   ? String.valueOf(usuario.getUnidadeOrgId())
                   : usuario.getUnidadeNome())));
        add(cabecalho, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JButton botaoPessoas = new JButton("Cadastro de pessoas");
        botaoPessoas.addActionListener(evento -> new TelaPessoa(this).setVisible(true));
        menu.add(botaoPessoas);

        JButton botaoPerfil = new JButton("Meu perfil");
        botaoPerfil.addActionListener(evento ->
                new DialogoMeuPerfil(this).setVisible(true));
        menu.add(botaoPerfil);

        if (usuario.podeGerenciarRegional()) {
            JButton botaoGestao = new JButton("Pontos de coleta, distribuição e resultado");
            botaoGestao.setToolTipText("Disponível no menu de console desta versão");
            botaoGestao.addActionListener(evento -> Telas.informacao(this,
                    "Estas funções estão disponíveis no menu de console do sistema.\n\n"
                    + "A versão gráfica cobre as telas exigidas no item 4.1 do\n"
                    + "gabarito: login, tela principal, cadastro modelo,\n"
                    + "cadastre-se, meu perfil e lembrete de senha."));
            menu.add(botaoGestao);
        }

        add(menu, BorderLayout.CENTER);

        JButton botaoSair = new JButton("Sair");
        botaoSair.addActionListener(evento -> sair());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rodape.add(botaoSair);
        add(rodape, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(520, 360));
        pack();
        Telas.centralizar(this);
    }

    private void sair() {
        if (!Telas.confirmar(this, "Encerrar a sessão?")) {
            return;
        }
        Sessao.encerrar();
        new TelaLogin().setVisible(true);
        dispose();
    }
}
