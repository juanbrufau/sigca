/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import sigca.entities.Edicao;
import sigca.entities.Municipio;
import sigca.entities.ParticipacaoEdicao;
import sigca.entities.PerfilSistema;
import sigca.entities.Pessoa;
import sigca.entities.PontoColeta;
import sigca.entities.PontoColetaEdicao;
import sigca.entities.ResultadoAlimento;
import sigca.entities.TipoAlimento;
import sigca.entities.UnidadeOrg;
import sigca.entities.Usuario;
import sigca.repository.EdicaoRepository;
import sigca.repository.MunicipioRepository;
import sigca.repository.ParticipacaoEdicaoRepository;
import sigca.repository.PessoaRepository;
import sigca.repository.PontoColetaEdicaoRepository;
import sigca.repository.PontoColetaRepository;
import sigca.repository.ResultadoAlimentoRepository;
import sigca.repository.TipoAlimentoRepository;
import sigca.repository.UnidadeOrgRepository;
import sigca.repository.UsuarioRepository;
import sigca.servico.RegraNegocioException;
import sigca.servico.ServicoAutenticacao;
import sigca.servico.ServicoEscopo;
import sigca.servico.ServicoInscricao;

/**
 * Aplicação de console, orientada a menus de fluxo contínuo.
 *
 * Esta classe NÃO contém SQL. Ela coleta a entrada pelo Scanner, monta
 * a entidade, chama o repositório ou o serviço e trata a resposta para
 * mostrar sucesso ou erro — que é a separação de responsabilidades
 * adotada na disciplina.
 */
public class App {

    private static final Scanner TECLADO = new Scanner(System.in);

    private static final MunicipioRepository MUNICIPIOS = new MunicipioRepository();
    private static final UnidadeOrgRepository UNIDADES = new UnidadeOrgRepository();
    private static final PessoaRepository PESSOAS = new PessoaRepository();
    private static final UsuarioRepository USUARIOS = new UsuarioRepository();
    private static final EdicaoRepository EDICOES = new EdicaoRepository();
    private static final PontoColetaRepository PONTOS = new PontoColetaRepository();
    private static final PontoColetaEdicaoRepository PONTOS_EDICAO =
            new PontoColetaEdicaoRepository();
    private static final ParticipacaoEdicaoRepository PARTICIPACOES =
            new ParticipacaoEdicaoRepository();
    private static final TipoAlimentoRepository ALIMENTOS = new TipoAlimentoRepository();
    private static final ResultadoAlimentoRepository RESULTADOS =
            new ResultadoAlimentoRepository();

    private static final ServicoAutenticacao AUTENTICACAO = new ServicoAutenticacao();
    private static final ServicoEscopo ESCOPO = new ServicoEscopo();
    private static final ServicoInscricao INSCRICAO = new ServicoInscricao();

    private static Usuario logado;

    public static void main(String[] args) {
        cabecalho();
        if (!telaDeEntrada()) {
            System.out.println("\nAté logo.");
            return;
        }
        menuPrincipal();
        System.out.println("\nSessão encerrada. Até logo, "
                + logado.getNomeExibicao() + ".");
    }

