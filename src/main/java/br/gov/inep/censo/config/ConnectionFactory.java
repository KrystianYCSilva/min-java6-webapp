package br.gov.inep.censo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static String jdbcUrl = "jdbc:h2:./data/censo2025;DB_CLOSE_DELAY=-1";
    private static String jdbcUser = "sa";
    private static String jdbcPassword = "";

    static {
        loadDriver();
    }

    private ConnectionFactory() {
    }

    private static void loadDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver JDBC H2 nao encontrado.", e);
        }
    }

    public static synchronized void configure(String url, String user, String password) {
        if (url != null && url.trim().length() > 0) {
            jdbcUrl = url.trim();
        }
        if (user != null) {
            jdbcUser = user;
        }
        if (password != null) {
            jdbcPassword = password;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }
}
