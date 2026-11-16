-- =====================================================================
--  SIGCA — Sistema de Gestão da Coleta de Alimentos
--  03 — Consultas  ·  PostgreSQL 16
--
--  Todas as consultas usam apenas o que foi dado em Banco de Dados:
--  junções (aula 13), GROUP BY e HAVING (aula 14), subconsulta simples
--  e funções de agregação. Não há view, gatilho, procedimento nem CTE
--  recursiva — o percurso da árvore de unidades é feito em Java, por
--  recursividade (Estrutura de Dados, aula 15).
-- =====================================================================


-- ---------------------------------------------------------------------
-- C1. Cobertura de cada ponto de coleta na edição de 2026:
--     a meta contra o número de voluntários já alocados.
-- ---------------------------------------------------------------------
SELECT pce.nome_snapshot                       AS loja,
       u.nome                                  AS unidade,
       pce.meta_voluntarios                    AS meta,
       COUNT(pa.id)                            AS alocados,
       pce.meta_voluntarios - COUNT(pa.id)     AS deficit
  FROM ponto_coleta_edicao pce
       INNER JOIN edicao e        ON e.id  = pce.edicao_id
       INNER JOIN ponto_coleta pc ON pc.id = pce.ponto_coleta_id
       INNER JOIN unidade_org u   ON u.id  = pc.unidade_org_id
       LEFT  JOIN participacao_edicao pa ON pa.ponto_coleta_edicao_id = pce.id
 WHERE e.ano = 2026
   AND pce.status_negociacao <> 'RECUSADO'
 GROUP BY pce.id, pce.nome_snapshot, u.nome, pce.meta_voluntarios
 ORDER BY deficit DESC, loja;


-- ---------------------------------------------------------------------
-- C2. Quanto foi arrecadado de cada tipo de alimento numa edição.
--     É a consulta que a campanha não conseguia responder antes.
-- ---------------------------------------------------------------------
SELECT ta.categoria,
       ta.nome                       AS alimento,
       SUM(ra.quantidade_kg)         AS total_kg,
       COUNT(DISTINCT ra.ponto_coleta_edicao_id) AS lojas
  FROM resultado_alimento ra
       INNER JOIN tipo_alimento ta       ON ta.id  = ra.tipo_alimento_id
       INNER JOIN ponto_coleta_edicao pce ON pce.id = ra.ponto_coleta_edicao_id
       INNER JOIN edicao e               ON e.id   = pce.edicao_id
 WHERE e.ano = 2025
 GROUP BY ta.categoria, ta.nome
 ORDER BY total_kg DESC;


-- ---------------------------------------------------------------------
-- C3. Total arrecadado por loja numa edição.
--     O total NÃO está armazenado: é a soma do detalhamento.
-- ---------------------------------------------------------------------
SELECT pce.nome_snapshot        AS loja,
       m.nome                   AS municipio,
       COUNT(ra.tipo_alimento_id) AS tipos_arrecadados,
       SUM(ra.quantidade_kg)    AS total_kg
  FROM ponto_coleta_edicao pce
       INNER JOIN edicao e        ON e.id  = pce.edicao_id
       INNER JOIN ponto_coleta pc ON pc.id = pce.ponto_coleta_id
       INNER JOIN municipio m     ON m.id  = pc.municipio_id
       INNER JOIN resultado_alimento ra ON ra.ponto_coleta_edicao_id = pce.id
 WHERE e.ano = 2025
 GROUP BY pce.id, pce.nome_snapshot, m.nome
 ORDER BY total_kg DESC;


-- ---------------------------------------------------------------------
-- C4. Voluntários inscritos na edição corrente que ainda não foram
--     alocados a nenhum ponto de coleta — o "pool" da regional.
-- ---------------------------------------------------------------------
SELECT COALESCE(p.nome_social, p.nome) AS voluntario,
       u.nome                          AS regional,
       pa.disponivel_das,
       pa.disponivel_ate,
       pa.inscrito_em
  FROM participacao_edicao pa
       INNER JOIN pessoa p      ON p.id = pa.pessoa_id
       INNER JOIN unidade_org u ON u.id = pa.unidade_org_id
       INNER JOIN edicao e      ON e.id = pa.edicao_id
 WHERE e.ano = 2026
   AND pa.ponto_coleta_edicao_id IS NULL
 ORDER BY pa.inscrito_em;


