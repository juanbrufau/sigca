-- =====================================================================
--  SIGCA — Sistema de Gestão da Coleta de Alimentos
--  02 — Carga de dados  ·  PostgreSQL 16
--
--  Dados FICTÍCIOS. Nenhum voluntário real aparece aqui: o MVP é
--  acadêmico e não recebe dado pessoal verdadeiro.
--
--  Mínimo exigido pelo gabarito: 10 inserts por tabela. Este script
--  carrega municipio: 10, unidade_org: 10, pessoa: 20, usuario: 10, edicao: 10, ponto_coleta: 12, ponto_coleta_edicao: 12, participacao_edicao: 20, tipo_alimento: 18, resultado_alimento: 37.
--
--  Senha de todos os usuários: sigca2026
--  O hash é SHA-256 de (sal || senha); o sal é próprio de cada usuário.
--
--  Para executar:  psql -U postgres -d sigca -f 02-carga-sigca.sql
-- =====================================================================

DELETE FROM resultado_alimento;
UPDATE ponto_coleta_edicao SET coordenador_participacao_id = NULL;
DELETE FROM participacao_edicao;
DELETE FROM ponto_coleta_edicao;
DELETE FROM tipo_alimento;
DELETE FROM ponto_coleta;
DELETE FROM edicao;
DELETE FROM usuario;
DELETE FROM pessoa;
DELETE FROM unidade_org;
DELETE FROM municipio;

-- ------------------------------------------------------------------
-- 1. MUNICIPIO — região de São José do Rio Preto
-- ------------------------------------------------------------------
INSERT INTO municipio
    (id, nome, uf)
VALUES
    (1, 'São José do Rio Preto', 'SP'),
    (2, 'Catanduva', 'SP'),
    (3, 'Votuporanga', 'SP'),
    (4, 'Mirassol', 'SP'),
    (5, 'Olímpia', 'SP'),
    (6, 'José Bonifácio', 'SP'),
    (7, 'Tanabi', 'SP'),
    (8, 'Bady Bassitt', 'SP'),
    (9, 'Monte Aprazível', 'SP'),
    (10, 'Nova Granada', 'SP');

-- ------------------------------------------------------------------
-- 2. UNIDADE_ORG — a árvore: 1 nacional, 4 regionais, 5 locais
-- ------------------------------------------------------------------
INSERT INTO unidade_org
    (id, unidade_org_pai_id, tipo, nome, ativo)
VALUES
    (1, NULL, 'NACIONAL', 'Companhia das Obras — Nacional', TRUE),
    (2, 1, 'REGIONAL', 'Regional Rio Preto', TRUE),
    (3, 1, 'REGIONAL', 'Regional Catanduva', TRUE),
    (4, 1, 'REGIONAL', 'Regional Votuporanga', TRUE),
    (5, 1, 'REGIONAL', 'Regional Olímpia', TRUE),
    (6, 2, 'LOCAL', 'Núcleo Centro — Rio Preto', TRUE),
    (7, 2, 'LOCAL', 'Núcleo Zona Norte — Rio Preto', TRUE),
    (8, 2, 'LOCAL', 'Núcleo Mirassol', TRUE),
    (9, 3, 'LOCAL', 'Núcleo Catanduva Centro', TRUE),
    (10, 4, 'LOCAL', 'Núcleo Votuporanga Centro', FALSE);

-- ------------------------------------------------------------------
-- 3. PESSOA — 20 pessoas fictícias; duas usam nome social
-- ------------------------------------------------------------------
INSERT INTO pessoa
    (id, municipio_id, nome, nome_social, data_nascimento, telefone, email, bairro)
