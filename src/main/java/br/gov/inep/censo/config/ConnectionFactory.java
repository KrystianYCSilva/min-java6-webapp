package br.gov.inep.censo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static String jdbcUrl = "jdbc:h2:./data/censo2025;DB_CLOSE_DELAY=-1";
    private static String jdbcUser = "sa";
    private static String jdbcPassword = "";
    private static String loadedDriverClass;

    static {
        ensureDriverLoaded(jdbcUrl);
    }

    private ConnectionFactory() {
    }

    private static synchronized void ensureDriverLoaded(String url) {
        String driverClass = resolveDriverClass(url);
        if (driverClass.equals(loadedDriverClass)) {
            return;
        }
        try {
            Class.forName(driverClass);
            loadedDriverClass = driverClass;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver JDBC nao encontrado: " + driverClass, e);
        }
    }

    public static synchronized void configure(String url, String user, String password) {
        boolean changed = false;

        if (url != null && url.trim().length() > 0) {
            String normalizedUrl = url.trim();
            if (!normalizedUrl.equals(jdbcUrl)) {
                jdbcUrl = normalizedUrl;
                changed = true;
            }
        }
        if (user != null) {
            if (!user.equals(jdbcUser)) {
                jdbcUser = user;
                changed = true;
            }
        }
        if (password != null) {
            if (!password.equals(jdbcPassword)) {
                jdbcPassword = password;
                changed = true;
            }
        }

        if (changed) {
            ensureDriverLoaded(jdbcUrl);
            HibernateConnectionProvider.invalidate();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    public static synchronized String getJdbcUrl() {
        return jdbcUrl;
    }

    public static synchronized String getJdbcUser() {
        return jdbcUser;
    }

    public static synchronized String getJdbcPassword() {
        return jdbcPassword;
    }

    private static String resolveDriverClass(String url) {
        if (url != null && url.startsWith("jdbc:postgresql:")) {
            return "org.postgresql.Driver";
        }
        if (url != null && url.startsWith("jdbc:mysql:")) {
            return "com.mysql.jdbc.Driver";
        }
        if (url != null && url.startsWith("jdbc:db2:")) {
            return "com.ibm.db2.jcc.DB2Driver";
        }
        return "org.h2.Driver";
    }
}
