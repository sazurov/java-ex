package com.example.ui.panel;

import com.example.dao.CategoryDAO;
import com.example.model.Category;
import com.example.ui.dialog.CategoryDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryPanel extends JPanel {
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField();

    public CategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Поиск:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Поиск");
        searchButton.addActionListener(e -> searchCategories());
        searchPanel.add(searchButton, BorderLayout.EAST);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = new JButton("+ Добавить");
        JButton editButton = new JButton("✎ Редактировать");
        JButton deleteButton = new JButton("✗ Удалить");
        JButton refreshButton = new JButton("↺ Обновить");

        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        refreshButton.addActionListener(e -> loadCategories());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Название", "Описание", "Единиц оборудования"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadCategories();
    }

    private void loadCategories() {
        fillTable(categoryDAO.getAll());
    }

    private void searchCategories() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            loadCategories();
            return;
        }
        fillTable(categoryDAO.search(q));
    }

    private void fillTable(List<Category> categories) {
        tableModel.setRowCount(0);
        for (Category c : categories) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getDescription(),
                    categoryDAO.getEquipmentCount(c.getId())
            });
        }
    }

    private void addCategory() {
        Category category = new Category();
        CategoryDialog dialog = new CategoryDialog(JOptionPane.getFrameForComponent(this), "Добавить категорию", category);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            if (categoryDAO.create(dialog.getCategory())) {
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось добавить категорию. Возможно, такое имя уже есть.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Выберите категорию");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Category category = categoryDAO.getById(id);
        if (category == null) {
            JOptionPane.showMessageDialog(this, "Категория не найдена", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CategoryDialog dialog = new CategoryDialog(JOptionPane.getFrameForComponent(this), "Редактировать категорию", category);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            if (categoryDAO.update(dialog.getCategory())) {
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось обновить категорию", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Выберите категорию");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить категорию '" + name + "'?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (categoryDAO.delete(id)) {
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Не удалось удалить категорию. Убедитесь, что к ней не привязано оборудование.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