-- ---------------------------------------------------------------------
-- C5. Taxa de retorno: quem participou em 2025 e voltou em 2026.
--     Junção da tabela de participação consigo mesma.
-- ---------------------------------------------------------------------
SELECT COALESCE(p.nome_social, p.nome) AS voluntario,
       anterior.papel                  AS papel_2025,
       atual.papel                     AS papel_2026
  FROM participacao_edicao anterior
       INNER JOIN edicao ea ON ea.id = anterior.edicao_id AND ea.ano = 2025
       INNER JOIN participacao_edicao atual
               ON atual.pessoa_id = anterior.pessoa_id
       INNER JOIN edicao eb ON eb.id = atual.edicao_id    AND eb.ano = 2026
       INNER JOIN pessoa p  ON p.id  = anterior.pessoa_id
 ORDER BY voluntario;


-- ---------------------------------------------------------------------
-- C6. Lojas que fecharam a edição abaixo da meta de voluntários.
--     Uso de HAVING sobre o resultado da agregação.
-- ---------------------------------------------------------------------
SELECT pce.nome_snapshot    AS loja,
       pce.meta_voluntarios AS meta,
       COUNT(pa.id)         AS alocados
  FROM ponto_coleta_edicao pce
       INNER JOIN edicao e ON e.id = pce.edicao_id
       LEFT  JOIN participacao_edicao pa ON pa.ponto_coleta_edicao_id = pce.id
 WHERE e.ano = 2026
   AND pce.status_negociacao = 'CONFIRMADO'
 GROUP BY pce.id, pce.nome_snapshot, pce.meta_voluntarios
HAVING COUNT(pa.id) < pce.meta_voluntarios
 ORDER BY pce.meta_voluntarios - COUNT(pa.id) DESC;


-- ---------------------------------------------------------------------
-- C7. Quantas pessoas cadastradas por município, só onde há mais de uma.
-- ---------------------------------------------------------------------
SELECT m.nome    AS municipio,
       m.uf,
       COUNT(p.id) AS pessoas
  FROM municipio m
       INNER JOIN pessoa p ON p.municipio_id = m.id
 GROUP BY m.id, m.nome, m.uf
HAVING COUNT(p.id) > 1
 ORDER BY pessoas DESC, municipio;


-- ---------------------------------------------------------------------
-- C8. Os cinco alimentos mais arrecadados, somando todas as edições.
-- ---------------------------------------------------------------------
SELECT ta.nome              AS alimento,
       ta.categoria,
       SUM(ra.quantidade_kg) AS total_kg
  FROM resultado_alimento ra
       INNER JOIN tipo_alimento ta ON ta.id = ra.tipo_alimento_id
 GROUP BY ta.id, ta.nome, ta.categoria
 ORDER BY total_kg DESC
 LIMIT 5;


-- ---------------------------------------------------------------------
-- C9. Unidades subordinadas diretamente a uma regional, com o número de
--     pontos de coleta de cada uma. O percurso completo da árvore, em
--     qualquer profundidade, é feito em Java por recursividade.
-- ---------------------------------------------------------------------
SELECT filha.nome        AS unidade,
       filha.tipo,
       COUNT(pc.id)      AS pontos_de_coleta
  FROM unidade_org pai
       INNER JOIN unidade_org filha ON filha.unidade_org_pai_id = pai.id
       LEFT  JOIN ponto_coleta pc   ON pc.unidade_org_id = filha.id
 WHERE pai.nome = 'Regional Rio Preto'
 GROUP BY filha.id, filha.nome, filha.tipo
 ORDER BY unidade;


-- ---------------------------------------------------------------------
-- C10. Escala de uma loja: quem está alocado, e quem coordena.
--      COALESCE faz o nome social prevalecer sobre o nome de registro.
-- ---------------------------------------------------------------------
SELECT COALESCE(p.nome_social, p.nome) AS voluntario,
       pa.papel,
       pa.disponivel_das,
       pa.disponivel_ate,
       CASE WHEN pce.coordenador_participacao_id = pa.id
            THEN 'SIM' ELSE 'não' END AS coordena
  FROM participacao_edicao pa
       INNER JOIN pessoa p               ON p.id   = pa.pessoa_id
       INNER JOIN ponto_coleta_edicao pce ON pce.id = pa.ponto_coleta_edicao_id
       INNER JOIN edicao e               ON e.id   = pce.edicao_id
 WHERE e.ano = 2026
   AND pce.nome_snapshot = 'Supermercado Avenida — Centro'
 ORDER BY coordena DESC, voluntario;
