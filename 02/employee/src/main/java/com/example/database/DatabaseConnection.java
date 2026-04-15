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
    
    private static final String URL = "jdbc:postgresql://localhost:55433/employee_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL JDBC Driver не найден", e);
        }
    }

    /**
     * Получить подключение к базе данных
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Инициализировать базу данных (создать таблицы)
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Создание таблицы отделов
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS departments (" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL UNIQUE," +
                "description TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Создание таблицы сотрудников
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id SERIAL PRIMARY KEY," +
                "first_name VARCHAR(50) NOT NULL," +
                "last_name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) NOT NULL UNIQUE," +
                "phone VARCHAR(20)," +
                "salary DECIMAL(10, 2) NOT NULL," +
                "hire_date DATE NOT NULL," +
                "position VARCHAR(100) NOT NULL," +
                "department_id INTEGER NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE" +
                ")"
            );

            logger.info("База данных успешно инициализирована");

        } catch (SQLException e) {
            logger.error("Ошибка при инициализации базы данных", e);
        }
    }
}
