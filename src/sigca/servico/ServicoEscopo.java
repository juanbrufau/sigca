/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.servico;

import java.util.ArrayList;
import java.util.List;
import sigca.entities.PerfilSistema;
import sigca.entities.UnidadeOrg;
import sigca.entities.Usuario;
import sigca.repository.UnidadeOrgRepository;

/**
 * Define o que cada usuário enxerga.
 *
 * A estrutura da campanha é uma árvore: Nacional no topo, Regionais
 * abaixo e Núcleos locais na base. Um usuário enxerga a sua unidade e
 * tudo que estiver abaixo dela — e é só isso que separa a visão do
 * Gestor Regional da visão do Coordenador de Loja.
 *
 * O percurso da árvore é RECURSIVO, conteúdo da aula 15 de Estrutura de
 * Dados. A árvore montada em memória usa ArrayList (aula 13).
 *
 * A recursão fica em Java, e não em SQL, de propósito: consulta
 * recursiva (CTE WITH RECURSIVE) não foi dada em Banco de Dados.
 */
public class ServicoEscopo {

    private final UnidadeOrgRepository repositorio = new UnidadeOrgRepository();

    /**
     * Identificadores da unidade informada e de todas as subordinadas a
     * ela, em qualquer profundidade.
     */
    public List<Integer> idsNoEscopo(int unidadeOrgId) {
        List<Integer> ids = new ArrayList<>();
        coletar(unidadeOrgId, ids);
        return ids;
    }

    /**
     * O escopo de um usuário. O administrador nacional está na raiz, de
     * modo que o mesmo cálculo lhe devolve a árvore inteira — não é
     * preciso tratá-lo como caso especial.
     */
    public List<Integer> idsNoEscopo(Usuario usuario) {
        return idsNoEscopo(usuario.getUnidadeOrgId());
    }

    /**
     * Monta a árvore a partir de uma unidade, preenchendo a lista de
     * filhas de cada nó.
     */
    public UnidadeOrg montarArvore(int unidadeOrgId) {
        UnidadeOrg raiz = repositorio.buscarPorId(unidadeOrgId);
        if (raiz != null) {
            preencherFilhas(raiz);
        }
        return raiz;
    }

    /**
     * Desenha a árvore no console, com recuo por nível.
     */
    public void imprimirArvore(UnidadeOrg no, int nivel) {
        if (no == null) {
            return;
        }
        System.out.println("   ".repeat(nivel) + "- " + no.getNome()
                + " (" + no.getTipo() + ")");
        for (UnidadeOrg filha : no.getFilhas()) {
            imprimirArvore(filha, nivel + 1);
        }
    }

    /**
     * Profundidade da árvore a partir de um nó. Serve de demonstração do
     * caso base e do caso recursivo.
     */
    public int profundidade(UnidadeOrg no) {
        if (no == null || no.getFilhas().isEmpty()) {
            return 1;
        }
        int maior = 0;
        for (UnidadeOrg filha : no.getFilhas()) {
            maior = Math.max(maior, profundidade(filha));
        }
        return maior + 1;
    }

    public boolean podeVer(Usuario usuario, int unidadeOrgId) {
        if (usuario.getPerfilSistema() == PerfilSistema.ADMINISTRADOR_NACIONAL) {
            return true;
        }
        return idsNoEscopo(usuario).contains(unidadeOrgId);
    }

    // ------------------------------------------------------------------
    // Caso base: a unidade não tem filhas, e a recursão para.
    // Caso recursivo: para cada filha, repete o mesmo procedimento.
    // ------------------------------------------------------------------
    private void coletar(int unidadeOrgId, List<Integer> acumulador) {
        acumulador.add(unidadeOrgId);
        for (UnidadeOrg filha : repositorio.listarPorPai(unidadeOrgId)) {
            coletar(filha.getId(), acumulador);
        }
    }

    private void preencherFilhas(UnidadeOrg no) {
        for (UnidadeOrg filha : repositorio.listarPorPai(no.getId())) {
            no.getFilhas().add(filha);
            preencherFilhas(filha);
        }
    }
}
