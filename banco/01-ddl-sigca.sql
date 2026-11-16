-- =====================================================================
--  SIGCA — Sistema de Gestão da Coleta de Alimentos
--  01 — DDL do MVP  ·  PostgreSQL 16
--
--  Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
--  Tecnologia em Informática para Negócios, 3º período
--
--  Dez tabelas, recorte do modelo de dados v4 (26 entidades).
--  Três relacionamentos N:N, uma generalização e uma árvore.
--
--  Para executar:  psql -U postgres -f 01-ddl-sigca.sql
-- =====================================================================

DROP TABLE IF EXISTS resultado_alimento   CASCADE;
DROP TABLE IF EXISTS participacao_edicao  CASCADE;
DROP TABLE IF EXISTS ponto_coleta_edicao  CASCADE;
DROP TABLE IF EXISTS tipo_alimento        CASCADE;
DROP TABLE IF EXISTS ponto_coleta         CASCADE;
DROP TABLE IF EXISTS edicao               CASCADE;
DROP TABLE IF EXISTS usuario              CASCADE;
DROP TABLE IF EXISTS pessoa               CASCADE;
DROP TABLE IF EXISTS unidade_org          CASCADE;
DROP TABLE IF EXISTS municipio            CASCADE;


-- ---------------------------------------------------------------------
-- 1. MUNICIPIO
--    Cidade e UF. Tabela de apoio: evita a dependência transitiva
--    cidade -> uf dentro de PESSOA e de PONTO_COLETA (3FN, BD aula 16).
-- ---------------------------------------------------------------------
CREATE TABLE municipio (
    id    SERIAL       NOT NULL,
    nome  VARCHAR(80)  NOT NULL,
    uf    CHAR(2)      NOT NULL,
    CONSTRAINT pk_municipio PRIMARY KEY (id),
    CONSTRAINT uq_municipio_nome_uf UNIQUE (nome, uf)
);

COMMENT ON TABLE municipio IS 'Municípios usados por pessoas e pontos de coleta.';


-- ---------------------------------------------------------------------
-- 2. UNIDADE_ORG
--    Estrutura da campanha: nacional -> regional -> local.
--    Auto-relacionamento 1:N — a árvore percorrida por recursividade
--    (Estrutura de Dados, aulas 15 e 17). É ela que define o escopo de
--    visualização de cada usuário.
-- ---------------------------------------------------------------------
CREATE TABLE unidade_org (
    id                  SERIAL       NOT NULL,
    unidade_org_pai_id  INTEGER      NULL,
    tipo                VARCHAR(10)  NOT NULL,
    nome                VARCHAR(80)  NOT NULL,
    ativo               BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_unidade_org PRIMARY KEY (id),
    CONSTRAINT fk_unidade_org_pai FOREIGN KEY (unidade_org_pai_id)
        REFERENCES unidade_org (id),
    CONSTRAINT ck_unidade_org_tipo
        CHECK (tipo IN ('NACIONAL', 'REGIONAL', 'LOCAL')),
    -- a raiz é a única sem pai, e nenhuma unidade é pai de si mesma
    CONSTRAINT ck_unidade_org_raiz
        CHECK ((tipo = 'NACIONAL' AND unidade_org_pai_id IS NULL)
            OR (tipo <> 'NACIONAL' AND unidade_org_pai_id IS NOT NULL)),
    CONSTRAINT ck_unidade_org_autopai CHECK (unidade_org_pai_id <> id)
);

COMMENT ON TABLE unidade_org IS 'Árvore organizacional da campanha; define o escopo do usuário.';


-- ---------------------------------------------------------------------
-- 3. PESSOA
--    Cadastro permanente de toda pessoa física do domínio.
--    Sem CPF e sem RG, por minimização (LGPD, art. 6º, III).
-- ---------------------------------------------------------------------
CREATE TABLE pessoa (
    id               SERIAL        NOT NULL,
    municipio_id     INTEGER       NULL,
    nome             VARCHAR(120)  NOT NULL,
    nome_social      VARCHAR(120)  NULL,
    data_nascimento  DATE          NOT NULL,
    telefone         VARCHAR(20)   NULL,
    email            VARCHAR(120)  NULL,
    bairro           VARCHAR(80)   NULL,
    criado_em        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_pessoa PRIMARY KEY (id),
    CONSTRAINT fk_pessoa_municipio FOREIGN KEY (municipio_id)
        REFERENCES municipio (id),
    CONSTRAINT ck_pessoa_nascimento CHECK (data_nascimento < CURRENT_DATE)
);

