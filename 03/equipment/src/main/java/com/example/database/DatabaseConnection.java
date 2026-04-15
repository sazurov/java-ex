package com.example.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс для управления подключением к PostgreSQL
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private static final String URL = "jdbc:postgresql://localhost:55434/equipment_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL JDBC Driver не найден", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL UNIQUE," +
                "description TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS equipment (" +
                "id SERIAL PRIMARY KEY," +
                "inventory_number VARCHAR(50) NOT NULL UNIQUE," +
                "name VARCHAR(150) NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "status VARCHAR(30) NOT NULL," +
                "location VARCHAR(150)," +
                "purchase_date DATE," +
                "cost DECIMAL(12, 2) NOT NULL DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ",FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT" +
                ")"
            );

            logger.info("База данных успешно инициализирована");
        } catch (SQLException e) {
            logger.error("Ошибка при инициализации базы данных", e);
        }
    }
}
