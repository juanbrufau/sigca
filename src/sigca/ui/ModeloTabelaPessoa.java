/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import sigca.entities.Pessoa;

/**
 * Liga a lista de pessoas vinda do repositório à JTable da tela.
 *
 * Herda de AbstractTableModel: a classe abstrata já resolve a maior
 * parte do trabalho, e aqui só são implementados os métodos que ela
 * deixa em aberto — quantas linhas, quantas colunas, o nome de cada
 * coluna e o valor de cada célula. É herança e sobrescrita, conteúdo
 * das aulas 9, 10 e 16 de Linguagem de Programação I.
 */
public class ModeloTabelaPessoa extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUNAS = {
        "Código", "Nome", "Nome social", "Nascimento", "Telefone", "Município"
    };

    private transient List<Pessoa> pessoas;

    public ModeloTabelaPessoa(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    /**
     * Troca o conteúdo da tabela e avisa a JTable para se redesenhar.
     */
    public void atualizar(List<Pessoa> novaLista) {
        this.pessoas = novaLista;
        fireTableDataChanged();
    }

    public Pessoa getPessoa(int linha) {
        if (linha < 0 || linha >= pessoas.size()) {
            return null;
        }
        return pessoas.get(linha);
    }

    @Override
    public int getRowCount() {
        return pessoas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    @Override
    public boolean isCellEditable(int linha, int coluna) {
        return false;   // a edição é feita pelo formulário, não na grade
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Pessoa p = pessoas.get(linha);
        switch (coluna) {
            case 0:  return p.getId();
            case 1:  return p.getNome();
            case 2:  return p.getNomeSocial() == null ? "" : p.getNomeSocial();
            case 3:  return Telas.formatar(p.getDataNascimento());
            case 4:  return p.getTelefone() == null ? "" : p.getTelefone();
            case 5:  return p.getMunicipioNome() == null ? "" : p.getMunicipioNome();
            default: return "";
        }
    }
}
