/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.servico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import sigca.entities.Edicao;
import sigca.entities.FaixaEtaria;
import sigca.entities.PapelParticipacao;
import sigca.entities.ParticipacaoEdicao;
import sigca.entities.Pessoa;
import sigca.entities.StatusEdicao;
import sigca.repository.ParticipacaoEdicaoRepository;

/**
 * Inscrição do voluntário numa edição da campanha.
 *
 * Aqui mora a regra de idade. A campanha aceita a partir de onze anos
 * completos na data da coleta, mas o MVP acadêmico só inscreve maiores
 * de dezoito: o cadastro do responsável legal e o termo de autorização
 * — exigidos pelo art. 14 da LGPD e pelo ECA — ficam para a versão
 * entregue ao cliente.
 *
 * A faixa etária é calculada e gravada mesmo assim, porque a coluna já
 * existe no banco e porque é ela que o sistema definitivo vai usar.
 */
public class ServicoInscricao {

    public static final int IDADE_MINIMA_CAMPANHA = 11;
    public static final int MAIORIDADE = 18;

    private final ParticipacaoEdicaoRepository repositorio =
            new ParticipacaoEdicaoRepository();

    /**
     * Classifica a pessoa conforme o ECA, art. 2º: criança é quem tem
     * até doze anos incompletos; adolescente, de doze a dezoito.
     *
     * Método estático (aula 14 de LP1): é cálculo puro, não depende do
     * estado do objeto nem do banco, e por isso pode ser chamado e
     * testado sem instanciar o serviço.
     */
    public static FaixaEtaria faixaEtariaNaColeta(LocalDate dataNascimento,
                                                  LocalDate dataColeta) {
        int idade = idadeNaColeta(dataNascimento, dataColeta);
        if (idade < 12) {
            return FaixaEtaria.CRIANCA;
        }
        if (idade < MAIORIDADE) {
            return FaixaEtaria.ADOLESCENTE;
        }
        return FaixaEtaria.ADULTO;
    }

    public static int idadeNaColeta(LocalDate dataNascimento, LocalDate dataColeta) {
        return Period.between(dataNascimento, dataColeta).getYears();
    }

    /**
     * Inscreve a pessoa na edição, recusando o que o MVP não comporta.
     */
    public ParticipacaoEdicao inscrever(Pessoa pessoa, Edicao edicao,
                                        int unidadeOrgId,
                                        LocalTime disponivelDas,
                                        LocalTime disponivelAte)
            throws RegraNegocioException {

        if (edicao == null || edicao.getStatus() != StatusEdicao.INSCRICOES_ABERTAS) {
            throw new RegraNegocioException(
                    "As inscrições desta edição não estão abertas.");
        }
        if (repositorio.jaInscrito(edicao.getId(), pessoa.getId())) {
            throw new RegraNegocioException(
                    "Esta pessoa já está inscrita na edição de " + edicao.getAno() + ".");
        }

        int idade = idadeNaColeta(pessoa.getDataNascimento(), edicao.getDataColeta());
        FaixaEtaria faixa = faixaEtariaNaColeta(pessoa.getDataNascimento(),
                                                edicao.getDataColeta());

        if (idade < IDADE_MINIMA_CAMPANHA) {
            throw new RegraNegocioException(String.format(
                    "A campanha aceita voluntários a partir de %d anos completos "
                    + "na data da coleta. Esta pessoa terá %d.",
                    IDADE_MINIMA_CAMPANHA, idade));
        }
        if (faixa != FaixaEtaria.ADULTO) {
            throw new RegraNegocioException(String.format(
                    "Esta pessoa terá %d anos na data da coleta (%s) e a "
                    + "inscrição de menores depende do cadastro do responsável "
                    + "legal e do termo de autorização, exigidos pelo art. 14 "
                    + "da LGPD e pelo ECA. Esse fluxo está previsto para a "
                    + "versão entregue à Companhia das Obras e não faz parte "
                    + "deste MVP.", idade, faixa.getDescricao()));
        }
        if (disponivelDas != null && disponivelAte != null
                && !disponivelAte.isAfter(disponivelDas)) {
            throw new RegraNegocioException(
                    "O horário final precisa ser maior que o inicial.");
        }

        ParticipacaoEdicao participacao = new ParticipacaoEdicao();
        participacao.setEdicaoId(edicao.getId());
        participacao.setPessoaId(pessoa.getId());
        participacao.setUnidadeOrgId(unidadeOrgId);
        participacao.setPapel(PapelParticipacao.VOLUNTARIO);
        participacao.setFaixaEtaria(faixa);
        participacao.setDisponivelDas(disponivelDas);
        participacao.setDisponivelAte(disponivelAte);
        participacao.setInscritoEm(LocalDateTime.now());

        if (!repositorio.inserir(participacao)) {
            throw new RegraNegocioException(
                    "Não foi possível gravar a inscrição. Tente novamente.");
        }
        return participacao;
    }
}
