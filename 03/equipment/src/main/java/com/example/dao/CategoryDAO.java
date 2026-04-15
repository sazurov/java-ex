package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);

    public boolean create(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    category.setId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании категории", e);
        }
        return false;
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM categories ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении категорий", e);
        }
        return categories;
    }

    public Category getById(int id) {
        String sql = "SELECT id, name, description, created_at FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении категории", e);
        }
        return null;
    }

    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setInt(3, category.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении категории", e);
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении категории", e);
        }
        return false;
    }

    public List<Category> search(String query) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM categories " +
                "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(COALESCE(description, '')) LIKE LOWER(?) ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске категорий", e);
        }
        return categories;
    }

    public int getEquipmentCount(int categoryId) {
        String sql = "SELECT COUNT(*) FROM equipment WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при подсчете оборудования категории", e);
        }
        return 0;
    }

    private Category mapResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            category.setCreatedAt(ts.toLocalDateTime());
        }
        return category;
    }
}
