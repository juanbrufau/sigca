/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.conexao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gerencia a conexão com o banco de dados via driver JDBC.
 *
 * O arquivo postgresql-42.7.4.jar deve estar na pasta lib do projeto e
 * ser adicionado ao classpath (no NetBeans: Propriedades do projeto →
 * Bibliotecas → no "+" da linha Classpath → Add JAR/Folder).
 *
 * Todas as classes Repository herdam desta, de modo que a conexão é
 * estabelecida implicitamente quando o repositório é instanciado.
 *
 * ENDEREÇO E SENHA NÃO FICAM NO CÓDIGO. Eles são lidos do arquivo
 * banco.properties, na raiz do projeto, que não é versionado no Git —
 * senha de banco num repositório público fica no histórico para sempre,
 * mesmo que seja apagada depois. O modelo a copiar é o
 * banco.properties.exemplo, esse sim versionado.
 *
 * A leitura de arquivo de propriedades usa a classe Properties e fluxo
 * de entrada, conteúdo da aula 7 de Linguagem de Programação I.
 */
public class Conexao {

    private static final String ARQUIVO_CONFIG = "banco.properties";

    // Valores de reserva: valem quando o arquivo de configuração não
    // existe, para que o sistema ainda suba numa instalação padrão.
    private static final String URL_PADRAO =
            "jdbc:postgresql://localhost:5432/sigca";
    private static final String USUARIO_PADRAO = "postgres";
    private static final String SENHA_PADRAO = "postgres";

    /** Disponível para as subclasses Repository. */
    protected Connection conexao;

    public Conexao() {
        Properties config = carregarConfiguracao();
        String url = config.getProperty("banco.url", URL_PADRAO);
        String usuario = config.getProperty("banco.usuario", USUARIO_PADRAO);
        String senha = config.getProperty("banco.senha", SENHA_PADRAO);
        try {
            Class.forName("org.postgresql.Driver");
            this.conexao = DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Driver do PostgreSQL não encontrado. "
                    + "Confira se o .jar está na pasta lib e no classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível conectar ao banco: " + e.getMessage()
                    + "\nConfira o arquivo " + ARQUIVO_CONFIG
                    + " na raiz do projeto.", e);
        }
    }

    /**
     * Lê banco.properties da pasta onde a aplicação está sendo executada.
     * No NetBeans, essa pasta é a raiz do projeto.
     *
     * A ausência do arquivo não é erro: o sistema avisa e segue com os
     * valores de reserva.
     */
    private Properties carregarConfiguracao() {
        Properties config = new Properties();
        try (InputStreamReader leitor = new InputStreamReader(
                new FileInputStream(ARQUIVO_CONFIG), StandardCharsets.UTF_8)) {
            config.load(leitor);
        } catch (IOException e) {
            System.out.println("Aviso: " + ARQUIVO_CONFIG + " não encontrado. "
                    + "Usando a configuração padrão de conexão.");
        }
        return config;
    }

    public Connection getConexao() {
        return conexao;
    }

    /**
     * Fecha a conexão. Chamado ao encerrar a aplicação.
     */
    public void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.out.println("Aviso: falha ao fechar a conexão — "
                    + e.getMessage());
        }
    }
}
