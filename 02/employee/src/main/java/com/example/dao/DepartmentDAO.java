package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для управления отделами
 */
public class DepartmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentDAO.class);

    /**
     * Создать новый отдел
     */
    public boolean create(Department department) {
        String sql = "INSERT INTO departments (name, description) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getDescription());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    department.setId(rs.getInt(1));
                    logger.info("Отдел успешно создан: {}", department.getName());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании отдела", e);
        }
        return false;
    }

    /**
     * Получить все отделы
     */
    public List<Department> getAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM departments ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Department dept = mapResultSet(rs);
                departments.add(dept);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех отделов", e);
        }
        return departments;
    }

    /**
     * Получить отдел по ID
     */
    public Department getById(int id) {
        String sql = "SELECT id, name, description, created_at FROM departments WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении отдела по ID", e);
        }
        return null;
    }

    /**
     * Обновить отдел
     */
    public boolean update(Department department) {
        String sql = "UPDATE departments SET name = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getDescription());
            pstmt.setInt(3, department.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Отдел успешно обновлен: {}", department.getName());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении отдела", e);
        }
        return false;
    }

    /**
     * Удалить отдел
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Отдел успешно удален: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении отдела", e);
        }
        return false;
    }

    /**
     * Получить количество сотрудников в отделе
     */
    public int getEmployeeCount(int departmentId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE department_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, departmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении количества сотрудников", e);
        }
        return 0;
    }

    /**
     * Поиск отделов по названию
     */
    public List<Department> search(String query) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM departments " +
                    "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?) " +
                    "ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    departments.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске отделов", e);
        }
        return departments;
    }

    private Department mapResultSet(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setName(rs.getString("name"));
        dept.setDescription(rs.getString("description"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            dept.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return dept;
    }
}
