# SIGCA — Sistema de Gestão da Coleta de Alimentos

Projeto de Extensão Comunitária (Curricularização)
**Fatec São José do Rio Preto** · Tecnologia em Informática para Negócios · 3º período

Sistema de apoio à campanha anual de coleta de alimentos da **Companhia das Obras**,
realizada há vinte anos no primeiro sábado de novembro.

---

## O que este MVP faz

Cobre o caminho principal da campanha: cadastrar pessoas, inscrever voluntários numa
edição, ativar lojas como pontos de coleta, distribuir os voluntários entre elas e
registrar quanto foi arrecadado **de cada tipo de alimento** em cada loja.

O acesso é controlado por perfil, e cada perfil enxerga uma fatia diferente do sistema:

| Perfil | O que enxerga | O que faz |
|---|---|---|
| **Gestor Regional** | a sua unidade organizacional e todas as subordinadas | abre edição, cadastra pontos, inscreve e distribui voluntários, vê o relatório da regional |
| **Coordenador de Loja** | apenas o ponto de coleta que coordena | lança o resultado por tipo de alimento e consulta os seus voluntários |

O Administrador Nacional é o mesmo código do Gestor Regional, com a raiz da árvore
como escopo.

---

## Requisitos

- **JDK 17 ou superior** (desenvolvido e testado no JDK 21)
- **PostgreSQL 16**
- **Driver JDBC do PostgreSQL** — `postgresql-42.7.4.jar`, que precisa ser baixado
  em <https://jdbc.postgresql.org/download/> e colocado na pasta `lib/` deste projeto.
  O arquivo não vai no repositório.

---

## Como montar o banco

```bash
# 1. criar o banco
psql -U postgres -c "CREATE DATABASE sigca;"

# 2. criar as tabelas
psql -U postgres -d sigca -f banco/01-ddl-sigca.sql

# 3. carregar os dados de teste
psql -U postgres -d sigca -f banco/02-carga-sigca.sql

# 4. (opcional) conferir as consultas
psql -U postgres -d sigca -f banco/03-consultas-sigca.sql
```

A carga traz **159 linhas** distribuídas nas dez tabelas, nenhuma com menos de dez
registros, como o gabarito exige. Todos os dados são **fictícios**: o MVP acadêmico
não recebe dado pessoal verdadeiro de voluntário.

### Usuários carregados

Todos com a senha **`sigca2026`**.

| E-mail de acesso | Perfil |
|---|---|
| `ana.lima@cdo.org.br` | Administrador Nacional |
| `bruno.tavares@cdo.org.br` | Gestor Regional (Rio Preto) |
| `camila.prado@cdo.org.br` | Gestor Regional (Catanduva) |
| `fabio.siqueira@cdo.org.br` | Coordenador de Loja |
| `gabriela.martins@cdo.org.br` | Coordenador de Loja |

A senha nunca é gravada em texto: o que vai para o banco é o resumo SHA-256 de
(sal ‖ senha), com um sal sorteado por usuário.

---

## Como rodar

### Pela linha de comando

```bash
javac -encoding UTF-8 -d build $(find src -name '*.java')

java -cp build:lib/postgresql-42.7.4.jar sigca.AppGrafico   # versão com janelas
java -cp build:lib/postgresql-42.7.4.jar sigca.App          # versão de console
```

O sistema tem **duas interfaces sobre o mesmo núcleo**: `sigca.AppGrafico` abre as
janelas Swing exigidas no item 4.1 do gabarito, e `sigca.App` abre o menu de console no
formato da metodologia da disciplina. As duas chamam os mesmos repositórios e os mesmos
serviços — nenhuma regra de negócio foi duplicada.

No Windows, troque os dois-pontos do classpath por ponto e vírgula:
`java -cp build;lib\postgresql-42.7.4.jar sigca.App`

### No NetBeans

1. Arquivo → Novo Projeto → Java com Ant → Projeto Java com Fontes Existentes;
2. aponte a pasta `src` como pasta de fontes;
3. Propriedades do Projeto → Bibliotecas → Adicionar JAR → `lib/postgresql-42.7.4.jar`;
4. classe principal: `sigca.AppGrafico` (janelas) ou `sigca.App` (console).

### Se o banco não estiver no padrão

Ajuste `URL`, `USUARIO` e `SENHA` em `src/sigca/conexao/Conexao.java`.

---

## Estrutura do projeto

```
sigca/
├── banco/
│   ├── 01-ddl-sigca.sql        dez tabelas, restrições e comentários
│   ├── 02-carga-sigca.sql      159 linhas de dados fictícios
│   └── 03-consultas-sigca.sql  dez consultas da disciplina de Banco de Dados
├── lib/                        driver JDBC (não versionado)
└── src/sigca/
    ├── App.java                aplicação de console, menu do-while + switch-case
    ├── entities/               classes de entidade e enumerações
    ├── conexao/Conexao.java    conexão JDBC; os repositórios herdam dela
    ├── repository/             um repositório por tabela, com todo o SQL
    ├── servico/                regras de negócio
    └── ui/                     seis telas Swing do item 4.1
```

