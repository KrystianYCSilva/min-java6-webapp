package br.gov.inep.censo.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fornece conexoes JDBC gerenciadas pelo Hibernate para preservar o contrato
 * atual dos DAOs legados.
 */
public final class HibernateConnectionProvider {

    private static final Object LOCK = new Object();

    private static volatile SessionFactory sessionFactory;
    private static volatile String activeUrl;
    private static volatile String activeUser;
    private static volatile String activePassword;

    private HibernateConnectionProvider() {
    }

    public static Connection getConnection() throws SQLException {
        ensureSessionFactory();
        final Session session = sessionFactory.openSession();

        final Connection rawConnection;
        try {
            rawConnection = (Connection) session.doReturningWork(new ReturningWork<Connection>() {
                public Connection execute(Connection connection) {
                    return connection;
                }
            });
        } catch (RuntimeException e) {
            closeSessionQuietly(session);
            throw new SQLException("Falha ao obter conexao via Hibernate.", e);
        }

        return createSessionBoundConnection(rawConnection, session);
    }

    public static void invalidate() {
        synchronized (LOCK) {
            closeSessionFactoryQuietly(sessionFactory);
            sessionFactory = null;
            activeUrl = null;
            activeUser = null;
            activePassword = null;
        }
    }

    public static void shutdown() {
        invalidate();
    }

    private static void ensureSessionFactory() {
        String url = ConnectionFactory.getJdbcUrl();
        String user = ConnectionFactory.getJdbcUser();
        String password = ConnectionFactory.getJdbcPassword();

        if (isCurrentConfig(url, user, password) && sessionFactory != null) {
            return;
        }

        synchronized (LOCK) {
            if (isCurrentConfig(url, user, password) && sessionFactory != null) {
                return;
            }
            closeSessionFactoryQuietly(sessionFactory);
            sessionFactory = buildSessionFactory(url, user, password);
            activeUrl = url;
            activeUser = user;
            activePassword = password;
        }
    }

    private static boolean isCurrentConfig(String url, String user, String password) {
        return equalsNullable(activeUrl, url)
                && equalsNullable(activeUser, user)
                && equalsNullable(activePassword, password);
    }

    private static boolean equalsNullable(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    private static SessionFactory buildSessionFactory(String url, String user, String password) {
        Configuration configuration = new Configuration();
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", resolveDriver(url));
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", user == null ? "" : user);
        properties.setProperty("hibernate.connection.password", password == null ? "" : password);
        properties.setProperty("hibernate.dialect", resolveDialect(url));
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "false");
        properties.setProperty("hibernate.current_session_context_class", "thread");
        configuration.setProperties(properties);

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .buildServiceRegistry();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static String resolveDriver(String url) {
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

    private static String resolveDialect(String url) {
        if (url != null && url.startsWith("jdbc:postgresql:")) {
            return "org.hibernate.dialect.PostgreSQLDialect";
        }
        if (url != null && url.startsWith("jdbc:mysql:")) {
            return "org.hibernate.dialect.MySQL5InnoDBDialect";
        }
        if (url != null && url.startsWith("jdbc:db2:")) {
            return "org.hibernate.dialect.DB2Dialect";
        }
        return "org.hibernate.dialect.H2Dialect";
    }

    private static Connection createSessionBoundConnection(final Connection delegate, final Session session) {
        InvocationHandler handler = new InvocationHandler() {
            private boolean closed;

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();

                if ("close".equals(methodName)) {
                    closeProxyConnection();
                    return null;
                }
                if ("isClosed".equals(methodName)) {
                    if (closed) {
                        return Boolean.TRUE;
                    }
                    return Boolean.valueOf(delegate.isClosed());
                }
                if (closed) {
                    throw new SQLException("Conexao encerrada.");
                }

                try {
                    return method.invoke(delegate, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }

            private void closeProxyConnection() {
                if (closed) {
                    return;
                }
                closed = true;
                closeSessionQuietly(session);
            }
        };

        return (Connection) Proxy.newProxyInstance(
                HibernateConnectionProvider.class.getClassLoader(),
                new Class[]{Connection.class},
                handler);
    }

    private static void closeSessionFactoryQuietly(SessionFactory factory) {
        if (factory != null) {
            try {
                factory.close();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }

    private static void closeSessionQuietly(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }
}