COMMENT ON COLUMN pessoa.nome_social IS 'Quando preenchido, é ele que aparece nas telas e nos documentos.';


-- ---------------------------------------------------------------------
-- 4. USUARIO
--    Especialização de PESSOA (generalização/especialização, BD aula 6):
--    quem tem credencial de acesso. O voluntário comum não tem.
--    A chave primária é a da pessoa — é isso que torna a herança total.
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    pessoa_id           INTEGER       NOT NULL,
    unidade_org_id      INTEGER       NOT NULL,
    email_acesso        VARCHAR(120)  NOT NULL,
    senha_hash          CHAR(64)      NOT NULL,
    senha_sal           CHAR(32)      NOT NULL,
    pergunta_seguranca  VARCHAR(120)  NOT NULL,
    resposta_hash       CHAR(64)      NOT NULL,
    perfil_sistema      VARCHAR(25)   NOT NULL,
    ativo               BOOLEAN       NOT NULL DEFAULT TRUE,
    ultimo_acesso_em    TIMESTAMP     NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_usuario_pessoa FOREIGN KEY (pessoa_id)
        REFERENCES pessoa (id),
    CONSTRAINT fk_usuario_unidade FOREIGN KEY (unidade_org_id)
        REFERENCES unidade_org (id),
    CONSTRAINT uq_usuario_email UNIQUE (email_acesso),
    CONSTRAINT ck_usuario_perfil CHECK (perfil_sistema IN
        ('ADMINISTRADOR_NACIONAL', 'GESTOR_REGIONAL', 'COORDENADOR_LOJA'))
);

COMMENT ON COLUMN usuario.senha_hash IS 'SHA-256 de (sal + senha), 64 caracteres hexadecimais.';
COMMENT ON COLUMN usuario.pergunta_seguranca IS 'Recuperação de senha sem envio de e-mail; o serviço de e-mail entra na entrega ao cliente.';


-- ---------------------------------------------------------------------
-- 5. EDICAO
--    A realização anual da campanha, sempre no primeiro sábado de
--    novembro. É o eixo do tempo do sistema.
-- ---------------------------------------------------------------------
CREATE TABLE edicao (
    id                 SERIAL       NOT NULL,
    ano                INTEGER      NOT NULL,
    data_coleta        DATE         NOT NULL,
    inscricoes_inicio  DATE         NOT NULL,
    inscricoes_fim     DATE         NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_edicao PRIMARY KEY (id),
    CONSTRAINT uq_edicao_ano UNIQUE (ano),
    CONSTRAINT ck_edicao_status CHECK (status IN
        ('PLANEJAMENTO', 'INSCRICOES_ABERTAS', 'EM_EXECUCAO', 'ENCERRADA')),
    CONSTRAINT ck_edicao_janela CHECK (inscricoes_fim >= inscricoes_inicio),
    CONSTRAINT ck_edicao_coleta CHECK (data_coleta >= inscricoes_inicio)
);


-- ---------------------------------------------------------------------
-- 6. PONTO_COLETA
--    A loja onde os voluntários recebem as doações. Permanece
--    cadastrada entre as edições.
-- ---------------------------------------------------------------------
CREATE TABLE ponto_coleta (
    id              SERIAL        NOT NULL,
    unidade_org_id  INTEGER       NOT NULL,
    municipio_id    INTEGER       NOT NULL,
    nome            VARCHAR(120)  NOT NULL,
    endereco        VARCHAR(160)  NOT NULL,
    ativo           BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_ponto_coleta PRIMARY KEY (id),
    CONSTRAINT fk_ponto_coleta_unidade FOREIGN KEY (unidade_org_id)
        REFERENCES unidade_org (id),
    CONSTRAINT fk_ponto_coleta_municipio FOREIGN KEY (municipio_id)
        REFERENCES municipio (id)
);


