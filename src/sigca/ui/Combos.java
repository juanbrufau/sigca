/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import sigca.entities.Municipio;
import sigca.entities.UnidadeOrg;

/**
 * Caixas de seleção que listam registros do banco mostrando o nome, e
 * não o toString() completo da entidade.
 *
 * O toString() das entidades foi escrito para o menu de console, onde
 * mostrar código e todos os campos ajuda. Numa caixa de seleção isso
 * polui, então a apresentação é trocada por um renderizador — sem mexer
 * na entidade, que continua servindo às duas interfaces.
 */
public final class Combos {

    private Combos() {
    }

    public static JComboBox<Municipio> deMunicipios(java.util.List<Municipio> lista) {
        JComboBox<Municipio> combo = new JComboBox<>();
        for (Municipio m : lista) {
            combo.addItem(m);
        }
        combo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean selecionado, boolean comFoco) {
                super.getListCellRendererComponent(lista, valor, indice,
                        selecionado, comFoco);
                if (valor instanceof Municipio) {
                    Municipio m = (Municipio) valor;
                    setText(m.getNome() + " — " + m.getUf());
                }
                return this;
            }
        });
        return combo;
    }

    public static JComboBox<UnidadeOrg> deUnidades(java.util.List<UnidadeOrg> lista) {
        JComboBox<UnidadeOrg> combo = new JComboBox<>();
        for (UnidadeOrg u : lista) {
            combo.addItem(u);
        }
        combo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean selecionado, boolean comFoco) {
                super.getListCellRendererComponent(lista, valor, indice,
                        selecionado, comFoco);
                if (valor instanceof UnidadeOrg) {
                    UnidadeOrg u = (UnidadeOrg) valor;
                    setText(u.getNome() + "  (" + u.getTipo() + ")");
                }
                return this;
            }
        });
        return combo;
    }

    /**
     * Seleciona no combo o município cujo código corresponde ao valor.
     */
    public static void selecionarMunicipio(JComboBox<Municipio> combo, Integer id) {
        if (id == null) {
            combo.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedIndex(-1);
    }

    /** Layouts usados pelas telas; reunidos aqui por conveniência. */
    public static GridBagLayout formulario() {
        return new GridBagLayout();
    }

    public static BorderLayout moldura() {
        return new BorderLayout(10, 10);
    }
}
