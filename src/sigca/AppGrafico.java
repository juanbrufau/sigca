/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca;

import javax.swing.SwingUtilities;
import sigca.ui.TelaLogin;
import sigca.ui.Telas;

/**
 * Ponto de entrada da versão GRÁFICA do sistema.
 *
 * O sistema tem duas interfaces sobre o mesmo núcleo:
 *
 *   sigca.App         — menu de console, no formato da metodologia da
 *                       disciplina: Scanner, do-while e switch-case.
 *   sigca.AppGrafico  — janelas Swing, que atendem o item 4.1 do
 *                       gabarito da curricularização.
 *
 * As duas chamam exatamente os mesmos repositórios e os mesmos
 * serviços. Nenhuma regra de negócio foi duplicada: se a regra de idade
 * mudar no ServicoInscricao, ela muda para as duas interfaces ao mesmo
 * tempo. É o que a arquitetura em camadas compra.
 */
public class AppGrafico {

    public static void main(String[] args) {
        Telas.aplicarVisualDoSistema();

        // Toda criação e alteração de componente Swing acontece na
        // thread de eventos. Fora dela, a interface pode travar ou
        // desenhar errado — é regra da biblioteca, não preferência.
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