VALUES
    (1, 1, 'Ana Carolina Ferreira Lima', NULL, '1985-03-14', '(17) 91037-2053', 'ana.lima@exemplo.org.br', 'Boa Vista'),
    (2, 1, 'Bruno Henrique Tavares', NULL, '1979-11-02', '(17) 91074-2106', 'bruno.tavares@exemplo.org.br', 'Redentora'),
    (3, 2, 'Camila Rodrigues Prado', NULL, '1992-07-25', '(17) 91111-2159', 'camila.prado@exemplo.org.br', 'Centro'),
    (4, 3, 'Daniel Moreira Campos', NULL, '1988-01-30', '(17) 91148-2212', 'daniel.campos@exemplo.org.br', 'Vila Marin'),
    (5, 4, 'Eduarda Nunes Ribeiro', 'Duda Nunes', '1995-09-09', '(17) 91185-2265', 'eduarda.ribeiro@exemplo.org.br', 'Jardim Alvorada'),
    (6, 5, 'Fábio Augusto Siqueira', NULL, '1974-05-18', '(17) 91222-2318', 'fabio.siqueira@exemplo.org.br', 'Centro'),
    (7, 1, 'Gabriela Souza Martins', NULL, '1990-12-04', '(17) 91259-2371', 'gabriela.martins@exemplo.org.br', 'Vila Toninho'),
    (8, 6, 'Henrique Lopes Barbosa', NULL, '1983-08-21', '(17) 91296-2424', 'henrique.barbosa@exemplo.org.br', 'Centro'),
    (9, 7, 'Isabela Cristina Rocha', NULL, '1998-02-11', '(17) 91333-2477', 'isabela.rocha@exemplo.org.br', 'Jardim Paulista'),
    (10, 8, 'João Pedro Almeida Dias', NULL, '1981-06-07', '(17) 91370-2530', 'joao.dias@exemplo.org.br', 'Centro'),
    (11, 1, 'Karina Vieira Mendonça', NULL, '1996-04-23', '(17) 91407-2583', 'karina.mendonca@exemplo.org.br', 'Schmitt'),
    (12, 1, 'Lucas Teixeira Antunes', NULL, '1993-10-16', '(17) 91444-2636', 'lucas.antunes@exemplo.org.br', 'Eldorado'),
    (13, 2, 'Mariana Costa Figueiredo', 'Mari Costa', '1987-03-03', '(17) 91481-2689', 'mariana.figueiredo@exemplo.org.br', 'Higienópolis'),
    (14, 3, 'Nelson Oliveira Branco', NULL, '1968-12-29', '(17) 91518-2742', 'nelson.branco@exemplo.org.br', 'Pozzobon'),
    (15, 4, 'Patrícia Gomes Carvalho', NULL, '1991-05-05', '(17) 91555-2795', 'patricia.carvalho@exemplo.org.br', 'Santa Luzia'),
    (16, 1, 'Rafael Monteiro Queiroz', NULL, '1999-08-12', '(17) 91592-2848', 'rafael.queiroz@exemplo.org.br', 'Solo Sagrado'),
    (17, 5, 'Sabrina Duarte Pacheco', NULL, '1986-01-19', '(17) 91629-2901', 'sabrina.pacheco@exemplo.org.br', 'Jardim Donata'),
    (18, 9, 'Thiago Correia Vasques', NULL, '1977-07-08', '(17) 91666-2954', 'thiago.vasques@exemplo.org.br', 'Centro'),
    (19, 10, 'Vanessa Pinheiro Lacerda', NULL, '1994-11-27', '(17) 91703-3007', 'vanessa.lacerda@exemplo.org.br', 'Jardim Europa'),
    (20, 1, 'Wagner Brito Salgado', NULL, '1972-09-15', '(17) 91740-3060', 'wagner.salgado@exemplo.org.br', 'Parque Industrial');

-- ------------------------------------------------------------------
-- 4. USUARIO — especialização de PESSOA: 1 nacional, 4 regionais, 5 de loja
-- ------------------------------------------------------------------
INSERT INTO usuario
    (pessoa_id, unidade_org_id, email_acesso, senha_hash, senha_sal, pergunta_seguranca, resposta_hash, perfil_sistema, ativo, ultimo_acesso_em)
