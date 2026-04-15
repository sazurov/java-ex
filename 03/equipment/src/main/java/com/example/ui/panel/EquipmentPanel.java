package com.example.ui.panel;

import com.example.dao.CategoryDAO;
import com.example.dao.EquipmentDAO;
import com.example.model.Category;
import com.example.model.Equipment;
import com.example.ui.dialog.EquipmentDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private final JTextField searchField = new JTextField();
    private final JComboBox<Object> categoryFilter = new JComboBox<>();
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "Все", "В эксплуатации", "На складе", "На ремонте", "Списано"
    });
    private final JTextField minCostField = new JTextField("0", 8);
    private final JTextField maxCostField = new JTextField("999999999", 8);

    private final DefaultTableModel tableModel;
    private final JTable table;

    public EquipmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Поиск:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Поиск");
        searchButton.addActionListener(e -> applyFilters());
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.add(new JLabel("Категория:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(new JLabel("Статус:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Стоимость:"));
        filterPanel.add(minCostField);
        filterPanel.add(new JLabel("-"));
        filterPanel.add(maxCostField);
        JButton filterButton = new JButton("Фильтр");
        filterButton.addActionListener(e -> applyFilters());
        filterPanel.add(filterButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = new JButton("+ Добавить");
        JButton editButton = new JButton("✎ Редактировать");
        JButton deleteButton = new JButton("✗ Удалить");
        JButton refreshButton = new JButton("↺ Обновить");
        addButton.addActionListener(e -> addEquipment());
        editButton.addActionListener(e -> editEquipment());
        deleteButton.addActionListener(e -> deleteEquipment());
        refreshButton.addActionListener(e -> loadEquipment());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel centerTop = new JPanel(new BorderLayout(5, 5));
        centerTop.add(filterPanel, BorderLayout.CENTER);
        centerTop.add(buttonPanel, BorderLayout.SOUTH);

        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(centerTop, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Инв. номер", "Название", "Категория", "Статус", "Локация", "Дата покупки", "Стоимость"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        fillCategoryFilter();
        loadEquipment();
    }

    private void fillCategoryFilter() {
        categoryFilter.removeAllItems();
        categoryFilter.addItem("Все");
        for (Category category : categoryDAO.getAll()) {
            categoryFilter.addItem(category);
        }
    }

    private void loadEquipment() {
        fillCategoryFilter();
        fillTable(equipmentDAO.getAll());
    }

    private void applyFilters() {
        BigDecimal minCost;
        BigDecimal maxCost;
        try {
            minCost = new BigDecimal(minCostField.getText().trim());
            maxCost = new BigDecimal(maxCostField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректный диапазон стоимости", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object selectedCategory = categoryFilter.getSelectedItem();
        Integer categoryId = selectedCategory instanceof Category ? ((Category) selectedCategory).getId() : null;
        String status = (String) statusFilter.getSelectedItem();

        fillTable(equipmentDAO.searchAndFilter(searchField.getText(), categoryId, status, minCost, maxCost));
    }

    private void fillTable(List<Equipment> list) {
        tableModel.setRowCount(0);
        for (Equipment e : list) {
            LocalDate purchaseDate = e.getPurchaseDate();
            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getInventoryNumber(),
                    e.getName(),
                    e.getCategoryName(),
                    e.getStatus(),
                    e.getLocation(),
                    purchaseDate == null ? "" : purchaseDate,
                    e.getCost()
            });
        }
    }

    private void addEquipment() {
        if (categoryDAO.getAll().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Сначала создайте хотя бы одну категорию");
            return;
        }
        Equipment equipment = new Equipment();
        EquipmentDialog dialog = new EquipmentDialog(JOptionPane.getFrameForComponent(this), "Добавить оборудование", equipment);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            if (equipmentDAO.create(dialog.getEquipment())) {
                loadEquipment();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Не удалось добавить оборудование. Проверьте уникальность инвентарного номера.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editEquipment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Выберите оборудование");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Equipment equipment = equipmentDAO.getById(id);
        if (equipment == null) {
            JOptionPane.showMessageDialog(this, "Оборудование не найдено", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EquipmentDialog dialog = new EquipmentDialog(JOptionPane.getFrameForComponent(this), "Редактировать оборудование", equipment);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            if (equipmentDAO.update(dialog.getEquipment())) {
                loadEquipment();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось обновить оборудование", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEquipment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Выберите оборудование");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String inventoryNumber = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить оборудование с номером '" + inventoryNumber + "'?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (equipmentDAO.delete(id)) {
                loadEquipment();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось удалить оборудование", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
