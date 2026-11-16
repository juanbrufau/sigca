/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.servico;

/**
 * Erro de regra de negócio: aquilo que o sistema recusa por decisão do
 * domínio, e não por falha técnica.
 *
 * Exceção verificada (checked): quem chama é obrigado a tratar, o que
 * força a mensagem a chegar ao usuário. Tratamento de exceções é
 * conteúdo da aula 5 de Linguagem de Programação I.
 */
public class RegraNegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
