package br.gov.inep.censo.spring.datasource;

import br.gov.inep.censo.config.ConnectionFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * DataSource simples baseado na ConnectionFactory legada.
 */
public class ConnectionFactoryDataSource implements DataSource {

    public Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(ConnectionFactory.getJdbcUrl(), username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Logger de parent nao suportado.");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Wrapper nao suportado.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
