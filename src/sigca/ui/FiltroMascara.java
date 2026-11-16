/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Coloca os símbolos sozinho enquanto a pessoa digita: as barras da
 * data e os parênteses e o traço do telefone.
 *
 * Como funciona: o filtro intercepta toda alteração do campo, joga fora
 * tudo que não é dígito, corta no tamanho máximo e remonta o texto já
 * formatado. Assim o campo nunca fica num estado inválido, e quem lê o
 * valor depois recebe sempre no mesmo formato.
 *
 * Usa DocumentFilter em vez de MaskFormatter porque o telefone brasileiro
 * tem dois tamanhos — oito dígitos no fixo e nove no celular — e máscara
 * fixa não acomoda os dois.
 */
public class FiltroMascara extends DocumentFilter {

    /** O que o campo aceita. */
    public enum Formato {
        DATA(8),
        TELEFONE(11);

        private final int maximoDigitos;

        Formato(int maximoDigitos) {
            this.maximoDigitos = maximoDigitos;
        }
    }

    private final Formato formato;

    private FiltroMascara(Formato formato) {
        this.formato = formato;
    }

    /**
     * Liga a máscara a um campo de texto.
     */
    public static void aplicar(JTextField campo, Formato formato) {
        ((AbstractDocument) campo.getDocument())
                .setDocumentFilter(new FiltroMascara(formato));
        campo.setToolTipText(formato == Formato.DATA
                ? "dd/mm/aaaa — digite só os números"
                : "(DD) 90000-0000 — digite só os números");
    }

    // =================================================================
    //  Interceptação: os três métodos caem no mesmo lugar
    // =================================================================
    @Override
    public void insertString(FilterBypass fb, int posicao, String texto,
                             AttributeSet atributos) throws BadLocationException {
        replace(fb, posicao, 0, texto, atributos);
    }

    @Override
    public void remove(FilterBypass fb, int posicao, int tamanho)
            throws BadLocationException {
        replace(fb, posicao, tamanho, "", null);
    }

    @Override
    public void replace(FilterBypass fb, int posicao, int tamanho, String texto,
                        AttributeSet atributos) throws BadLocationException {
        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String resultado = atual.substring(0, posicao)
                + (texto == null ? "" : texto)
                + atual.substring(posicao + tamanho);

        String digitos = apenasDigitos(resultado);
        if (digitos.length() > formato.maximoDigitos) {
            digitos = digitos.substring(0, formato.maximoDigitos);
        }
        fb.replace(0, fb.getDocument().getLength(), formatar(digitos), atributos);
    }

    // =================================================================
    //  Formatação
    // =================================================================
    private String apenasDigitos(String texto) {
        StringBuilder numeros = new StringBuilder();
        for (char c : texto.toCharArray()) {
            if (Character.isDigit(c)) {
                numeros.append(c);
            }
        }
        return numeros.toString();
    }

    private String formatar(String digitos) {
        return formato == Formato.DATA
                ? formatarData(digitos)
                : formatarTelefone(digitos);
    }

    /** 09081999 vira 09/08/1999, mostrando a barra assim que ela cabe. */
    private String formatarData(String d) {
        int n = d.length();
        if (n <= 2) {
            return d;
        }
        if (n <= 4) {
            return d.substring(0, 2) + "/" + d.substring(2);
        }
        return d.substring(0, 2) + "/" + d.substring(2, 4) + "/" + d.substring(4);
    }

    /**
     * 17981098601 vira (17) 98109-8601, e 1732214455 vira (17) 3221-4455.
     * O ponto de quebra muda conforme o número tenha oito ou nove dígitos.
     */
    private String formatarTelefone(String d) {
        int n = d.length();
        if (n == 0) {
            return "";
        }
        StringBuilder saida = new StringBuilder("(");
        saida.append(d, 0, Math.min(2, n));
        if (n > 2) {
            saida.append(") ");
            int quebra = (n <= 10) ? 4 : 5;     // fixo: 4+4 · celular: 5+4
            saida.append(d, 2, Math.min(2 + quebra, n));
            if (n > 2 + quebra) {
                saida.append('-').append(d, 2 + quebra, n);
            }
        }
        return saida.toString();
    }
}