VALUES
    (1, 1, 'ana.lima@cdo.org.br', '3b81dbbb2feb8117a0e7f09a4d4ce588c8a7513d724136aae9e6e21f78894d1f', '162c5dbc610f6403d438d2c29b5c1436', 'Nome da primeira escola?', '9a10a7d33f5295320d0005ac68ea5b5cfcc3b3f749a185873f82945148467065', 'ADMINISTRADOR_NACIONAL', TRUE, NULL),
    (2, 2, 'bruno.tavares@cdo.org.br', '448d2ba9ae64da8406b3a9325c98082b5b53fd715281480e97917f5c7ac82074', 'd7448f42d1771d0459e82e923d22ca4d', 'Cidade onde nasceu?', '4c00776cb70e684ba38e6e134f3307ba9bb87e2c82511592ac0f5d0725eea8fc', 'GESTOR_REGIONAL', TRUE, NULL),
    (3, 3, 'camila.prado@cdo.org.br', '0fc93abb85f88a1bc3231d707f918505394812cbc8ad5fed3ac5d7fc0cea3c6b', 'fb7eeec0a2fe29a171a704ca17c18c48', 'Nome do primeiro animal de estimação?', '9a26d72fe06c7928da7e0f2ee5f007901a074cb72a312e558b43213161901df6', 'GESTOR_REGIONAL', TRUE, NULL),
    (4, 4, 'daniel.campos@cdo.org.br', '2c7f7689d64afdf986bb5ccc629d1170049775a155783cc84630a574be7b8b81', 'd366a14b27040442ad903d3f19070c9e', 'Nome da primeira escola?', 'f5d6686eba379b57eda31ff5d780bf3856feedb5549cee33632ac97407550f54', 'GESTOR_REGIONAL', TRUE, NULL),
    (5, 5, 'eduarda.ribeiro@cdo.org.br', '41f1a6abee7c3f600ddfe9e5d322ae50f202bfd1cdc276bff51a17ac5f017330', 'fcd9b5f3ab61191af387293f6cb1898d', 'Cidade onde nasceu?', '141ad981344e9f278fd5870d09bd8a2b2d43ef856e2cbbd3f8587888c9f227b6', 'GESTOR_REGIONAL', TRUE, NULL),
    (6, 6, 'fabio.siqueira@cdo.org.br', 'f14fc0ed806794b45def17f0b5db4fed829ec7e5c1733c73a47dc09392548cbb', 'b17e06a06942c090819018922c0ecfba', 'Nome do primeiro animal de estimação?', 'a44290370d93f5607959ad9c884ec7ede8117ef4ce0aa7d7820369adfd6b896b', 'COORDENADOR_LOJA', TRUE, NULL),
    (7, 7, 'gabriela.martins@cdo.org.br', 'e7fa9a30e1ae5c88dbb921b304350b5c17e2b6b6fcbf38a3a555fc210f403be6', '15957161e911d672318d9970aa2701e3', 'Cidade onde nasceu?', '54fddab5dca496618ba49d61534241363ed93efa7121a414ab9411ed9043c3ec', 'COORDENADOR_LOJA', TRUE, NULL),
    (8, 8, 'henrique.barbosa@cdo.org.br', '79801265055a17f1dda6ee151aeabd1ea1776408c085a901fe77c345d57e9092', 'ea23143b0a87f416437cb3347b773ee9', 'Nome da primeira escola?', '0c2e75bf8f3f1b9752914c018c9046a8b737b13abbfc553ac70d36b1600e901a', 'COORDENADOR_LOJA', TRUE, NULL),
    (9, 9, 'isabela.rocha@cdo.org.br', 'a5f931713e6500026912868fb2791afe1ca9a724408d3b8db238c68c95aa71a1', '50f2f576821662f53ae04b44e6d05ee9', 'Nome do primeiro animal de estimação?', '4a8d56dffbcebc8e9ee11eeb026395003adca002eac2bf9cd89dee76ee434437', 'COORDENADOR_LOJA', TRUE, NULL),
    (10, 10, 'joao.dias@cdo.org.br', '79ba88107d64e0cffc80e57f4a98a34d5505cc38a72ece8fae2d7725d32517b2', '72ddd54d63b84cfb20944506d3002119', 'Cidade onde nasceu?', '1c769ec9a528dc0dc0e1e93e2eaa68d6af414dc3d4477f61eee45b295c33206e', 'COORDENADOR_LOJA', TRUE, NULL);

-- ------------------------------------------------------------------
-- 5. EDICAO — 2017 a 2026, sempre no primeiro sábado de novembro
-- ------------------------------------------------------------------
INSERT INTO edicao
    (id, ano, data_coleta, inscricoes_inicio, inscricoes_fim, status)