    // =================================================================
    //  Entrada: login, cadastre-se e lembrete de senha
    // =================================================================
    private static boolean telaDeEntrada() {
        int opcao;
        do {
            System.out.println("\n--- ACESSO ---");
            System.out.println("1 - Entrar");
            System.out.println("2 - Cadastre-se");
            System.out.println("3 - Esqueci minha senha");
            System.out.println("0 - Sair");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> {
                    if (entrar()) {
                        return true;
                    }
                }
                case 2 -> cadastrarSe();
                case 3 -> lembreteDeSenha();
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
        return false;
    }

    private static boolean entrar() {
        String email = lerTexto("E-mail: ");
        String senha = lerTexto("Senha: ");
        try {
            logado = AUTENTICACAO.autenticar(email, senha);
            System.out.println("\nBem-vindo(a), " + logado.getNomeExibicao()
                    + " — " + logado.getPerfilSistema().getDescricao() + ".");
            return true;
        } catch (RegraNegocioException e) {
            System.out.println("** " + e.getMessage());
            return false;
        }
    }

    private static void cadastrarSe() {
        System.out.println("\n--- CADASTRE-SE ---");
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(lerTexto("Nome completo: "));
        pessoa.setNomeSocial(lerTextoOpcional("Nome social (enter para pular): "));
        pessoa.setDataNascimento(lerData("Data de nascimento (dd/mm/aaaa): "));
        pessoa.setTelefone(lerTexto("Telefone: "));
        pessoa.setEmail(lerTexto("E-mail: "));
        pessoa.setBairro(lerTextoOpcional("Bairro: "));
        pessoa.setMunicipioId(escolherMunicipio());

        if (!PESSOAS.inserir(pessoa)) {
            System.out.println("** Não foi possível gravar a pessoa.");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setId(pessoa.getId());
        usuario.setNome(pessoa.getNome());
        usuario.setEmailAcesso(lerTexto("E-mail de acesso: ").toLowerCase());
        usuario.setUnidadeOrgId(escolherUnidade());
        usuario.setPerfilSistema(PerfilSistema.COORDENADOR_LOJA);
        usuario.setAtivo(false);
        usuario.setPerguntaSeguranca(lerTexto("Pergunta de segurança: "));
        String resposta = lerTexto("Resposta: ");
        String senha = lerTexto("Senha (mínimo 8 caracteres): ");

        try {
            AUTENTICACAO.prepararCredenciais(usuario, senha, resposta);
        } catch (RegraNegocioException e) {
            System.out.println("** " + e.getMessage());
            return;
        }
        if (USUARIOS.inserir(usuario)) {
            System.out.println("Cadastro gravado. O acesso fica pendente de "
                    + "aprovação pelo gestor da sua regional.");
        }
    }

    private static void lembreteDeSenha() {
        System.out.println("\n--- LEMBRETE DE SENHA ---");
        System.out.println("O envio por e-mail entra na versão entregue ao "
                + "cliente; aqui a conferência é pela pergunta de segurança.");
        String email = lerTexto("E-mail de acesso: ");
        try {
            System.out.println("Pergunta: " + AUTENTICACAO.perguntaDe(email));
            String resposta = lerTexto("Resposta: ");
            String nova = lerTexto("Nova senha (mínimo 8 caracteres): ");
            if (AUTENTICACAO.redefinirSenha(email, resposta, nova)) {
                System.out.println("Senha redefinida.");
            }
        } catch (RegraNegocioException e) {
            System.out.println("** " + e.getMessage());
        }
    }

    // =================================================================
    //  Menu principal — montado conforme o perfil
    // =================================================================
    private static void menuPrincipal() {
        int opcao;
        do {
            System.out.println("\n===== SIGCA — MENU PRINCIPAL =====");
            System.out.println("Usuário: " + logado.getNomeExibicao()
                    + " | Perfil: " + logado.getPerfilSistema().getDescricao());
            System.out.println("1 - Cadastro de pessoas");
            System.out.println("2 - Meu perfil");
            System.out.println("3 - Pontos de coleta e cobertura da edição");
            System.out.println("4 - Lançar resultado por tipo de alimento");
            System.out.println("5 - Relatório: arrecadado por alimento");
            if (logado.podeGerenciarRegional()) {
                System.out.println("6 - Inscrever voluntário na edição");
                System.out.println("7 - Distribuir voluntários");
                System.out.println("8 - Estrutura organizacional (árvore)");
                System.out.println("9 - Cadastro de municípios");
            }
            System.out.println("0 - Sair");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> menuPessoas();
                case 2 -> meuPerfil();
                case 3 -> coberturaDaEdicao();
                case 4 -> lancarResultado();
                case 5 -> relatorioPorAlimento();
                case 6 -> { if (exigirGestor()) inscreverVoluntario(); }
                case 7 -> { if (exigirGestor()) distribuir(); }
                case 8 -> { if (exigirGestor()) mostrarArvore(); }
                case 9 -> { if (exigirGestor()) menuMunicipios(); }
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static boolean exigirGestor() {
        if (logado.podeGerenciarRegional()) {
            return true;
        }
        System.out.println("** Seu perfil não tem acesso a esta função.");
        return false;
    }

    // =================================================================
    //  CRUD modelo — Pessoa
    // =================================================================
    private static void menuPessoas() {
        int opcao;
        do {
            System.out.println("\n--- CADASTRO DE PESSOAS ---");
            System.out.println("1 - Listar todas");
            System.out.println("2 - Pesquisar por nome");
            System.out.println("3 - Incluir");
            System.out.println("4 - Alterar");
            System.out.println("5 - Excluir");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> imprimir(PESSOAS.listarTodos());
                case 2 -> imprimir(PESSOAS.pesquisarPorNome(
                        lerTexto("Início do nome: ")));
                case 3 -> incluirPessoa();
                case 4 -> alterarPessoa();
                case 5 -> excluirPessoa();
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void incluirPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(lerTexto("Nome completo: "));
        pessoa.setNomeSocial(lerTextoOpcional("Nome social (enter para pular): "));
        pessoa.setDataNascimento(lerData("Data de nascimento (dd/mm/aaaa): "));
        pessoa.setTelefone(lerTexto("Telefone: "));
        pessoa.setEmail(lerTextoOpcional("E-mail: "));
        pessoa.setBairro(lerTextoOpcional("Bairro: "));
        pessoa.setMunicipioId(escolherMunicipio());

        ArrayList<Pessoa> semelhantes =
                PESSOAS.listarSemelhantes(pessoa.getNome(), pessoa.getTelefone());
        if (!semelhantes.isEmpty()) {
            System.out.println("\nAtenção — cadastros parecidos já existem:");
            imprimir(semelhantes);
            if (!confirmar("Gravar mesmo assim? (s/n): ")) {
                return;
            }
        }
        System.out.println(PESSOAS.inserir(pessoa)
                ? "Pessoa gravada com o código " + pessoa.getId() + "."
                : "** Não foi possível gravar.");
    }

    private static void alterarPessoa() {
        Pessoa pessoa = PESSOAS.buscarPorId(lerInteiro("Código da pessoa: "));
        if (pessoa == null) {
            System.out.println("** Pessoa não encontrada.");
            return;
        }
        System.out.println("Atual: " + pessoa);
        pessoa.setNome(lerTextoPadrao("Nome", pessoa.getNome()));
        pessoa.setNomeSocial(lerTextoPadrao("Nome social", pessoa.getNomeSocial()));
        pessoa.setTelefone(lerTextoPadrao("Telefone", pessoa.getTelefone()));
        pessoa.setEmail(lerTextoPadrao("E-mail", pessoa.getEmail()));
        pessoa.setBairro(lerTextoPadrao("Bairro", pessoa.getBairro()));
        System.out.println(PESSOAS.alterar(pessoa)
                ? "Alterações gravadas." : "** Não foi possível alterar.");
    }

    private static void excluirPessoa() {
        int id = lerInteiro("Código da pessoa: ");
        Pessoa pessoa = PESSOAS.buscarPorId(id);
        if (pessoa == null) {
            System.out.println("** Pessoa não encontrada.");
            return;
        }
        System.out.println(pessoa);
        if (confirmar("Confirma a exclusão? (s/n): ")) {
            System.out.println(PESSOAS.excluir(id)
                    ? "Pessoa excluída."
                    : "** Não foi possível excluir: há registros que dependem dela.");
        }
    }

    // =================================================================
    //  Meu perfil
    // =================================================================
    private static void meuPerfil() {
        System.out.println("\n--- MEU PERFIL ---");
        System.out.println(logado);
        if (!confirmar("Alterar a senha? (s/n): ")) {
            return;
        }
        try {
            String atual = lerTexto("Senha atual: ");
            String nova = lerTexto("Nova senha (mínimo 8 caracteres): ");
            System.out.println(AUTENTICACAO.alterarSenha(logado, atual, nova)
                    ? "Senha alterada." : "** Não foi possível alterar.");
        } catch (RegraNegocioException e) {
            System.out.println("** " + e.getMessage());
        }
    }

    // =================================================================
    //  Edição, pontos de coleta e distribuição
    // =================================================================
    private static void coberturaDaEdicao() {
        Edicao edicao = EDICOES.buscarCorrente();
        if (edicao == null) {
            System.out.println("** Não há edição aberta.");
            return;
        }
        System.out.println("\n--- COBERTURA — EDIÇÃO " + edicao.getAno() + " ---");
        List<Integer> escopo = ESCOPO.idsNoEscopo(logado);
        for (PontoColetaEdicao pce : PONTOS_EDICAO.listarCobertura(edicao.getId())) {
            PontoColeta ponto = PONTOS.buscarPorId(pce.getPontoColetaId());
            if (ponto == null || !escopo.contains(ponto.getUnidadeOrgId())) {
                continue;   // fora do escopo deste usuário
            }
            int deficit = pce.getMetaVoluntarios() - pce.getAlocados();
            System.out.printf("[%d] %-38s meta %2d | alocados %2d | faltam %2d%n",
                    pce.getId(), pce.getNomeSnapshot(), pce.getMetaVoluntarios(),
                    pce.getAlocados(), Math.max(deficit, 0));
        }
    }

    private static void inscreverVoluntario() {
        Edicao edicao = EDICOES.buscarCorrente();
        if (edicao == null) {
            System.out.println("** Não há edição aberta.");
            return;
        }
        Pessoa pessoa = PESSOAS.buscarPorId(lerInteiro("Código da pessoa: "));
        if (pessoa == null) {
            System.out.println("** Pessoa não encontrada.");
            return;
        }
        System.out.println(pessoa);
        LocalTime das = lerHoraOpcional("Disponível das (hh:mm, enter para pular): ");
        LocalTime ate = lerHoraOpcional("até (hh:mm, enter para pular): ");
        try {
            ParticipacaoEdicao p = INSCRICAO.inscrever(pessoa, edicao,
                    logado.getUnidadeOrgId(), das, ate);
            System.out.println("Inscrição registrada no pool da regional "
                    + "(código " + p.getId() + ").");
        } catch (RegraNegocioException e) {
            System.out.println("** " + e.getMessage());
        }
    }

    private static void distribuir() {
        Edicao edicao = EDICOES.buscarCorrente();
        if (edicao == null) {
            System.out.println("** Não há edição aberta.");
            return;
        }
        ArrayList<ParticipacaoEdicao> pool =
                PARTICIPACOES.listarNaoAlocados(edicao.getId());
        if (pool.isEmpty()) {
            System.out.println("Não há voluntários aguardando distribuição.");
            return;
        }
        System.out.println("\n--- VOLUNTÁRIOS NÃO ALOCADOS ---");
        for (ParticipacaoEdicao p : pool) {
            System.out.printf("[%d] %s%n", p.getId(), p.getPessoaNome());
        }
        coberturaDaEdicao();
        int participacao = lerInteiro("Código da participação: ");
        int ponto = lerInteiro("Código do ponto de coleta da edição: ");
        System.out.println(PARTICIPACOES.alocar(participacao, ponto, "DISTRIBUIDA")
                ? "Voluntário alocado." : "** Não foi possível alocar.");
    }

    private static void mostrarArvore() {
        System.out.println("\n--- ESTRUTURA ORGANIZACIONAL ---");
        for (UnidadeOrg raiz : UNIDADES.listarRaizes()) {
            UnidadeOrg arvore = ESCOPO.montarArvore(raiz.getId());
            ESCOPO.imprimirArvore(arvore, 0);
            System.out.println("Profundidade: " + ESCOPO.profundidade(arvore)
                    + " níveis.");
        }
        System.out.println("Unidades no seu escopo: "
                + ESCOPO.idsNoEscopo(logado).size());
    }

    // =================================================================
    //  Resultado por tipo de alimento
    // =================================================================
    private static void lancarResultado() {
        Edicao edicao = EDICOES.buscarCorrente();
        if (edicao == null) {
            System.out.println("** Não há edição aberta.");
            return;
        }
        int pontoEdicao = lerInteiro("Código do ponto de coleta da edição: ");
        PontoColetaEdicao pce = PONTOS_EDICAO.buscarPorId(pontoEdicao);
        if (pce == null) {
            System.out.println("** Ponto de coleta não encontrado nesta edição.");
            return;
        }
        System.out.println("Loja: " + pce.getNomeSnapshot());

        ArrayList<TipoAlimento> tipos = ALIMENTOS.listarTodos();
        for (TipoAlimento t : tipos) {
            System.out.printf("[%2d] %-22s (%s)%n", t.getId(), t.getNome(),
                    t.getCategoria().getDescricao());
        }
        int codigo;
        do {
            codigo = lerInteiro("Código do alimento (0 para encerrar): ");
            if (codigo == 0) {
                continue;
            }
            BigDecimal kg = lerDecimal("Quantidade em quilos: ");
            if (kg.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("** A quantidade precisa ser maior que zero.");
                continue;
            }
            ResultadoAlimento r = new ResultadoAlimento();
            r.setPontoColetaEdicaoId(pontoEdicao);
            r.setTipoAlimentoId(codigo);
            r.setQuantidadeKg(kg);
            System.out.println(RESULTADOS.inserir(r)
                    ? "Lançado." : "** Não foi possível lançar.");
        } while (codigo != 0);

        System.out.println("Total da loja: "
                + RESULTADOS.totalDoPonto(pontoEdicao) + " kg");
    }

    private static void relatorioPorAlimento() {
        Edicao edicao = EDICOES.buscarCorrente();
        if (edicao == null) {
            System.out.println("** Não há edição aberta.");
            return;
        }
        int ano = lerInteiro("Ano da edição (0 para a corrente): ");
        Edicao alvo = ano == 0 ? edicao : EDICOES.buscarPorAno(ano);
        if (alvo == null) {
            System.out.println("** Edição não encontrada.");
            return;
        }
        Map<String, BigDecimal> totais = RESULTADOS.somarPorAlimento(alvo.getId());
        if (totais.isEmpty()) {
            System.out.println("Nenhum resultado lançado nesta edição.");
            return;
        }
        System.out.println("\n--- ARRECADADO POR ALIMENTO — " + alvo.getAno() + " ---");
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> linha : totais.entrySet()) {
            System.out.printf("%-24s %10s kg%n", linha.getKey(), linha.getValue());
            total = total.add(linha.getValue());
        }
        System.out.printf("%-24s %10s kg%n", "TOTAL", total);
    }

    // =================================================================
    //  Municípios — segundo CRUD, mais simples
    // =================================================================
    private static void menuMunicipios() {
        int opcao;
        do {
            System.out.println("\n--- MUNICÍPIOS ---");
            System.out.println("1 - Listar");
            System.out.println("2 - Pesquisar por nome");
            System.out.println("3 - Incluir");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> imprimir(MUNICIPIOS.listarTodos());
                case 2 -> imprimir(MUNICIPIOS.pesquisarPorNome(
                        lerTexto("Início do nome: ")));
                case 3 -> {
                    Municipio m = new Municipio();
                    m.setNome(lerTexto("Nome: "));
                    m.setUf(lerTexto("UF: ").toUpperCase());
                    System.out.println(MUNICIPIOS.inserir(m)
                            ? "Município gravado." : "** Não foi possível gravar.");
                }
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    // =================================================================
    //  Entrada e saída de console
    // =================================================================
    private static void cabecalho() {
        System.out.println("=".repeat(62));
        System.out.println(" SIGCA — Sistema de Gestão da Coleta de Alimentos");
        System.out.println(" Companhia das Obras · Fatec Rio Preto");
        System.out.println("=".repeat(62));
    }

    private static <T> void imprimir(List<T> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        System.out.println();
        for (T item : lista) {
            System.out.println(item);
        }
        System.out.println(lista.size() + " registro(s).");
    }

    private static int escolherMunicipio() {
        imprimir(MUNICIPIOS.listarTodos());
        return lerInteiro("Código do município: ");
    }

    private static int escolherUnidade() {
        imprimir(UNIDADES.listarTodos());
        return lerInteiro("Código da unidade organizacional: ");
    }

    private static String lerTexto(String rotulo) {
        System.out.print(rotulo);
        return TECLADO.nextLine().trim();
    }

    private static String lerTextoOpcional(String rotulo) {
        String valor = lerTexto(rotulo);
        return valor.isBlank() ? null : valor;
    }

    private static String lerTextoPadrao(String rotulo, String atual) {
        String valor = lerTexto(rotulo + " [" + (atual == null ? "" : atual) + "]: ");
        return valor.isBlank() ? atual : valor;
    }

    private static int lerInteiro(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                System.out.println("** Digite um número inteiro.");
            }
        }
    }

    private static BigDecimal lerDecimal(String rotulo) {
        while (true) {
            try {
                return new BigDecimal(lerTexto(rotulo).replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("** Digite um número, usando ponto ou vírgula.");
            }
        }
    }

    private static LocalDate lerData(String rotulo) {
        while (true) {
            String valor = lerTexto(rotulo);
            try {
                String[] p = valor.split("/");
                return LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]),
                        Integer.parseInt(p[0]));
            } catch (RuntimeException e) {
                System.out.println("** Data inválida. Use dd/mm/aaaa.");
            }
        }
    }

    private static LocalTime lerHoraOpcional(String rotulo) {
        String valor = lerTexto(rotulo);
        if (valor.isBlank()) {
            return null;
        }
        try {
            String[] p = valor.split(":");
            return LocalTime.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
        } catch (RuntimeException e) {
            System.out.println("** Hora inválida; o campo ficou em branco.");
            return null;
        }
    }

    private static boolean confirmar(String rotulo) {
        return lerTexto(rotulo).equalsIgnoreCase("s");
    }
}
