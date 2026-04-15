package com.example.ui.panel;

import com.example.dao.DepartmentDAO;
import com.example.model.Department;
import com.example.ui.dialog.DepartmentDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Панель для управления отделами
 */
public class DepartmentPanel extends JPanel {
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private DefaultTableModel tableModel;
    private JTable departmentTable;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;

    public DepartmentPanel() {
        initComponents();
        loadDepartments();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Верхняя панель с поиском и кнопками
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // Поиск
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Поиск:"), BorderLayout.WEST);
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JButton searchButton = new JButton("Поиск");
        searchButton.addActionListener(e -> searchDepartments());
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Кнопки действия
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("+ Добавить");
        editButton = new JButton("✎ Редактировать");
        deleteButton = new JButton("✗ Удалить");
        refreshButton = new JButton("↺ Обновить");
        
        addButton.addActionListener(e -> addDepartment());
        editButton.addActionListener(e -> editDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        refreshButton.addActionListener(e -> loadDepartments());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Таблица
        String[] columnNames = {"ID", "Название", "Описание", "Количество сотрудников"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(departmentTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDepartments() {
        tableModel.setRowCount(0);
        List<Department> departments = departmentDAO.getAll();
        
        for (Department dept : departments) {
            int employeeCount = departmentDAO.getEmployeeCount(dept.getId());
            tableModel.addRow(new Object[]{
                dept.getId(),
                dept.getName(),
                dept.getDescription(),
                employeeCount
            });
        }
    }

    private void searchDepartments() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadDepartments();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Department> departments = departmentDAO.search(query);
        
        for (Department dept : departments) {
            int employeeCount = departmentDAO.getEmployeeCount(dept.getId());
            tableModel.addRow(new Object[]{
                dept.getId(),
                dept.getName(),
                dept.getDescription(),
                employeeCount
            });
        }
    }

    private void addDepartment() {
        Department newDept = new Department();
        DepartmentDialog dialog = new DepartmentDialog(
            SwingUtilities.getWindowAncestor(this),
            "Добавить отдел",
            newDept
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            if (departmentDAO.create(dialog.getDepartment())) {
                JOptionPane.showMessageDialog(this, "Отдел успешно добавлен");
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении отдела", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите отдел для редактирования");
            return;
        }
        
        int deptId = (int) tableModel.getValueAt(selectedRow, 0);
        Department dept = departmentDAO.getById(deptId);
        
        if (dept == null) {
            JOptionPane.showMessageDialog(this, "Не удалось загрузить отдел", 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DepartmentDialog dialog = new DepartmentDialog(
            SwingUtilities.getWindowAncestor(this),
            "Редактировать отдел",
            dept
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            if (departmentDAO.update(dialog.getDepartment())) {
                JOptionPane.showMessageDialog(this, "Отдел успешно обновлен");
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении отдела", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите отдел для удаления");
            return;
        }
        
        int deptId = (int) tableModel.getValueAt(selectedRow, 0);
        String deptName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Вы уверены, что хотите удалить отдел '" + deptName + "'?\n" +
            "Все сотрудники этого отдела также будут удалены.",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (departmentDAO.delete(deptId)) {
                JOptionPane.showMessageDialog(this, "Отдел успешно удален");
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении отдела", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