-- ---------------------------------------------------------------------
-- 7. PONTO_COLETA_EDICAO
--    Resolve o N:N entre PONTO_COLETA e EDICAO.
--    Carrega o que é daquele ano: a meta, o status da negociação, o
--    congelamento do nome e do endereço e o momento do fechamento.
-- ---------------------------------------------------------------------
CREATE TABLE ponto_coleta_edicao (
    id                 SERIAL        NOT NULL,
    edicao_id          INTEGER       NOT NULL,
    ponto_coleta_id    INTEGER       NOT NULL,
    nome_snapshot      VARCHAR(120)  NOT NULL,
    endereco_snapshot  VARCHAR(160)  NOT NULL,
    meta_voluntarios   INTEGER       NOT NULL DEFAULT 0,
    status_negociacao  VARCHAR(15)   NOT NULL,
    resultado_em       TIMESTAMP     NULL,
    CONSTRAINT pk_ponto_coleta_edicao PRIMARY KEY (id),
    CONSTRAINT fk_pce_edicao FOREIGN KEY (edicao_id) REFERENCES edicao (id),
    CONSTRAINT fk_pce_ponto FOREIGN KEY (ponto_coleta_id)
        REFERENCES ponto_coleta (id),
    CONSTRAINT uq_pce UNIQUE (edicao_id, ponto_coleta_id),
    CONSTRAINT ck_pce_meta CHECK (meta_voluntarios >= 0),
    CONSTRAINT ck_pce_status CHECK (status_negociacao IN
        ('PROSPECCAO', 'NEGOCIANDO', 'CONFIRMADO', 'RECUSADO'))
);

COMMENT ON COLUMN ponto_coleta_edicao.nome_snapshot IS 'Nome da loja no momento da ativação; a loja pode ser renomeada depois.';


-- ---------------------------------------------------------------------
-- 8. PARTICIPACAO_EDICAO
--    Resolve o N:N entre PESSOA e EDICAO: a inscrição do voluntário.
--    O papel pertence à edição, não à pessoa — quem coordena num ano é
--    voluntário no outro.
-- ---------------------------------------------------------------------
CREATE TABLE participacao_edicao (
    id                      SERIAL       NOT NULL,
    edicao_id               INTEGER      NOT NULL,
    pessoa_id               INTEGER      NOT NULL,
    unidade_org_id          INTEGER      NOT NULL,
    ponto_coleta_edicao_id  INTEGER      NULL,
    papel                   VARCHAR(25)  NOT NULL,
    faixa_etaria            VARCHAR(12)  NOT NULL,
    disponivel_das          TIME         NULL,
    disponivel_ate          TIME         NULL,
    origem_alocacao         VARCHAR(12)  NULL,
    alocado_em              TIMESTAMP    NULL,
    inscrito_em             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_participacao_edicao PRIMARY KEY (id),
    CONSTRAINT fk_part_edicao FOREIGN KEY (edicao_id) REFERENCES edicao (id),
    CONSTRAINT fk_part_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id),
    CONSTRAINT fk_part_unidade FOREIGN KEY (unidade_org_id)
        REFERENCES unidade_org (id),
    CONSTRAINT fk_part_pce FOREIGN KEY (ponto_coleta_edicao_id)
        REFERENCES ponto_coleta_edicao (id),
    CONSTRAINT uq_part_pessoa_edicao UNIQUE (edicao_id, pessoa_id),
    CONSTRAINT ck_part_papel CHECK (papel IN
        ('VOLUNTARIO', 'COORDENADOR_LOJA', 'COORDENADOR_REGIONAL',
         'ADMINISTRADOR_NACIONAL')),
    CONSTRAINT ck_part_faixa CHECK (faixa_etaria IN
        ('CRIANCA', 'ADOLESCENTE', 'ADULTO')),
    CONSTRAINT ck_part_origem CHECK (origem_alocacao IS NULL
        OR origem_alocacao IN ('DISTRIBUIDA', 'ESPONTANEA')),
    CONSTRAINT ck_part_disponibilidade CHECK (disponivel_das IS NULL
        OR disponivel_ate IS NULL OR disponivel_ate > disponivel_das),
    -- alocação é um par: ou tem ponto e origem, ou não tem nenhum dos dois
    CONSTRAINT ck_part_alocacao CHECK (
        (ponto_coleta_edicao_id IS NULL AND origem_alocacao IS NULL
             AND alocado_em IS NULL)
     OR (ponto_coleta_edicao_id IS NOT NULL AND origem_alocacao IS NOT NULL
             AND alocado_em IS NOT NULL))
);

