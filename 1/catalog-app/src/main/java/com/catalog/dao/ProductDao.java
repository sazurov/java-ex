package com.catalog.dao;

import com.catalog.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    public List<Product> search(String query, Integer categoryId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT p.id, p.name, p.description, p.price, p.stock, p.category_id, c.name AS category_name
            FROM products p
            LEFT JOIN categories c ON c.id = p.category_id
            WHERE 1 = 1
            """);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(COALESCE(p.description, '')) LIKE ?)");
            String q = "%" + query.toLowerCase() + "%";
            params.add(q);
            params.add(q);
        }
        if (categoryId != null) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        sql.append(" ORDER BY p.id DESC");

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            List<Product> products = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setStock(rs.getInt("stock"));
                    p.setCategoryId((Integer) rs.getObject("category_id"));
                    p.setCategoryName(rs.getString("category_name"));
                    products.add(p);
                }
            }
            return products;
        }
    }

    public void create(Product product) throws SQLException {
        String sql = "INSERT INTO products(name, description, price, stock, category_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fillProductStatement(product, ps);
            ps.executeUpdate();
        }
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ?, category_id = ? WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fillProductStatement(product, ps);
            ps.setInt(6, product.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void fillProductStatement(Product product, PreparedStatement ps) throws SQLException {
        ps.setString(1, product.getName());
        ps.setString(2, product.getDescription());
        ps.setBigDecimal(3, product.getPrice());
        ps.setInt(4, product.getStock());
        if (product.getCategoryId() == null) {
            ps.setNull(5, Types.INTEGER);
        } else {
            ps.setInt(5, product.getCategoryId());
        }
    }
}
