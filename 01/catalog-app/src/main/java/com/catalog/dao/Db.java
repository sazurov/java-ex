package com.catalog.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private static final String URL = env("CATALOG_DB_URL", "jdbc:postgresql://localhost:5432/catalog");
    private static final String USER = env("CATALOG_DB_USER", "catalog");
    private static final String PASSWORD = env("CATALOG_DB_PASSWORD", "catalog");

    private Db() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
