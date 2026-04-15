package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Equipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentDAO.class);

    public boolean create(Equipment equipment) {
        String sql = "INSERT INTO equipment (inventory_number, name, category_id, status, location, purchase_date, cost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(equipment, pstmt);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipment.setId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании оборудования", e);
        }
        return false;
    }

    public List<Equipment> getAll() {
        List<Equipment> result = new ArrayList<>();
        String sql = baseSelect() + " ORDER BY e.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении списка оборудования", e);
        }
        return result;
    }

    public Equipment getById(int id) {
        String sql = baseSelect() + " WHERE e.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении оборудования", e);
        }
        return null;
    }

    public boolean update(Equipment equipment) {
        String sql = "UPDATE equipment SET inventory_number = ?, name = ?, category_id = ?, status = ?, " +
                "location = ?, purchase_date = ?, cost = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            fillStatement(equipment, pstmt);
            pstmt.setInt(8, equipment.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении оборудования", e);
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении оборудования", e);
        }
        return false;
    }

    public List<Equipment> searchAndFilter(String query, Integer categoryId, String status, BigDecimal minCost, BigDecimal maxCost) {
        List<Equipment> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseSelect() + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(e.inventory_number) LIKE LOWER(?) OR LOWER(e.name) LIKE LOWER(?) OR LOWER(COALESCE(e.location, '')) LIKE LOWER(?)) ");
            String pattern = "%" + query.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (categoryId != null) {
            sql.append("AND e.category_id = ? ");
            params.add(categoryId);
        }
        if (status != null && !status.equals("Все")) {
            sql.append("AND e.status = ? ");
            params.add(status);
        }
        if (minCost != null) {
            sql.append("AND e.cost >= ? ");
            params.add(minCost);
        }
        if (maxCost != null) {
            sql.append("AND e.cost <= ? ");
            params.add(maxCost);
        }
        sql.append("ORDER BY e.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске/фильтрации оборудования", e);
        }
        return result;
    }

    public List<Object[]> getCountByCategory() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT c.name, COUNT(e.id) AS cnt FROM categories c " +
                "LEFT JOIN equipment e ON e.category_id = c.id GROUP BY c.id, c.name ORDER BY cnt DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{rs.getString("name"), rs.getInt("cnt")});
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сборе статистики по категориям", e);
        }
        return data;
    }

    public List<Object[]> getCountByStatus() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) AS cnt FROM equipment GROUP BY status ORDER BY cnt DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{rs.getString("status"), rs.getInt("cnt")});
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сборе статистики по статусам", e);
        }
        return data;
    }

    public BigDecimal getTotalCost() {
        String sql = "SELECT COALESCE(SUM(cost), 0) AS total FROM equipment";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при подсчете общей стоимости", e);
        }
        return BigDecimal.ZERO;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM equipment";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при подсчете количества оборудования", e);
        }
        return 0;
    }

    private void fillStatement(Equipment equipment, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, equipment.getInventoryNumber());
        pstmt.setString(2, equipment.getName());
        pstmt.setInt(3, equipment.getCategoryId());
        pstmt.setString(4, equipment.getStatus());
        pstmt.setString(5, equipment.getLocation());
        if (equipment.getPurchaseDate() != null) {
            pstmt.setDate(6, Date.valueOf(equipment.getPurchaseDate()));
        } else {
            pstmt.setNull(6, Types.DATE);
        }
        pstmt.setBigDecimal(7, equipment.getCost());
    }

    private String baseSelect() {
        return "SELECT e.id, e.inventory_number, e.name, e.category_id, c.name AS category_name, e.status, e.location, " +
                "e.purchase_date, e.cost, e.created_at FROM equipment e " +
                "LEFT JOIN categories c ON c.id = e.category_id";
    }

    private Equipment mapResultSet(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setId(rs.getInt("id"));
        e.setInventoryNumber(rs.getString("inventory_number"));
        e.setName(rs.getString("name"));
        e.setCategoryId(rs.getInt("category_id"));
        e.setCategoryName(rs.getString("category_name"));
        e.setStatus(rs.getString("status"));
        e.setLocation(rs.getString("location"));
        Date purchaseDate = rs.getDate("purchase_date");
        if (purchaseDate != null) {
            e.setPurchaseDate(purchaseDate.toLocalDate());
        }
        e.setCost(rs.getBigDecimal("cost"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            e.setCreatedAt(ts.toLocalDateTime());
        }
        return e;
    }
}
