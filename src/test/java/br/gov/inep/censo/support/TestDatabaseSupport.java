package br.gov.inep.censo.support;

import br.gov.inep.censo.config.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utilitario para preparar o banco em memoria dos testes de integracao.
 */
public final class TestDatabaseSupport {

    private static final String JDBC_URL = "jdbc:h2:mem:censo_test;DB_CLOSE_DELAY=-1";

    private TestDatabaseSupport() {
    }

    public static void resetDatabase() throws Exception {
        ConnectionFactory.configure(JDBC_URL, "sa", "");
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            statement.execute("DROP ALL OBJECTS");
            closeQuietly(statement);

            executeSqlScript(connection, "db/schema.sql");
            executeSqlScript(connection, "db/seed.sql");
            executeSqlScript(connection, "db/seed_layout.sql");
            executeSqlScript(connection, "db/seed_layout_ies_docente.sql");
            executeSqlScript(connection, "db/seed_municipio.sql");

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            closeQuietly(statement);
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static void executeSqlScript(Connection connection, String resourcePath) throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalStateException("Script nao encontrado: " + resourcePath);
        }

        BufferedReader reader = null;
        Statement statement = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            statement = connection.createStatement();

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.length() == 0 || trimmed.startsWith("--")) {
                    continue;
                }
                sql.append(line);
                if (trimmed.endsWith(";")) {
                    String command = sql.substring(0, sql.length() - 1);
                    statement.execute(command);
                    sql.setLength(0);
                } else {
                    sql.append('\n');
                }
            }
        } finally {
            closeQuietly(statement);
            if (reader != null) {
                reader.close();
            }
            inputStream.close();
        }
    }

    private static void closeQuietly(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ignored) {
                // noop
            }
        }
    }
}
