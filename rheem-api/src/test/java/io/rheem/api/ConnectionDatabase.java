package io.rheem.api;

import io.rheem.core.api.Configuration;
import io.rheem.core.api.exception.RheemException;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDatabase {

    private final String jdbcUrl, user, password, jdbcDriverClassName;

    public ConnectionDatabase(Configuration configuration, String platformID) {
        this(
                configuration.getStringProperty(String.format("rheem.%s.jdbc.url", platformID)),
                configuration.getStringProperty(String.format("rheem.%s.jdbc.user", platformID), null),
                configuration.getStringProperty(String.format("rheem.%s.jdbc.password", platformID), null),
                configuration.getStringProperty(String.format("rheem.%s.jdbc.driver.class", platformID), null)
        );
    }

    /**
     * Creates a new instance.
     *
     * @param jdbcUrl             JDBC URL to the database
     * @param user                <i>optional</i> user name or {@code null}
     * @param password            <i>optional</i> password or {@code null}
     * @param jdbcDriverClassName name of the JDBC driver {@link Class} to access the database;
     *                            required for {@link #createJdbcConnection()}
     */
    public ConnectionDatabase(String jdbcUrl, String user, String password, String jdbcDriverClassName) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.jdbcDriverClassName = jdbcDriverClassName;
    }

    /**
     * Creates a {@link Connection} to the database described by this instance.
     *
     * @return the {@link Connection}
     */
    public Connection createJdbcConnection() {
        try {
            Class.forName(this.jdbcDriverClassName);
        } catch (Exception e) {
            throw new RheemException(String.format("Could not load JDBC driver (%s).", this.jdbcDriverClassName), e);
        }

        try {
            return DriverManager.getConnection(this.jdbcUrl, this.user, this.password);
        } catch (Throwable e) {
            throw new RheemException(String.format(
                    "Could not connect to database (%s) as %s with driver %s.", this.jdbcUrl, this.user, this.jdbcDriverClassName
            ), e);
        }
    }
}
