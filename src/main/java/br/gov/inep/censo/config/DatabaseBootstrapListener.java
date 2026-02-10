package br.gov.inep.censo.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Listener de inicializacao do banco para ambiente de prototipo.
 */
public class DatabaseBootstrapListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String url = context.getInitParameter("jdbc.url");
        String user = context.getInitParameter("jdbc.user");
        String password = context.getInitParameter("jdbc.password");

        ConnectionFactory.configure(url, user, password);

        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            executeSqlScript(connection, "db/schema.sql");
            executeSqlScript(connection, "db/seed.sql");
            executeSqlScript(connection, "db/seed_layout.sql");
            executeSqlScript(connection, "db/seed_layout_ies_docente.sql");
            executeSqlScript(connection, "db/seed_municipio.sql");

            connection.commit();
            context.log("Banco inicializado com sucesso.");
        } catch (Exception e) {
            rollbackQuietly(connection);
            context.log("Falha ao inicializar banco de dados.", e);
            throw new RuntimeException("Erro de bootstrap do banco.", e);
        } finally {
            closeQuietly(connection);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // Sem recursos globais para finalizar.
    }

    private void executeSqlScript(Connection connection, String resourcePath) throws IOException, SQLException {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Arquivo SQL nao encontrado: " + resourcePath);
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
                    String command = sql.toString();
                    command = command.substring(0, command.lastIndexOf(';'));
                    statement.execute(command);
                    sql.setLength(0);
                } else {
                    sql.append('\n');
                }
            }
        } finally {
            closeQuietly(reader);
            closeQuietly(statement);
            closeQuietly(inputStream);
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
                // noop
            }
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
                // noop
            }
        }
    }

    private void closeQuietly(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ignored) {
                // noop
            }
        }
    }

    private void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
                // noop
            }
        }
    }

    private void closeQuietly(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ignored) {
                // noop
            }
        }
    }
}
