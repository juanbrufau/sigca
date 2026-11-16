/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import sigca.entities.Municipio;
import sigca.entities.Pessoa;
import sigca.repository.MunicipioRepository;
import sigca.repository.PessoaRepository;

/**
 * Cadastro de pessoas — o "modelo de CRUD" pedido no item 4.1.
 *
 * Tem as quatro operações: incluir, alterar, excluir e pesquisar. A
 * grade à esquerda lista o resultado; o formulário à direita edita o
 * registro selecionado.
 *
 * A pesquisa é delegada ao banco pela cláusula LIKE, no repositório —
 * a tela nunca percorre a lista filtrando com for e if.
 */
public class TelaPessoa extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient PessoaRepository repositorio = new PessoaRepository();
    private final transient MunicipioRepository municipios = new MunicipioRepository();

    private final transient ModeloTabelaPessoa modelo =
            new ModeloTabelaPessoa(new ArrayList<>());
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoPesquisa = new JTextField(18);
    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoNomeSocial = new JTextField(22);
    private final JTextField campoNascimento = new JTextField(22);
    private final JTextField campoTelefone = new JTextField(22);
    private final JTextField campoEmail = new JTextField(22);
    private final JTextField campoBairro = new JTextField(22);
    private final transient JComboBox<Municipio> comboMunicipio;

    /** Código da pessoa em edição; zero quando é um cadastro novo. */
    private int codigoEmEdicao;

    // Montar a janela no construtor é o padrão do Swing; o aviso
    // this-escape não se aplica porque esta classe não é estendida.
    @SuppressWarnings("this-escape")
    public TelaPessoa(JFrame pai) {
        super(pai, "SIGCA — Cadastro de pessoas", true);
        this.comboMunicipio = Combos.deMunicipios(municipios.listarTodos());
        montarTela();
        listarTodas();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---- barra de pesquisa
        JButton botaoPesquisar = new JButton("Pesquisar");
        JButton botaoListarTodas = new JButton("Listar todas");
        botaoPesquisar.addActionListener(evento -> pesquisar());
        botaoListarTodas.addActionListener(evento -> {
            campoPesquisa.setText("");
            listarTodas();
        });

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        barra.add(Telas.subtitulo("Nome começa com:"));
        barra.add(campoPesquisa);
        barra.add(botaoPesquisar);
        barra.add(botaoListarTodas);
        add(barra, BorderLayout.NORTH);

        // ---- grade
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(22);
        tabela.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setPreferredSize(new Dimension(560, 320));
        add(rolagem, BorderLayout.CENTER);

        // ---- formulário
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Dados da pessoa"));
        Telas.linhaDeFormulario(formulario, 0, "Nome:", campoNome);
        Telas.linhaDeFormulario(formulario, 1, "Nome social:", campoNomeSocial);
        Telas.linhaDeFormulario(formulario, 2, "Nascimento:", campoNascimento);
        Telas.linhaDeFormulario(formulario, 3, "Telefone:", campoTelefone);
        Telas.linhaDeFormulario(formulario, 4, "E-mail:", campoEmail);
        Telas.linhaDeFormulario(formulario, 5, "Bairro:", campoBairro);
        Telas.linhaDeFormulario(formulario, 6, "Município:", comboMunicipio);
        campoNascimento.setToolTipText("dd/mm/aaaa");
        add(formulario, BorderLayout.EAST);

        // ---- botões
        JButton botaoNovo = new JButton("Novo");
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoExcluir = new JButton("Excluir");
        JButton botaoFechar = new JButton("Fechar");

        botaoNovo.addActionListener(evento -> limparFormulario());
        botaoSalvar.addActionListener(evento -> salvar());
        botaoExcluir.addActionListener(evento -> excluir());
        botaoFechar.addActionListener(evento -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        botoes.add(botaoNovo);
        botoes.add(botaoSalvar);
        botoes.add(botaoExcluir);
        botoes.add(botaoFechar);
        add(botoes, BorderLayout.SOUTH);

        pack();
        Telas.centralizar(this);
    }

    // =================================================================
    //  Operações
    // =================================================================
    private void listarTodas() {
        modelo.atualizar(repositorio.listarTodos());
        limparFormulario();
    }

    private void pesquisar() {
        String texto = campoPesquisa.getText().trim();
        if (texto.isEmpty()) {
            listarTodas();
            return;
        }
        modelo.atualizar(repositorio.pesquisarPorNome(texto));
        if (modelo.getRowCount() == 0) {
            Telas.informacao(this, "Nenhuma pessoa encontrada com esse início de nome.");
        }
        limparFormulario();
    }

    private void salvar() {
        Pessoa pessoa = lerFormulario();
        if (pessoa == null) {
            return;
        }
        boolean gravou;
        if (codigoEmEdicao == 0) {
            gravou = repositorio.inserir(pessoa);
            if (gravou) {
                Telas.informacao(this, "Pessoa cadastrada com o código "
                        + pessoa.getId() + ".");
            }
        } else {
            pessoa.setId(codigoEmEdicao);
            gravou = repositorio.alterar(pessoa);
            if (gravou) {
                Telas.informacao(this, "Alterações gravadas.");
            }
        }
        if (!gravou) {
            Telas.erro(this, "Não foi possível gravar. "
                    + "Confira a saída do console para o detalhe do erro.");
            return;
        }
        listarTodas();
    }

    private void excluir() {
        if (codigoEmEdicao == 0) {
            Telas.aviso(this, "Selecione uma pessoa na lista antes de excluir.");
            return;
        }
        if (!Telas.confirmar(this, "Excluir \"" + campoNome.getText() + "\"?")) {
            return;
        }
        if (repositorio.excluir(codigoEmEdicao)) {
            Telas.informacao(this, "Pessoa excluída.");
            listarTodas();
        } else {
            Telas.erro(this, "Não foi possível excluir.\n\n"
                    + "Provavelmente existem registros que dependem desta pessoa — "
                    + "uma inscrição em alguma edição, por exemplo. "
                    + "A restrição de chave estrangeira do banco impede a exclusão "
                    + "justamente para não deixar registros órfãos.");
        }
    }

    // =================================================================
    //  Formulário
    // =================================================================
    private void preencherFormulario() {
        Pessoa pessoa = modelo.getPessoa(tabela.getSelectedRow());
        if (pessoa == null) {
            return;
        }
        codigoEmEdicao = pessoa.getId();
        campoNome.setText(pessoa.getNome());
        campoNomeSocial.setText(texto(pessoa.getNomeSocial()));
        campoNascimento.setText(Telas.formatar(pessoa.getDataNascimento()));
        campoTelefone.setText(texto(pessoa.getTelefone()));
        campoEmail.setText(texto(pessoa.getEmail()));
        campoBairro.setText(texto(pessoa.getBairro()));
        Combos.selecionarMunicipio(comboMunicipio, pessoa.getMunicipioId());
    }

    private void limparFormulario() {
        codigoEmEdicao = 0;
        tabela.clearSelection();
        campoNome.setText("");
        campoNomeSocial.setText("");
        campoNascimento.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        campoBairro.setText("");
        comboMunicipio.setSelectedIndex(-1);
        campoNome.requestFocusInWindow();
    }

    /**
     * Monta a entidade a partir dos campos, validando o que a tela
     * consegue validar sozinha. Devolve nulo quando há erro.
     */
    private Pessoa lerFormulario() {
        if (campoNome.getText().isBlank()) {
            Telas.aviso(this, "O nome é obrigatório.");
            campoNome.requestFocusInWindow();
            return null;
        }
        LocalDate nascimento;
        try {
            nascimento = Telas.lerData(campoNascimento.getText());
        } catch (DateTimeParseException e) {
            Telas.aviso(this, "Data de nascimento inválida. Use dd/mm/aaaa.");
            campoNascimento.requestFocusInWindow();
            return null;
        }
        if (nascimento == null) {
            Telas.aviso(this, "A data de nascimento é obrigatória.");
            campoNascimento.requestFocusInWindow();
            return null;
        }
        if (!nascimento.isBefore(LocalDate.now())) {
            Telas.aviso(this, "A data de nascimento precisa ser anterior a hoje.");
            campoNascimento.requestFocusInWindow();
            return null;
        }

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(campoNome.getText().trim());
        pessoa.setNomeSocial(opcional(campoNomeSocial.getText()));
        pessoa.setDataNascimento(nascimento);
        pessoa.setTelefone(opcional(campoTelefone.getText()));
        pessoa.setEmail(opcional(campoEmail.getText()));
        pessoa.setBairro(opcional(campoBairro.getText()));

        Municipio municipio = (Municipio) comboMunicipio.getSelectedItem();
        pessoa.setMunicipioId(municipio == null ? null : municipio.getId());
        return pessoa;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
