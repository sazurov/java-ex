package com.example.ui.dialog;

import com.example.dao.DepartmentDAO;
import com.example.model.Department;
import com.example.model.Employee;
import com.example.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Диалог для добавления/редактирования сотрудника
 */
public class EmployeeDialog extends JDialog {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField salaryField;
    private JSpinner hireDateSpinner;
    private JTextField positionField;
    private JComboBox<Department> departmentCombo;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Employee employee;
    private boolean saved = false;
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public EmployeeDialog(Frame parent, String title, Employee emp) {
        super(parent, title, true);
        this.employee = emp;
        
        initComponents();
        populateDepartments();
        populateFields();
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Основная панель
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Первое имя
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Имя:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        firstNameField = new JTextField(15);
        mainPanel.add(firstNameField, gbc);
        
        // Фамилия
        gbc.gridx = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Фамилия:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        lastNameField = new JTextField(15);
        mainPanel.add(lastNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        emailField = new JTextField(30);
        mainPanel.add(emailField, gbc);
        
        // Телефон
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Телефон:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(15);
        mainPanel.add(phoneField, gbc);
        
        // Зарплата
        gbc.gridx = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Зарплата:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        salaryField = new JTextField(10);
        mainPanel.add(salaryField, gbc);
        
        // Дата приема
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Дата приема:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        hireDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(hireDateSpinner, "dd.MM.yyyy");
        hireDateSpinner.setEditor(dateEditor);
        mainPanel.add(hireDateSpinner, gbc);
        
        // Должность
        gbc.gridx = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Должность:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        positionField = new JTextField(15);
        mainPanel.add(positionField, gbc);
        
        // Отдел
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Отдел:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        departmentCombo = new JComboBox<>();
        mainPanel.add(departmentCombo, gbc);
        
        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        saveButton = new JButton("Сохранить");
        cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(e -> saveData());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }

    private void populateDepartments() {
        List<Department> departments = departmentDAO.getAll();
        for (Department dept : departments) {
            departmentCombo.addItem(dept);
        }
    }

    private void populateFields() {
        if (employee != null && employee.getId() > 0) {
            firstNameField.setText(employee.getFirstName());
            lastNameField.setText(employee.getLastName());
            emailField.setText(employee.getEmail());
            phoneField.setText(employee.getPhone() != null ? employee.getPhone() : "");
            salaryField.setText(employee.getSalary().toString());
            
            java.util.Date date = java.sql.Date.valueOf(employee.getHireDate());
            hireDateSpinner.setValue(date);
            
            positionField.setText(employee.getPosition());
            
            // Выбрать отдел
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                Department dept = (Department) departmentCombo.getItemAt(i);
                if (dept.getId() == employee.getDepartmentId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            // Установить текущую дату по умолчанию
            hireDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        }
    }

    private void saveData() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String salaryStr = salaryField.getText().trim();
        String position = positionField.getText().trim();
        Department selectedDept = (Department) departmentCombo.getSelectedItem();
        
        // Валидация
        ValidationUtil.ValidationResult validation;
        
        validation = ValidationUtil.validateName(firstName, "Имя");
        if (!validation.valid) {
            showError(validation.message);
            return;
        }
        
        validation = ValidationUtil.validateName(lastName, "Фамилия");
        if (!validation.valid) {
            showError(validation.message);
            return;
        }
        
        validation = ValidationUtil.validateEmail(email);
        if (!validation.valid) {
            showError(validation.message);
            return;
        }
        
        if (!phone.isEmpty()) {
            validation = ValidationUtil.validatePhone(phone);
            if (!validation.valid) {
                showError(validation.message);
                return;
            }
        }
        
        try {
            BigDecimal salary = new BigDecimal(salaryStr);
            validation = ValidationUtil.validateSalary(salary);
            if (!validation.valid) {
                showError(validation.message);
                return;
            }
        } catch (NumberFormatException e) {
            showError("Зарплата должна быть числом");
            return;
        }
        
        validation = ValidationUtil.validatePosition(position);
        if (!validation.valid) {
            showError(validation.message);
            return;
        }
        
        if (selectedDept == null) {
            showError("Выберите отдел");
            return;
        }
        
        java.util.Date selectedDate = (java.util.Date) hireDateSpinner.getValue();
        LocalDate hireDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        
        validation = ValidationUtil.validateHireDate(hireDate);
        if (!validation.valid) {
            showError(validation.message);
            return;
        }
        
        // Обновляем объект
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhone(phone);
        employee.setSalary(new BigDecimal(salaryStr));
        employee.setHireDate(hireDate);
        employee.setPosition(position);
        employee.setDepartmentId(selectedDept.getId());
        
        saved = true;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, 
            "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
    }

    public Employee getEmployee() {
        return employee;
    }

    public boolean isSaved() {
        return saved;
    }
}
