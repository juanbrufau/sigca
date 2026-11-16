/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.ui;

import sigca.entities.Usuario;

/**
 * Guarda o usuário autenticado enquanto o programa está aberto.
 *
 * Classe de elementos estáticos (aula 14 de Linguagem de Programação I):
 * não se instancia, porque existe uma única sessão por execução. É o que
 * permite a qualquer tela saber quem está logado sem receber o usuário
 * como parâmetro em toda chamada.
 */
public final class Sessao {

    private static Usuario atual;

    private Sessao() {
    }

    public static void iniciar(Usuario usuario) {
        atual = usuario;
    }

    public static Usuario getAtual() {
        return atual;
    }

    public static boolean temSessao() {
        return atual != null;
    }

    public static void encerrar() {
        atual = null;
    }
}