COMMENT ON COLUMN participacao_edicao.faixa_etaria IS
    'CRIANCA e ADOLESCENTE existem no domínio para a entrega ao cliente; o MVP recusa a inscrição de menor na camada de serviço.';


-- ---------------------------------------------------------------------
--    Coordenador da loja na edição. É acrescentado depois porque
--    PONTO_COLETA_EDICAO e PARTICIPACAO_EDICAO se referenciam
--    mutuamente: ambas as colunas são nulas, então a inserção é feita
--    em dois passos.
-- ---------------------------------------------------------------------
ALTER TABLE ponto_coleta_edicao
    ADD COLUMN coordenador_participacao_id INTEGER NULL;

ALTER TABLE ponto_coleta_edicao
    ADD CONSTRAINT fk_pce_coordenador
        FOREIGN KEY (coordenador_participacao_id)
        REFERENCES participacao_edicao (id);

ALTER TABLE ponto_coleta_edicao
    ADD CONSTRAINT uq_pce_coordenador UNIQUE (coordenador_participacao_id);


-- ---------------------------------------------------------------------
-- 9. TIPO_ALIMENTO
--    Lista controlada dos alimentos que a campanha arrecada.
--    Existe para que 'arroz', 'Arroz' e 'arroz tipo 1' não virem três
--    linhas diferentes no relatório.
-- ---------------------------------------------------------------------
CREATE TABLE tipo_alimento (
    id         SERIAL       NOT NULL,
    nome       VARCHAR(40)  NOT NULL,
    categoria  VARCHAR(25)  NOT NULL,
    ordem      INTEGER      NOT NULL,
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_tipo_alimento PRIMARY KEY (id),
    CONSTRAINT uq_tipo_alimento_nome UNIQUE (nome),
    CONSTRAINT ck_tipo_alimento_categoria CHECK (categoria IN
        ('CEREAIS_E_GRAOS', 'FARINACEOS', 'ENLATADOS',
         'LATICINIOS_E_BEBIDAS', 'MERCEARIA', 'OUTROS'))
);

COMMENT ON TABLE tipo_alimento IS 'Tudo é pesado em quilograma, inclusive óleo e leite longa vida.';


-- ---------------------------------------------------------------------
-- 10. RESULTADO_ALIMENTO
--     Resolve o N:N entre PONTO_COLETA_EDICAO e TIPO_ALIMENTO.
--     O total arrecadado da loja NÃO é armazenado: é a soma destas
--     linhas. Guardar total e detalhe ao mesmo tempo criaria duas
--     versões da verdade.
-- ---------------------------------------------------------------------
CREATE TABLE resultado_alimento (
    ponto_coleta_edicao_id  INTEGER        NOT NULL,
    tipo_alimento_id        INTEGER        NOT NULL,
    quantidade_kg           NUMERIC(10,2)  NOT NULL,
    registrado_em           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_resultado_alimento
        PRIMARY KEY (ponto_coleta_edicao_id, tipo_alimento_id),
    CONSTRAINT fk_resultado_pce FOREIGN KEY (ponto_coleta_edicao_id)
        REFERENCES ponto_coleta_edicao (id),
    CONSTRAINT fk_resultado_tipo FOREIGN KEY (tipo_alimento_id)
        REFERENCES tipo_alimento (id),
    CONSTRAINT ck_resultado_quantidade CHECK (quantidade_kg > 0)
);


-- =====================================================================
--  Fim do DDL. Dez tabelas, 3 relacionamentos N:N, 1 generalização,
--  1 auto-relacionamento em árvore.
-- =====================================================================