VALUES
    (1, 2017, '2017-11-04', '2017-09-05', '2017-10-28', 'ENCERRADA'),
    (2, 2018, '2018-11-03', '2018-09-04', '2018-10-27', 'ENCERRADA'),
    (3, 2019, '2019-11-02', '2019-09-03', '2019-10-26', 'ENCERRADA'),
    (4, 2020, '2020-11-07', '2020-09-08', '2020-10-31', 'ENCERRADA'),
    (5, 2021, '2021-11-06', '2021-09-07', '2021-10-30', 'ENCERRADA'),
    (6, 2022, '2022-11-05', '2022-09-06', '2022-10-29', 'ENCERRADA'),
    (7, 2023, '2023-11-04', '2023-09-05', '2023-10-28', 'ENCERRADA'),
    (8, 2024, '2024-11-02', '2024-09-03', '2024-10-26', 'ENCERRADA'),
    (9, 2025, '2025-11-01', '2025-09-02', '2025-10-25', 'ENCERRADA'),
    (10, 2026, '2026-11-07', '2026-09-08', '2026-10-31', 'INSCRICOES_ABERTAS');

-- ------------------------------------------------------------------
-- 6. PONTO_COLETA — 12 lojas
-- ------------------------------------------------------------------
INSERT INTO ponto_coleta
    (id, unidade_org_id, municipio_id, nome, endereco, ativo)
VALUES
    (1, 6, 1, 'Supermercado Avenida — Centro', 'Av. Bady Bassitt, 3200 — Centro', TRUE),
    (2, 6, 1, 'Supermercado Avenida — Redentora', 'Av. Juscelino Kubitschek, 1450 — Redentora', TRUE),
    (3, 7, 1, 'Rede Porecatu — Zona Norte', 'Av. Alberto Andaló, 4100 — Vila Imperial', TRUE),
    (4, 7, 1, 'Mercado Bom Preço — Vila Toninho', 'R. Independência, 820 — Vila Toninho', TRUE),
    (5, 8, 4, 'Supermercado Mirassol Center', 'Av. Jerônimo Gallo, 970 — Centro', TRUE),
    (6, 8, 4, 'Mercado São Jorge — Mirassol', 'R. Sete de Setembro, 215 — Centro', TRUE),
    (7, 9, 2, 'Supermercado Catanduva Mais', 'Av. Pastor José Dutra, 1180 — Centro', TRUE),
    (8, 9, 2, 'Rede Econômica — Pozzobon', 'R. Minas Gerais, 640 — Pozzobon', TRUE),
    (9, 10, 3, 'Supermercado Votuporanga Norte', 'Av. Nasser Marão, 2500 — Centro', TRUE),
    (10, 5, 5, 'Mercado Olímpia Central', 'R. Dr. Álvaro Coelho, 310 — Centro', TRUE),
    (11, 2, 8, 'Mercado Bady Bassitt', 'Av. Brasil, 155 — Centro', TRUE),
    (12, 2, 9, 'Supermercado Monte Aprazível', 'R. Independência, 1020 — Centro', TRUE);

-- ------------------------------------------------------------------
-- 7. PONTO_COLETA_EDICAO — N:N entre loja e edição (8 em 2026, 4 em 2025)
-- ------------------------------------------------------------------
INSERT INTO ponto_coleta_edicao
    (id, edicao_id, ponto_coleta_id, nome_snapshot, endereco_snapshot, meta_voluntarios, status_negociacao, resultado_em)