> **Sobre o nome do pacote das entidades.** A imagem do gabarito pede o pacote
> `entities`; o sumário do arquivo .docx pede `model`. Adotamos **`entities`**, que é a
> versão mais recente do enunciado. Os dois nomes se referem à mesma camada: as classes
> que representam as tabelas do banco.

### As dez tabelas

| # | Tabela | Papel |
|---|---|---|
| 1 | `municipio` | cidade e UF |
| 2 | `unidade_org` | árvore nacional → regional → local |
| 3 | `pessoa` | cadastro base |
| 4 | `usuario` | **especialização** de `pessoa`: quem tem acesso |
| 5 | `edicao` | a campanha de cada ano |
| 6 | `ponto_coleta` | a loja |
| 7 | `ponto_coleta_edicao` | **N:N** loja × edição |
| 8 | `participacao_edicao` | **N:N** pessoa × edição (a inscrição) |
| 9 | `tipo_alimento` | os 18 itens que a campanha arrecada |
| 10 | `resultado_alimento` | **N:N** loja da edição × tipo de alimento |

O gabarito pede no mínimo cinco tabelas e um relacionamento N:N; o MVP tem **dez
tabelas e três N:N**, mais uma generalização e uma árvore.

---

## Decisões de implementação

**O total arrecadado não é armazenado.** Nem o da loja, nem o da entrega: os dois são a
soma de `resultado_alimento`. Guardar o total e o detalhe ao mesmo tempo criaria duas
versões da verdade, que divergem no primeiro acerto de lançamento.

**A busca é feita pelo banco.** Toda pesquisa usa `LIKE`/`ILIKE` com o curinga passado
como valor do parâmetro — nunca filtrando a lista com `for` e `if` no Java.

**Nada é concatenado na query.** Todos os valores entram por `PreparedStatement`, o que
previne SQL Injection.

**A recursão fica no Java.** O percurso da árvore de unidades organizacionais, que é o
que define o escopo de cada usuário, é feito por recursividade em `ServicoEscopo`.
Consulta recursiva (`WITH RECURSIVE`) não faz parte do programa de Banco de Dados.

**Menores de idade.** O sistema calcula a faixa etária conforme o art. 2º do ECA
(criança até doze anos incompletos, adolescente de doze a dezoito) e **recusa a
inscrição de menor**, com mensagem explicando por quê: o cadastro do responsável legal
e o termo de autorização, exigidos pelo art. 14 da LGPD, estão previstos para a versão
entregue à Companhia das Obras. As colunas já existem no banco.

**Nenhum documento de identidade é coletado** — nem CPF, nem RG — por minimização
(LGPD, art. 6º, III).

---

## O que foi usado de cada disciplina

**Banco de Dados** — modelo conceitual e lógico (aulas 4 a 8), generalização e
especialização de `pessoa`/`usuario` (aula 6), restrições de integridade: PK, FK,
UNIQUE e CHECK (aula 9), DDL e DML (aula 12), junções (aula 13), `GROUP BY` e `HAVING`
(aula 14), normalização até a 3FN (aula 16).

**Linguagem de Programação I** — classes, atributos, construtores e pacotes (aula 8),
encapsulamento e herança (aula 9), sobrescrita de `toString()` (aulas 10 e 11),
elementos estáticos (aula 14), polimorfismo e `enum` (aula 16), tratamento de exceções
(aula 5), `String` e formatação (aula 7).

**Estrutura de Dados** — `ArrayList` nas listas devolvidas pelos repositórios
(aula 13), `Map` na consolidação do arrecadado por alimento (aula 16), **árvore e
recursividade** no percurso da estrutura organizacional (aulas 15 e 17).

**Engenharia de Software** — requisitos funcionais e não funcionais, casos de uso de
alto nível e expandidos, modelo conceitual, arquitetura em camadas, diagramas de
sequência do sistema e contratos de operação, todos no método do Larman.

### Recursos fora do programa, declarados

| Recurso | Por que entra | Alternativa dentro do programa |
|---|---|---|
| **JDBC** | é a única ponte entre o que Banco de Dados exige (SGBD, DDL, consultas) e o que LP1 ensina (Java com interface) | persistir em arquivos (LP1, aula 7), o que deixaria a disciplina de Banco de Dados sem entrega |
| **`MessageDigest` / `SecureRandom`** | gravar senha em texto é indefensável num sistema com dados de voluntários; ambos são da biblioteca padrão e não exigem dependência externa | nenhuma: a alternativa seria não proteger a senha |

Na versão entregue ao cliente, a senha passará a usar **BCrypt** ou equivalente, que é
próprio para esse fim e tem custo de cálculo ajustável.

---

## Situação

| Entrega | Situação |
|---|---|
| Modelagem do banco e DDL | pronto |
| Carga de dados | pronto |
| Consultas da disciplina | pronto |
| Entidades e enumerações | pronto |
| Repositórios (DAO) | pronto |
| Serviços (segurança, escopo, inscrição) | pronto |
| Aplicação de console | pronto |
| Telas Swing do item 4.1 do gabarito | pronto |
| Diagrama de classes de projeto | em desenvolvimento |

---

*Trabalho acadêmico. Os dados de teste são fictícios e não representam pessoas reais.*
