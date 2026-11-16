/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import sigca.entities.Municipio;
import sigca.entities.PerfilSistema;
import sigca.entities.Pessoa;
import sigca.entities.UnidadeOrg;
import sigca.entities.Usuario;
import sigca.repository.MunicipioRepository;
import sigca.repository.PessoaRepository;
import sigca.repository.UnidadeOrgRepository;
import sigca.repository.UsuarioRepository;
import sigca.servico.RegraNegocioException;
import sigca.servico.ServicoAutenticacao;

/**
 * Cadastre-se — item 4.1 do gabarito.
 *
 * Grava em duas tabelas, nesta ordem: primeiro a pessoa, depois o
 * usuário. A ordem não é escolha de estilo: a chave primária de usuario
 * é a mesma de pessoa, então sem a pessoa gravada não existe código
 * para o usuário usar.
 *
 * O cadastro nasce INATIVO. Quem libera o acesso é o gestor da
 * regional — uma pessoa não se autoriza a entrar no sistema.
 */
public class DialogoCadastro extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient PessoaRepository pessoas = new PessoaRepository();
    private final transient UsuarioRepository usuarios = new UsuarioRepository();
    private final transient ServicoAutenticacao autenticacao = new ServicoAutenticacao();

    private final JTextField campoNome = new JTextField(24);
    private final JTextField campoNomeSocial = new JTextField(24);
    private final JTextField campoNascimento = new JTextField(24);
    private final JTextField campoTelefone = new JTextField(24);
    private final JTextField campoBairro = new JTextField(24);
    private final transient JComboBox<Municipio> comboMunicipio;

    private final JTextField campoEmailAcesso = new JTextField(24);
    private final transient JComboBox<UnidadeOrg> comboUnidade;
    private final JTextField campoPergunta = new JTextField(24);
    private final JTextField campoResposta = new JTextField(24);
    private final JPasswordField campoSenha = new JPasswordField(24);
    private final JPasswordField campoConfirmacao = new JPasswordField(24);

    // Montar a janela no construtor é o padrão do Swing; o aviso
    // this-escape não se aplica porque esta classe não é estendida.
    @SuppressWarnings("this-escape")
    public DialogoCadastro(JFrame pai) {
        super(pai, "SIGCA — Cadastre-se", true);
        this.comboMunicipio = Combos.deMunicipios(new MunicipioRepository().listarTodos());
        this.comboUnidade = Combos.deUnidades(new UnidadeOrgRepository().listarTodos());
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel dados = new JPanel(new GridBagLayout());
        dados.setBorder(BorderFactory.createTitledBorder("Seus dados"));
        Telas.linhaDeFormulario(dados, 0, "Nome completo:", campoNome);
        Telas.linhaDeFormulario(dados, 1, "Nome social:", campoNomeSocial);
        Telas.linhaDeFormulario(dados, 2, "Nascimento:", campoNascimento);
        Telas.linhaDeFormulario(dados, 3, "Telefone:", campoTelefone);
        Telas.linhaDeFormulario(dados, 4, "Bairro:", campoBairro);
        Telas.linhaDeFormulario(dados, 5, "Município:", comboMunicipio);
        campoNascimento.setToolTipText("dd/mm/aaaa");

        JPanel acesso = new JPanel(new GridBagLayout());
        acesso.setBorder(BorderFactory.createTitledBorder("Acesso ao sistema"));
        Telas.linhaDeFormulario(acesso, 0, "E-mail de acesso:", campoEmailAcesso);
        Telas.linhaDeFormulario(acesso, 1, "Unidade:", comboUnidade);
        Telas.linhaDeFormulario(acesso, 2, "Senha:", campoSenha);
        Telas.linhaDeFormulario(acesso, 3, "Confirme a senha:", campoConfirmacao);
        Telas.linhaDeFormulario(acesso, 4, "Pergunta de segurança:", campoPergunta);
        Telas.linhaDeFormulario(acesso, 5, "Resposta:", campoResposta);
        campoSenha.setToolTipText("Mínimo de oito caracteres");
        campoPergunta.setToolTipText(
                "Exemplo: nome da primeira escola, cidade onde nasceu");

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setBorder(BorderFactory.createEmptyBorder(10, 14, 0, 14));
        centro.add(dados, BorderLayout.NORTH);
        centro.add(acesso, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel aviso = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 2));
        aviso.add(Telas.subtitulo("O acesso fica pendente de aprovação pelo gestor "
                + "da regional."));
        add(aviso, BorderLayout.NORTH);

        JButton botaoGravar = new JButton("Cadastrar");
        JButton botaoCancelar = new JButton("Cancelar");
        botaoGravar.addActionListener(evento -> cadastrar());
        botaoCancelar.addActionListener(evento -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        botoes.add(botaoGravar);
        botoes.add(botaoCancelar);
        add(botoes, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botaoGravar);
        pack();
        Telas.centralizar(this);
    }

    private void cadastrar() {
        // ---- validações que a tela resolve sozinha
        if (campoNome.getText().isBlank()) {
            Telas.aviso(this, "O nome é obrigatório.");
            return;
        }
        if (campoEmailAcesso.getText().isBlank()) {
            Telas.aviso(this, "O e-mail de acesso é obrigatório.");
            return;
        }
        LocalDate nascimento;
        try {
            nascimento = Telas.lerData(campoNascimento.getText());
        } catch (DateTimeParseException e) {
            Telas.aviso(this, "Data de nascimento inválida. Use dd/mm/aaaa.");
            return;
        }
        if (nascimento == null) {
            Telas.aviso(this, "A data de nascimento é obrigatória.");
            return;
        }
        String senha = new String(campoSenha.getPassword());
        if (!senha.equals(new String(campoConfirmacao.getPassword()))) {
            Telas.aviso(this, "A senha e a confirmação não conferem.");
            return;
        }
        UnidadeOrg unidade = (UnidadeOrg) comboUnidade.getSelectedItem();
        if (unidade == null) {
            Telas.aviso(this, "Escolha a unidade organizacional.");
            return;
        }
        if (usuarios.buscarPorEmail(campoEmailAcesso.getText().trim().toLowerCase())
                != null) {
            Telas.aviso(this, "Já existe um usuário com este e-mail de acesso.");
            return;
        }

        // ---- primeiro a pessoa
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(campoNome.getText().trim());
        pessoa.setNomeSocial(opcional(campoNomeSocial.getText()));
        pessoa.setDataNascimento(nascimento);
        pessoa.setTelefone(opcional(campoTelefone.getText()));
        pessoa.setEmail(campoEmailAcesso.getText().trim().toLowerCase());
        pessoa.setBairro(opcional(campoBairro.getText()));
        Municipio municipio = (Municipio) comboMunicipio.getSelectedItem();
        pessoa.setMunicipioId(municipio == null ? null : municipio.getId());

        if (!pessoas.inserir(pessoa)) {
            Telas.erro(this, "Não foi possível gravar os seus dados.");
            return;
        }

        // ---- depois o usuário, com a mesma chave
        Usuario usuario = new Usuario();
        usuario.setId(pessoa.getId());
        usuario.setNome(pessoa.getNome());
        usuario.setEmailAcesso(campoEmailAcesso.getText().trim().toLowerCase());
        usuario.setUnidadeOrgId(unidade.getId());
        usuario.setPerfilSistema(PerfilSistema.COORDENADOR_LOJA);
        usuario.setAtivo(false);
        usuario.setPerguntaSeguranca(campoPergunta.getText().trim());

        try {
            autenticacao.prepararCredenciais(usuario, senha, campoResposta.getText());
        } catch (RegraNegocioException e) {
            Telas.aviso(this, e.getMessage());
            return;
        }

        if (usuarios.inserir(usuario)) {
            Telas.informacao(this,
                    "Cadastro gravado com o código " + pessoa.getId() + ".\n\n"
                    + "O acesso será liberado pelo gestor da sua regional.");
            dispose();
        } else {
            Telas.erro(this, "Os seus dados foram gravados, mas o acesso não. "
                    + "Procure o gestor da regional.");
        }
    }

    private String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