VALUES
    (1, 10, 1, 'Supermercado Avenida — Centro', 'Av. Bady Bassitt, 3200 — Centro', 12, 'CONFIRMADO', NULL),
    (2, 10, 2, 'Supermercado Avenida — Redentora', 'Av. Juscelino Kubitschek, 1450 — Redentora', 10, 'CONFIRMADO', NULL),
    (3, 10, 3, 'Rede Porecatu — Zona Norte', 'Av. Alberto Andaló, 4100 — Vila Imperial', 8, 'CONFIRMADO', NULL),
    (4, 10, 4, 'Mercado Bom Preço — Vila Toninho', 'R. Independência, 820 — Vila Toninho', 6, 'NEGOCIANDO', NULL),
    (5, 10, 5, 'Supermercado Mirassol Center', 'Av. Jerônimo Gallo, 970 — Centro', 8, 'CONFIRMADO', NULL),
    (6, 10, 6, 'Mercado São Jorge — Mirassol', 'R. Sete de Setembro, 215 — Centro', 5, 'PROSPECCAO', NULL),
    (7, 10, 7, 'Supermercado Catanduva Mais', 'Av. Pastor José Dutra, 1180 — Centro', 10, 'CONFIRMADO', NULL),
    (8, 10, 8, 'Rede Econômica — Pozzobon', 'R. Minas Gerais, 640 — Pozzobon', 6, 'RECUSADO', NULL),
    (9, 9, 1, 'Supermercado Avenida — Centro', 'Av. Bady Bassitt, 3200 — Centro', 10, 'CONFIRMADO', '2025-11-01 19:40:00'),
    (10, 9, 2, 'Supermercado Avenida — Redentora', 'Av. Juscelino Kubitschek, 1450 — Redentora', 9, 'CONFIRMADO', '2025-11-01 19:15:00'),
    (11, 9, 5, 'Supermercado Mirassol Center', 'Av. Jerônimo Gallo, 970 — Centro', 7, 'CONFIRMADO', '2025-11-01 18:50:00'),
    (12, 9, 7, 'Supermercado Catanduva Mais', 'Av. Pastor José Dutra, 1180 — Centro', 9, 'CONFIRMADO', '2025-11-01 19:05:00');

-- ------------------------------------------------------------------
-- 8. PARTICIPACAO_EDICAO — N:N entre pessoa e edição (14 em 2026, 6 em 2025)
-- ------------------------------------------------------------------
INSERT INTO participacao_edicao
    (id, edicao_id, pessoa_id, unidade_org_id, ponto_coleta_edicao_id, papel, faixa_etaria, disponivel_das, disponivel_ate, origem_alocacao, alocado_em, inscrito_em)
