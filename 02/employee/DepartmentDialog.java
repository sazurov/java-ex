package com.example.ui.dialog;

import com.example.model.Department;
import com.example.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Диалог для добавления/редактирования отдела
 */
public class DepartmentDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    
    private Department department;
    private boolean saved = false;

    public DepartmentDialog(Frame parent, String title, Department dept) {
        super(parent, title, true);
        this.department = dept;
        
        initComponents();
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
        
        // Название отдела
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Название:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(25);
        mainPanel.add(nameField, gbc);
        
        // Описание
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Описание:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        mainPanel.add(new JScrollPane(descriptionArea), gbc);
        
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

    private void populateFields() {
        if (department != null && department.getId() > 0) {
            nameField.setText(department.getName());
            descriptionArea.setText(department.getDescription() != null ? department.getDescription() : "");
        }
    }

    private void saveData() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        // Валидация
        ValidationUtil.ValidationResult nameValidation = ValidationUtil.validateDepartmentName(name);
        if (!nameValidation.valid) {
            JOptionPane.showMessageDialog(this, nameValidation.message, 
                "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Обновляем объект
        department.setName(name);
        department.setDescription(description);
        
        saved = true;
        dispose();
    }

    public Department getDepartment() {
        return department;
    }

    public boolean isSaved() {
        return saved;
    }
}
