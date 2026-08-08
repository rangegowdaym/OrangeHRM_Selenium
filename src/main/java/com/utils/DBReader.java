package com.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBReader implements AutoCloseable {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public synchronized Connection connect(String dbUrl, String driverClass, String user, String password) {
        requireNotBlank(dbUrl, "dbUrl");
        requireNotBlank(driverClass, "driverClass");

        try {
            Class.forName(driverClass.trim());
            connection = DriverManager.getConnection(dbUrl.trim(), user, password);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Database connection failed for URL: " + dbUrl, e);
        }
    }

    public synchronized Statement createStatement() {
        try {
            Connection activeConnection = requireConnection();
            statement = activeConnection.createStatement();
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException("Statement creation failed", e);
        }
    }

    public synchronized ResultSet executeQuery(String query) {
        requireNotBlank(query, "query");
        try {
            if (statement == null || statement.isClosed()) {
                statement = createStatement();
            }
            closeResultSetIfOpen();
            resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException("Query execution failed: " + query, e);
        }
    }

    public synchronized int executeUpdate(String sql) {
        requireNotBlank(sql, "sql");
        try {
            if (statement == null || statement.isClosed()) {
                statement = createStatement();
            }
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Update execution failed: " + sql, e);
        }
    }

    public synchronized void closeConnection() {
        RuntimeException closeFailure = null;
        closeFailure = closeResource(resultSet, "resultSet", closeFailure);
        resultSet = null;
        closeFailure = closeResource(statement, "statement", closeFailure);
        statement = null;
        closeFailure = closeResource(connection, "connection", closeFailure);
        connection = null;

        if (closeFailure != null) {
            throw closeFailure;
        }
    }

    @Override
    public void close() {
        closeConnection();
    }

    private Connection requireConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("Connection is not available. Call connect() first.");
        }
        return connection;
    }

    private void closeResultSetIfOpen() throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            resultSet.close();
        }
    }

    private RuntimeException closeResource(AutoCloseable resource, String name, RuntimeException existingFailure) {
        if (resource == null) {
            return existingFailure;
        }
        try {
            resource.close();
            return existingFailure;
        } catch (Exception e) {
            RuntimeException currentFailure = new RuntimeException("Failed to close " + name, e);
            if (existingFailure == null) {
                return currentFailure;
            }
            existingFailure.addSuppressed(currentFailure);
            return existingFailure;
        }
    }

    private void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