VALUES
    (1, 10, 1, 1, NULL, 'ADMINISTRADOR_NACIONAL', 'ADULTO', NULL, NULL, NULL, NULL, '2026-09-06 09:03:00'),
    (2, 10, 2, 2, NULL, 'COORDENADOR_REGIONAL', 'ADULTO', '08:00', '18:00', NULL, NULL, '2026-09-07 09:06:00'),
    (3, 10, 3, 3, NULL, 'COORDENADOR_REGIONAL', 'ADULTO', '08:00', '18:00', NULL, NULL, '2026-09-08 09:09:00'),
    (4, 10, 6, 6, 1, 'COORDENADOR_LOJA', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2026-10-05 20:14:00', '2026-09-09 09:12:00'),
    (5, 10, 7, 7, 3, 'COORDENADOR_LOJA', 'ADULTO', '09:00', '18:00', 'DISTRIBUIDA', '2026-10-05 20:15:00', '2026-09-10 09:15:00'),
    (6, 10, 9, 9, 7, 'COORDENADOR_LOJA', 'ADULTO', '08:00', '16:00', 'DISTRIBUIDA', '2026-10-05 20:16:00', '2026-09-11 09:18:00'),
    (7, 10, 11, 6, 1, 'VOLUNTARIO', 'ADULTO', '08:00', '12:00', 'DISTRIBUIDA', '2026-10-05 20:17:00', '2026-09-12 09:21:00'),
    (8, 10, 12, 6, 1, 'VOLUNTARIO', 'ADULTO', '12:00', '17:00', 'DISTRIBUIDA', '2026-10-05 20:18:00', '2026-09-13 09:24:00'),
    (9, 10, 13, 9, 7, 'VOLUNTARIO', 'ADULTO', '08:00', '13:00', 'DISTRIBUIDA', '2026-10-05 20:19:00', '2026-09-14 09:27:00'),
    (10, 10, 15, 8, 5, 'VOLUNTARIO', 'ADULTO', '09:00', '15:00', 'DISTRIBUIDA', '2026-10-05 20:20:00', '2026-09-15 09:30:00'),
    (11, 10, 16, 7, 3, 'VOLUNTARIO', 'ADULTO', '13:00', '18:00', 'DISTRIBUIDA', '2026-10-05 20:21:00', '2026-09-16 09:33:00'),
    (12, 10, 17, 5, NULL, 'VOLUNTARIO', 'ADULTO', '08:00', '14:00', NULL, NULL, '2026-09-17 09:36:00'),
    (13, 10, 19, 10, NULL, 'VOLUNTARIO', 'ADULTO', '10:00', '16:00', NULL, NULL, '2026-09-18 09:39:00'),
    (14, 10, 20, 6, 2, 'VOLUNTARIO', 'ADULTO', '08:00', '18:00', 'DISTRIBUIDA', '2026-10-05 20:24:00', '2026-09-19 09:42:00'),
    (15, 9, 11, 6, 9, 'VOLUNTARIO', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2025-10-12 21:00:00', '2025-09-20 10:30:00'),
    (16, 9, 12, 6, 9, 'VOLUNTARIO', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2025-10-12 21:00:00', '2025-09-20 10:30:00'),
    (17, 9, 13, 9, 12, 'VOLUNTARIO', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2025-10-12 21:00:00', '2025-09-20 10:30:00'),
    (18, 9, 6, 6, 9, 'COORDENADOR_LOJA', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2025-10-12 21:00:00', '2025-09-20 10:30:00'),
    (19, 9, 7, 7, 10, 'COORDENADOR_LOJA', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2025-10-12 21:00:00', '2025-09-20 10:30:00'),
    (20, 9, 15, 8, 11, 'VOLUNTARIO', 'ADULTO', '08:00', '17:00', 'DISTRIBUIDA', '2025-10-12 21:00:00', '2025-09-20 10:30:00');

-- ------------------------------------------------------------------
-- 9. TIPO_ALIMENTO — os 18 itens que a campanha arrecada hoje
-- ------------------------------------------------------------------
INSERT INTO tipo_alimento
    (id, nome, categoria, ordem, ativo)
VALUES
    (1, 'ARROZ', 'CEREAIS_E_GRAOS', 10, TRUE),
    (2, 'FEIJAO', 'CEREAIS_E_GRAOS', 20, TRUE),
    (3, 'MACARRAO', 'CEREAIS_E_GRAOS', 30, TRUE),
    (4, 'FARINHA DE MILHO', 'FARINACEOS', 40, TRUE),
    (5, 'FARINHA DE TRIGO', 'FARINACEOS', 50, TRUE),
    (6, 'FARINHA DE MANDIOCA', 'FARINACEOS', 60, TRUE),
    (7, 'FUBA', 'FARINACEOS', 70, TRUE),
    (8, 'ENLATADO ERVILHA', 'ENLATADOS', 80, TRUE),
    (9, 'ENLATADO MILHO', 'ENLATADOS', 90, TRUE),
    (10, 'ENLATADO SARDINHA', 'ENLATADOS', 100, TRUE),
    (11, 'ENLATADO SELETA', 'ENLATADOS', 110, TRUE),
    (12, 'LEITE EM PO', 'LATICINIOS_E_BEBIDAS', 120, TRUE),
    (13, 'LEITE LONGA VIDA', 'LATICINIOS_E_BEBIDAS', 130, TRUE),
    (14, 'ACHOCOLATADO', 'LATICINIOS_E_BEBIDAS', 140, TRUE),
    (15, 'ACUCAR', 'MERCEARIA', 150, TRUE),
    (16, 'OLEO DE SOJA', 'MERCEARIA', 160, TRUE),
    (17, 'MOLHO DE TOMATE', 'MERCEARIA', 170, TRUE),
    (18, 'MIX', 'OUTROS', 180, TRUE);

-- ------------------------------------------------------------------
-- 10. RESULTADO_ALIMENTO — N:N entre loja da edição e tipo de alimento
-- ------------------------------------------------------------------
INSERT INTO resultado_alimento
    (ponto_coleta_edicao_id, tipo_alimento_id, quantidade_kg, registrado_em)
VALUES
    (9, 1, 102.88, '2025-11-01 19:40:00'),
    (9, 3, 87.5, '2025-11-01 19:40:00'),
    (9, 5, 31.06, '2025-11-01 19:40:00'),
    (9, 6, 55.66, '2025-11-01 19:40:00'),
    (9, 7, 44.05, '2025-11-01 19:40:00'),
    (9, 8, 25.84, '2025-11-01 19:40:00'),
    (9, 9, 45.23, '2025-11-01 19:40:00'),
    (9, 13, 42.71, '2025-11-01 19:40:00'),
    (9, 18, 29.66, '2025-11-01 19:40:00'),
    (10, 1, 153.47, '2025-11-01 19:15:00'),
    (10, 3, 78.3, '2025-11-01 19:15:00'),
    (10, 5, 40.73, '2025-11-01 19:15:00'),
    (10, 6, 37.74, '2025-11-01 19:15:00'),
    (10, 8, 37.19, '2025-11-01 19:15:00'),
    (10, 9, 31.54, '2025-11-01 19:15:00'),
    (10, 13, 65.47, '2025-11-01 19:15:00'),
    (10, 15, 44.39, '2025-11-01 19:15:00'),
    (10, 16, 52.06, '2025-11-01 19:15:00'),
    (10, 18, 57.54, '2025-11-01 19:15:00'),
    (11, 2, 168.64, '2025-11-01 18:50:00'),
    (11, 3, 116.48, '2025-11-01 18:50:00'),
    (11, 5, 51.72, '2025-11-01 18:50:00'),
    (11, 6, 43.64, '2025-11-01 18:50:00'),
    (11, 8, 34.02, '2025-11-01 18:50:00'),
    (11, 9, 33.82, '2025-11-01 18:50:00'),
    (11, 10, 49.88, '2025-11-01 18:50:00'),
    (11, 13, 55.4, '2025-11-01 18:50:00'),
    (11, 15, 59.84, '2025-11-01 18:50:00'),
    (11, 16, 41.21, '2025-11-01 18:50:00'),
    (12, 1, 134.02, '2025-11-01 19:05:00'),
    (12, 2, 130.36, '2025-11-01 19:05:00'),
    (12, 5, 54.88, '2025-11-01 19:05:00'),
    (12, 7, 54.61, '2025-11-01 19:05:00'),
    (12, 9, 42.13, '2025-11-01 19:05:00'),
    (12, 12, 40.21, '2025-11-01 19:05:00'),
    (12, 13, 44.07, '2025-11-01 19:05:00'),
    (12, 16, 77.22, '2025-11-01 19:05:00');

-- ------------------------------------------------------------------
-- Coordenador de cada loja — segundo passo da referência circular
-- ------------------------------------------------------------------
UPDATE ponto_coleta_edicao SET coordenador_participacao_id = 4 WHERE id = 1;
UPDATE ponto_coleta_edicao SET coordenador_participacao_id = 5 WHERE id = 3;
UPDATE ponto_coleta_edicao SET coordenador_participacao_id = 6 WHERE id = 7;
UPDATE ponto_coleta_edicao SET coordenador_participacao_id = 19 WHERE id = 9;
UPDATE ponto_coleta_edicao SET coordenador_participacao_id = 20 WHERE id = 10;

-- ------------------------------------------------------------------
-- Sequências: alinhadas ao maior id inserido
-- ------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('municipio', 'id'), (SELECT MAX(id) FROM municipio));
SELECT setval(pg_get_serial_sequence('unidade_org', 'id'), (SELECT MAX(id) FROM unidade_org));
SELECT setval(pg_get_serial_sequence('pessoa', 'id'), (SELECT MAX(id) FROM pessoa));
SELECT setval(pg_get_serial_sequence('edicao', 'id'), (SELECT MAX(id) FROM edicao));
SELECT setval(pg_get_serial_sequence('ponto_coleta', 'id'), (SELECT MAX(id) FROM ponto_coleta));
SELECT setval(pg_get_serial_sequence('ponto_coleta_edicao', 'id'), (SELECT MAX(id) FROM ponto_coleta_edicao));
SELECT setval(pg_get_serial_sequence('participacao_edicao', 'id'), (SELECT MAX(id) FROM participacao_edicao));
SELECT setval(pg_get_serial_sequence('tipo_alimento', 'id'), (SELECT MAX(id) FROM tipo_alimento));
