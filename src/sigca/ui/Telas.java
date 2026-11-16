/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Pequenas rotinas que todas as telas usam: mensagens padronizadas,
 * leitura de data e montagem de formulário.
 *
 * Reunir isso num lugar só evita repetir o mesmo trecho em seis telas —
 * e é a mesma ideia de separação de responsabilidades que já separa a
 * interface do repositório.
 */
public final class Telas {

    public static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SUBTITULO = new Font("SansSerif", Font.PLAIN, 13);

    private Telas() {
    }

    /**
     * Faz as janelas usarem a aparência nativa do sistema operacional.
     */
    public static void aplicarVisualDoSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Aviso: mantendo a aparência padrão do Java — "
                    + e.getMessage());
        }
    }

    public static void centralizar(Window janela) {
        janela.setLocationRelativeTo(null);
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "SIGCA",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "SIGCA",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void informacao(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "SIGCA",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component pai, String pergunta) {
        return JOptionPane.showConfirmDialog(pai, pergunta, "SIGCA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Converte o texto digitado em data, devolvendo nulo quando o campo
     * está vazio e lançando exceção quando o formato não serve.
     */
    public static LocalDate lerData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return LocalDate.parse(texto.trim(), DATA);
    }

    public static String formatar(LocalDate data) {
        return data == null ? "" : data.format(DATA);
    }

    public static JLabel titulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(TITULO);
        return rotulo;
    }

    public static JLabel subtitulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(SUBTITULO);
        return rotulo;
    }

    /**
     * Restrições de posicionamento para formulários em GridBagLayout.
     * A coluna 0 recebe o rótulo; a coluna 1, o campo, que estica.
     */
    public static GridBagConstraints posicao(int coluna, int linha, boolean estica) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = coluna;
        c.gridy = linha;
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        if (estica) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
        }
        return c;
    }

    /**
     * Acrescenta um par rótulo + campo a um formulário.
     */
    public static void linhaDeFormulario(JComponent formulario, int linha,
                                         String rotulo, JComponent campo) {
        formulario.add(new JLabel(rotulo), posicao(0, linha, false));
        formulario.add(campo, posicao(1, linha, true));
    }
}
