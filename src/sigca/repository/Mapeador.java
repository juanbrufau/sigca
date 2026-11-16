/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.repository;

/**
 * Conversões usadas pelos repositórios ao ler o ResultSet.
 */
final class Mapeador {

    private Mapeador() {
    }

    /**
     * Converte o texto gravado na coluna para a constante da enumeração,
     * tolerando valor nulo.
     */
    static <E extends Enum<E>> E paraEnum(Class<E> tipo, String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return Enum.valueOf(tipo, valor.trim());
    }
}
