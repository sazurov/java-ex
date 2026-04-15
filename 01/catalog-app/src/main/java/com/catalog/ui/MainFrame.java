package com.catalog.ui;

import com.catalog.dao.CategoryDao;
import com.catalog.dao.ProductDao;
import com.catalog.model.Category;
import com.catalog.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {

    private final ProductDao  productDao  = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();

    // --- Товары ---
    private DefaultTableModel productTableModel;
    private JTable            productTable;
    private JTextField        searchField;
    private JComboBox<Object> categoryFilter;

    // --- Категории ---
    private DefaultTableModel categoryTableModel;
    private JTable            categoryTable;

    public MainFrame() {
        setTitle("Каталог товаров");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Товары",    buildProductsPanel());
        tabs.addTab("Категории", buildCategoriesPanel());
        add(tabs);

        loadCategories();
        loadProducts();
    }

    // ===================== ТОВАРЫ =====================

    private JPanel buildProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Панель поиска/фильтрации
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchField    = new JTextField(20);
        categoryFilter = new JComboBox<>();
        JButton searchBtn = new JButton("Найти");
        JButton resetBtn  = new JButton("Сброс");

        top.add(new JLabel("Поиск:"));
        top.add(searchField);
        top.add(new JLabel("Категория:"));
        top.add(categoryFilter);
        top.add(searchBtn);
        top.add(resetBtn);

        searchBtn.addActionListener(e -> loadProducts());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            loadProducts();
        });

        // Таблица товаров
        productTableModel = new DefaultTableModel(
            new String[]{"ID", "Название", "Описание", "Цена", "Остаток", "Категория"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getColumnModel().getColumn(0).setMaxWidth(50);

        // Кнопки CRUD
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton addBtn    = new JButton("Добавить");
        JButton editBtn   = new JButton("Редактировать");
        JButton deleteBtn = new JButton("Удалить");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);

        addBtn.addActionListener(e    -> showProductDialog(null));
        editBtn.addActionListener(e   -> {
            int row = productTable.getSelectedRow();
            if (row < 0) { showInfo("Выберите товар."); return; }
            int id = (int) productTableModel.getValueAt(row, 0);
            // найдём объект из текущего списка
            showProductDialog(getProductFromTable(row));
        });
        deleteBtn.addActionListener(e -> deleteSelectedProduct());

        panel.add(top,                           BorderLayout.NORTH);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(bottom,                        BorderLayout.SOUTH);
        return panel;
    }

    private void loadProducts() {
        try {
            String  query      = searchField.getText().trim();
            Integer categoryId = null;
            Object  selected   = categoryFilter.getSelectedItem();
            if (selected instanceof Category) categoryId = ((Category) selected).getId();

            List<Product> products = productDao.search(
                query.isEmpty() ? null : query, categoryId);

            productTableModel.setRowCount(0);
            for (Product p : products) {
                productTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getDescription(),
                    p.getPrice(), p.getStock(), p.getCategoryName()
                });
            }
        } catch (SQLException ex) {
            showError("Ошибка загрузки товаров: " + ex.getMessage());
        }
    }

    private Product getProductFromTable(int row) {
        Product p = new Product();
        p.setId((int) productTableModel.getValueAt(row, 0));
        p.setName((String) productTableModel.getValueAt(row, 1));
        p.setDescription((String) productTableModel.getValueAt(row, 2));
        p.setPrice((BigDecimal) productTableModel.getValueAt(row, 3));
        p.setStock((int) productTableModel.getValueAt(row, 4));
        p.setCategoryName((String) productTableModel.getValueAt(row, 5));
        return p;
    }

    private void showProductDialog(Product existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Редактировать товар" : "Добавить товар", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JTextField nameField  = new JTextField(existing != null ? existing.getName() : "", 25);
        JTextArea  descArea   = new JTextArea(existing != null ? existing.getDescription() : "", 3, 25);
        JTextField priceField = new JTextField(existing != null ? existing.getPrice().toPlainString() : "0.00", 25);
        JTextField stockField = new JTextField(existing != null ? String.valueOf(existing.getStock()) : "0", 25);

        JComboBox<Object> catBox = new JComboBox<>();
        catBox.addItem("— Без категории —");
        try {
            for (Category c : categoryDao.findAll()) catBox.addItem(c);
        } catch (SQLException ex) { showError(ex.getMessage()); }

        // Пре-выбрать категорию при редактировании
        if (isEdit && existing.getCategoryName() != null) {
            for (int i = 1; i < catBox.getItemCount(); i++) {
                Category c = (Category) catBox.getItemAt(i);
                if (c.getName().equals(existing.getCategoryName())) {
                    catBox.setSelectedIndex(i); break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Название:"));   form.add(nameField);
        form.add(new JLabel("Описание:"));   form.add(new JScrollPane(descArea));
        form.add(new JLabel("Цена:"));       form.add(priceField);
        form.add(new JLabel("Остаток:"));    form.add(stockField);
        form.add(new JLabel("Категория:"));  form.add(catBox);

        JButton saveBtn   = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            // Валидация
            String name = nameField.getText().trim();
            if (name.isEmpty()) { showInfo("Введите название."); return; }

            BigDecimal price;
            int stock;
            try {
                price = new BigDecimal(priceField.getText().trim());
                if (price.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showInfo("Цена должна быть числом >= 0."); return;
            }
            try {
                stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showInfo("Остаток должен быть целым числом >= 0."); return;
            }

            Integer catId = null;
            Object  sel   = catBox.getSelectedItem();
            if (sel instanceof Category) catId = ((Category) sel).getId();

            Product p = isEdit ? existing : new Product();
            p.setName(name);
            p.setDescription(descArea.getText().trim());
            p.setPrice(price);
            p.setStock(stock);
            p.setCategoryId(catId);

            try {
                if (isEdit) productDao.update(p);
                else        productDao.create(p);
                loadProducts();
                dialog.dispose();
            } catch (SQLException ex) {
                showError("Ошибка сохранения: " + ex.getMessage());
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) { showInfo("Выберите товар."); return; }
        int id   = (int) productTableModel.getValueAt(row, 0);
        String n = (String) productTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Удалить товар «" + n + "»?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            productDao.delete(id);
            loadProducts();
        } catch (SQLException ex) {
            showError("Ошибка удаления: " + ex.getMessage());
        }
    }

    // ===================== КАТЕГОРИИ =====================

    private JPanel buildCategoriesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        categoryTableModel = new DefaultTableModel(new String[]{"ID", "Название"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        categoryTable = new JTable(categoryTableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton addBtn    = new JButton("Добавить");
        JButton editBtn   = new JButton("Переименовать");
        JButton deleteBtn = new JButton("Удалить");
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);

        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Название категории:");
            if (name == null || name.isBlank()) return;
            try {
                categoryDao.create(name);
                loadCategories();
                loadProducts();
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });

        editBtn.addActionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row < 0) { showInfo("Выберите категорию."); return; }
            int    id      = (int) categoryTableModel.getValueAt(row, 0);
            String oldName = (String) categoryTableModel.getValueAt(row, 1);
            String newName = (String) JOptionPane.showInputDialog(
                this, "Новое название:", oldName);
            if (newName == null || newName.isBlank()) return;
            try {
                Category c = new Category(id, newName);
                categoryDao.update(c);
                loadCategories();
                loadProducts();
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });

        deleteBtn.addActionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row < 0) { showInfo("Выберите категорию."); return; }
            int    id   = (int) categoryTableModel.getValueAt(row, 0);
            String name = (String) categoryTableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить категорию «" + name + "»?\nТовары останутся без категории.",
                "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                categoryDao.delete(id);
                loadCategories();
                loadProducts();
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });

        panel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        panel.add(bottom,                         BorderLayout.SOUTH);
        return panel;
    }

    private void loadCategories() {
        try {
            List<Category> cats = categoryDao.findAll();

            categoryTableModel.setRowCount(0);
            for (Category c : cats) {
                categoryTableModel.addRow(new Object[]{c.getId(), c.getName()});
            }

            // Обновим фильтр на вкладке товаров
            Object prev = categoryFilter.getSelectedItem();
            categoryFilter.removeAllItems();
            categoryFilter.addItem("— Все категории —");
            for (Category c : cats) categoryFilter.addItem(c);
            if (prev instanceof Category) {
                for (int i = 1; i < categoryFilter.getItemCount(); i++) {
                    Category c = (Category) categoryFilter.getItemAt(i);
                    if (c.getId() == ((Category) prev).getId()) {
                        categoryFilter.setSelectedIndex(i); break;
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Ошибка загрузки категорий: " + ex.getMessage());
        }
    }

    // ===================== УТИЛИТЫ =====================

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Внимание", JOptionPane.INFORMATION_MESSAGE);
    }
}
