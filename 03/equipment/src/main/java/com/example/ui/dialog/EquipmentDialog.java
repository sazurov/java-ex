package com.example.ui.dialog;

import com.example.dao.CategoryDAO;
import com.example.model.Category;
import com.example.model.Equipment;
import com.example.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class EquipmentDialog extends JDialog {
    private final JTextField inventoryField = new JTextField(20);
    private final JTextField nameField = new JTextField(25);
    private final JComboBox<Category> categoryCombo = new JComboBox<>();
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "В эксплуатации", "На складе", "На ремонте", "Списано"
    });
    private final JTextField locationField = new JTextField(25);
    private final JTextField costField = new JTextField(15);
    private final JSpinner purchaseDateSpinner = new JSpinner(new SpinnerDateModel());

    private final Equipment equipment;
    private boolean saved = false;
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public EquipmentDialog(Frame parent, String title, Equipment equipment) {
        super(parent, title, true);
        this.equipment = equipment;
        initComponents();
        fillCategories();
        fillValues();
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Инв. номер:"), gbc);
        gbc.gridx = 1;
        form.add(inventoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Категория:"), gbc);
        gbc.gridx = 1;
        form.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        form.add(statusCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(new JLabel("Локация:"), gbc);
        gbc.gridx = 1;
        form.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        form.add(new JLabel("Дата покупки:"), gbc);
        gbc.gridx = 1;
        JSpinner.DateEditor editor = new JSpinner.DateEditor(purchaseDateSpinner, "dd.MM.yyyy");
        purchaseDateSpinner.setEditor(editor);
        form.add(purchaseDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        form.add(new JLabel("Стоимость:"), gbc);
        gbc.gridx = 1;
        form.add(costField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());
        buttons.add(saveButton);
        buttons.add(cancelButton);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getParent());
    }

    private void fillCategories() {
        List<Category> categories = categoryDAO.getAll();
        for (Category category : categories) {
            categoryCombo.addItem(category);
        }
    }

    private void fillValues() {
        if (equipment.getId() > 0) {
            inventoryField.setText(equipment.getInventoryNumber());
            nameField.setText(equipment.getName());
            locationField.setText(equipment.getLocation() == null ? "" : equipment.getLocation());
            costField.setText(equipment.getCost() == null ? "0" : equipment.getCost().toPlainString());
            if (equipment.getPurchaseDate() != null) {
                purchaseDateSpinner.setValue(java.sql.Date.valueOf(equipment.getPurchaseDate()));
            }
            statusCombo.setSelectedItem(equipment.getStatus());
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).getId() == equipment.getCategoryId()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            purchaseDateSpinner.setValue(new Date());
            costField.setText("0");
        }
    }

    private void onSave() {
        ValidationUtil.ValidationResult result;
        result = ValidationUtil.validateInventoryNumber(inventoryField.getText());
        if (!result.valid) {
            showValidationError(result.message);
            return;
        }

        result = ValidationUtil.validateEquipmentName(nameField.getText());
        if (!result.valid) {
            showValidationError(result.message);
            return;
        }

        Category selectedCategory = (Category) categoryCombo.getSelectedItem();
        if (selectedCategory == null) {
            showValidationError("Выберите категорию");
            return;
        }

        BigDecimal cost;
        try {
            cost = new BigDecimal(costField.getText().trim());
        } catch (NumberFormatException e) {
            showValidationError("Стоимость должна быть числом");
            return;
        }
        result = ValidationUtil.validateCost(cost);
        if (!result.valid) {
            showValidationError(result.message);
            return;
        }

        Date selectedDate = (Date) purchaseDateSpinner.getValue();
        LocalDate purchaseDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        result = ValidationUtil.validatePurchaseDate(purchaseDate);
        if (!result.valid) {
            showValidationError(result.message);
            return;
        }

        result = ValidationUtil.validateLocation(locationField.getText());
        if (!result.valid) {
            showValidationError(result.message);
            return;
        }

        equipment.setInventoryNumber(inventoryField.getText().trim());
        equipment.setName(nameField.getText().trim());
        equipment.setCategoryId(selectedCategory.getId());
        equipment.setStatus((String) statusCombo.getSelectedItem());
        equipment.setLocation(locationField.getText().trim());
        equipment.setPurchaseDate(purchaseDate);
        equipment.setCost(cost);
        saved = true;
        dispose();
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isSaved() {
        return saved;
    }

    public Equipment getEquipment() {
        return equipment;
    }
}
