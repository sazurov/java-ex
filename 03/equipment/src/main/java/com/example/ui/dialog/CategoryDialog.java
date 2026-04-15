package com.example.ui.dialog;

import com.example.model.Category;
import com.example.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

public class CategoryDialog extends JDialog {
    private final JTextField nameField = new JTextField(30);
    private final JTextArea descriptionArea = new JTextArea(5, 30);
    private boolean saved = false;
    private final Category category;

    public CategoryDialog(Frame parent, String title, Category category) {
        super(parent, title, true);
        this.category = category;
        initComponents();
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
        form.add(new JLabel("Название:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        form.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        form.add(new JScrollPane(descriptionArea), gbc);

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

    private void fillValues() {
        if (category.getId() > 0) {
            nameField.setText(category.getName());
            descriptionArea.setText(category.getDescription() == null ? "" : category.getDescription());
        }
    }

    private void onSave() {
        ValidationUtil.ValidationResult result = ValidationUtil.validateCategoryName(nameField.getText());
        if (!result.valid) {
            JOptionPane.showMessageDialog(this, result.message, "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return;
        }
        category.setName(nameField.getText().trim());
        category.setDescription(descriptionArea.getText().trim());
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Category getCategory() {
        return category;
    }
}
