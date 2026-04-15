package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для управления сотрудниками
 */
public class EmployeeDAO {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDAO.class);

    /**
     * Создать нового сотрудника
     */
    public boolean create(Employee employee) {
        String sql = "INSERT INTO employees (first_name, last_name, email, phone, salary, " +
                    "hire_date, position, department_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPhone());
            pstmt.setBigDecimal(5, employee.getSalary());
            pstmt.setDate(6, java.sql.Date.valueOf(employee.getHireDate()));
            pstmt.setString(7, employee.getPosition());
            pstmt.setInt(8, employee.getDepartmentId());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    employee.setId(rs.getInt(1));
                    logger.info("Сотрудник успешно создан: {}", employee.getFullName());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании сотрудника", e);
        }
        return false;
    }

    /**
     * Получить всех сотрудников
     */
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.first_name, e.last_name, e.email, e.phone, e.salary, " +
                    "e.hire_date, e.position, e.department_id, d.name as department_name, " +
                    "e.created_at FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.id " +
                    "ORDER BY e.last_name, e.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee emp = mapResultSet(rs);
                employees.add(emp);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех сотрудников", e);
        }
        return employees;
    }

    /**
     * Получить сотрудника по ID
     */
    public Employee getById(int id) {
        String sql = "SELECT e.id, e.first_name, e.last_name, e.email, e.phone, e.salary, " +
                    "e.hire_date, e.position, e.department_id, d.name as department_name, " +
                    "e.created_at FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.id " +
                    "WHERE e.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении сотрудника по ID", e);
        }
        return null;
    }

    /**
     * Обновить сотрудника
     */
    public boolean update(Employee employee) {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, " +
                    "phone = ?, salary = ?, hire_date = ?, position = ?, department_id = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPhone());
            pstmt.setBigDecimal(5, employee.getSalary());
            pstmt.setDate(6, java.sql.Date.valueOf(employee.getHireDate()));
            pstmt.setString(7, employee.getPosition());
            pstmt.setInt(8, employee.getDepartmentId());
            pstmt.setInt(9, employee.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Сотрудник успешно обновлен: {}", employee.getFullName());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении сотрудника", e);
        }
        return false;
    }

    /**
     * Удалить сотрудника
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Сотрудник успешно удален: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении сотрудника", e);
        }
        return false;
    }

    /**
     * Получить сотрудников по отделу
     */
    public List<Employee> getByDepartment(int departmentId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.first_name, e.last_name, e.email, e.phone, e.salary, " +
                    "e.hire_date, e.position, e.department_id, d.name as department_name, " +
                    "e.created_at FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.id " +
                    "WHERE e.department_id = ? " +
                    "ORDER BY e.last_name, e.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, departmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении сотрудников по отделу", e);
        }
        return employees;
    }

    /**
     * Поиск сотрудников
     */
    public List<Employee> search(String query) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.first_name, e.last_name, e.email, e.phone, e.salary, " +
                    "e.hire_date, e.position, e.department_id, d.name as department_name, " +
                    "e.created_at FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.id " +
                    "WHERE LOWER(e.first_name) LIKE LOWER(?) OR LOWER(e.last_name) LIKE LOWER(?) " +
                    "OR LOWER(e.email) LIKE LOWER(?) OR LOWER(e.position) LIKE LOWER(?) " +
                    "ORDER BY e.last_name, e.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске сотрудников", e);
        }
        return employees;
    }

    /**
     * Фильтрация сотрудников по зарплате
     */
    public List<Employee> filterBySalary(double minSalary, double maxSalary) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.first_name, e.last_name, e.email, e.phone, e.salary, " +
                    "e.hire_date, e.position, e.department_id, d.name as department_name, " +
                    "e.created_at FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.id " +
                    "WHERE e.salary BETWEEN ? AND ? " +
                    "ORDER BY e.salary DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, new java.math.BigDecimal(minSalary));
            pstmt.setBigDecimal(2, new java.math.BigDecimal(maxSalary));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при фильтрации сотрудников по зарплате", e);
        }
        return employees;
    }

    /**
     * Проверить уникальность email
     */
    public boolean isEmailUnique(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE email = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверке уникальности email", e);
        }
        return true;
    }

    /**
     * Получить сводку по зарплатам по отделам
     */
    public List<Object[]> getSalaryByDepartment() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT d.name, COUNT(e.id) as count, " +
                    "AVG(e.salary) as avg_salary, SUM(e.salary) as total_salary " +
                    "FROM departments d " +
                    "LEFT JOIN employees e ON d.id = e.department_id " +
                    "GROUP BY d.id, d.name " +
                    "ORDER BY total_salary DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("name");
                row[1] = rs.getInt("count");
                row[2] = rs.getBigDecimal("avg_salary");
                row[3] = rs.getBigDecimal("total_salary");
                data.add(row);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении сводки по зарплатам", e);
        }
        return data;
    }

    private Employee mapResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getInt("id"));
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        emp.setSalary(rs.getBigDecimal("salary"));
        emp.setHireDate(rs.getDate("hire_date").toLocalDate());
        emp.setPosition(rs.getString("position"));
        emp.setDepartmentId(rs.getInt("department_id"));
        emp.setDepartmentName(rs.getString("department_name"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            emp.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return emp;
    }
}
