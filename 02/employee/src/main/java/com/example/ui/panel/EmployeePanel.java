package com.example.ui.panel;

import com.example.dao.DepartmentDAO;
import com.example.dao.EmployeeDAO;
import com.example.model.Department;
import com.example.model.Employee;
import com.example.ui.dialog.EmployeeDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Панель для управления сотрудниками
 */
public class EmployeePanel extends JPanel {
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    
    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private JTextField minSalaryField;
    private JTextField maxSalaryField;
    private JButton addButton, editButton, deleteButton, refreshButton;

    public EmployeePanel() {
        initComponents();
        loadEmployees();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Верхняя панель с фильтрами
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // Панель поиска
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Поиск:"), BorderLayout.WEST);
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JButton searchButton = new JButton("Поиск");
        searchButton.addActionListener(e -> searchEmployees());
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Панель фильтров
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        filterPanel.add(new JLabel("Отдел:"));
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("Все");
        populateDepartmentFilter();
        departmentFilter.addActionListener(e -> applyFilters());
        filterPanel.add(departmentFilter);
        
        filterPanel.add(new JLabel("Зарплата:"));
        minSalaryField = new JTextField("0", 8);
        filterPanel.add(minSalaryField);
        
        filterPanel.add(new JLabel("-"));
        maxSalaryField = new JTextField("999999", 8);
        filterPanel.add(maxSalaryField);
        
        JButton filterButton = new JButton("Фильтр");
        filterButton.addActionListener(e -> applyFilters());
        filterPanel.add(filterButton);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Панель кнопок действия
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("+ Добавить");
        editButton = new JButton("✎ Редактировать");
        deleteButton = new JButton("✗ Удалить");
        refreshButton = new JButton("↺ Обновить");
        
        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        refreshButton.addActionListener(e -> loadEmployees());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Объединяем фильтры и кнопки
        JPanel filterAndButtonPanel = new JPanel(new BorderLayout(5, 5));
        filterAndButtonPanel.add(filterPanel, BorderLayout.NORTH);
        filterAndButtonPanel.add(buttonPanel, BorderLayout.SOUTH);
        topPanel.add(filterAndButtonPanel, BorderLayout.SOUTH);
        
        // Таблица
        String[] columnNames = {"ID", "Имя", "Фамилия", "Email", "Телефон", 
                               "Зарплата", "Должность", "Отдел", "Дата приема"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateDepartmentFilter() {
        List<Department> departments = departmentDAO.getAll();
        for (Department dept : departments) {
            departmentFilter.addItem(dept.getName());
        }
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAll();
        
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                emp.getId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getPhone(),
                emp.getSalary(),
                emp.getPosition(),
                emp.getDepartmentName(),
                emp.getHireDate()
            });
        }
    }

    private void searchEmployees() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadEmployees();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.search(query);
        
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                emp.getId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getPhone(),
                emp.getSalary(),
                emp.getPosition(),
                emp.getDepartmentName(),
                emp.getHireDate()
            });
        }
    }

    private void applyFilters() {
        String selectedDept = (String) departmentFilter.getSelectedItem();
        
        try {
            double minSalary = Double.parseDouble(minSalaryField.getText());
            double maxSalary = Double.parseDouble(maxSalaryField.getText());
            
            tableModel.setRowCount(0);
            
            // Получаем всех сотрудников и фильтруем
            List<Employee> employees;
            
            if ("Все".equals(selectedDept)) {
                employees = employeeDAO.filterBySalary(minSalary, maxSalary);
            } else {
                // Находим отдел по названию
                List<Department> allDepts = departmentDAO.getAll();
                Department selectedDepartment = null;
                
                for (Department dept : allDepts) {
                    if (dept.getName().equals(selectedDept)) {
                        selectedDepartment = dept;
                        break;
                    }
                }
                
                if (selectedDepartment != null) {
                    employees = employeeDAO.getByDepartment(selectedDepartment.getId());
                    // Фильтруем по зарплате
                    employees.removeIf(e -> e.getSalary().doubleValue() < minSalary || 
                                           e.getSalary().doubleValue() > maxSalary);
                } else {
                    employees = employeeDAO.filterBySalary(minSalary, maxSalary);
                }
            }
            
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getPhone(),
                    emp.getSalary(),
                    emp.getPosition(),
                    emp.getDepartmentName(),
                    emp.getHireDate()
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введите корректные значения зарплаты", 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEmployee() {
        Employee newEmp = new Employee();
        EmployeeDialog dialog = new EmployeeDialog(
            SwingUtilities.getWindowAncestor(this),
            "Добавить сотрудника",
            newEmp
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            if (employeeDAO.create(dialog.getEmployee())) {
                JOptionPane.showMessageDialog(this, "Сотрудник успешно добавлен");
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении сотрудника", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите сотрудника для редактирования");
            return;
        }
        
        int empId = (int) tableModel.getValueAt(selectedRow, 0);
        Employee emp = employeeDAO.getById(empId);
        
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Не удалось загрузить сотрудника", 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        EmployeeDialog dialog = new EmployeeDialog(
            SwingUtilities.getWindowAncestor(this),
            "Редактировать сотрудника",
            emp
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            if (employeeDAO.update(dialog.getEmployee())) {
                JOptionPane.showMessageDialog(this, "Сотрудник успешно обновлен");
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении сотрудника", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите сотрудника для удаления");
            return;
        }
        
        int empId = (int) tableModel.getValueAt(selectedRow, 0);
        String empName = (String) tableModel.getValueAt(selectedRow, 1) + " " + 
                        (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Вы уверены, что хотите удалить сотрудника '" + empName + "'?",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (employeeDAO.delete(empId)) {
                JOptionPane.showMessageDialog(this, "Сотрудник успешно удален");
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении сотрудника", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
