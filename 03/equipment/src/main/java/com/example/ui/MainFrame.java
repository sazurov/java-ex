package com.example.ui;

import com.example.database.DatabaseConnection;
import com.example.ui.panel.AnalyticsPanel;
import com.example.ui.panel.CategoryPanel;
import com.example.ui.panel.EquipmentPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

    private JTabbedPane tabbedPane;
    private AnalyticsPanel analyticsPanel;

    public MainFrame() {
        initializeDatabase();
        initComponents();
    }

    private void initComponents() {
        setTitle("Система учета оборудования");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("🧰 Оборудование", new EquipmentPanel());
        tabbedPane.addTab("📁 Категории", new CategoryPanel());
        analyticsPanel = new AnalyticsPanel();
        tabbedPane.addTab("📊 Статистика", analyticsPanel);
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == analyticsPanel) {
                analyticsPanel.refreshData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        createMenuBar();

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.add(new JLabel("Добро пожаловать в систему учета оборудования"));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "Система учета оборудования\n" +
            "Версия 1.0.0\n\n" +
            "Технологии:\n" +
            "- Java Swing\n" +
            "- PostgreSQL\n" +
            "- JFreeChart\n\n" +
            "Разработано с использованием Maven",
            "О программе",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void initializeDatabase() {
        try {
            DatabaseConnection.initializeDatabase();
            logger.info("База данных инициализирована успешно");
        } catch (Exception e) {
            logger.error("Ошибка при инициализации базы данных", e);
            JOptionPane.showMessageDialog(this,
                "Ошибка при подключении к базе данных:\n" + e.getMessage(),
                "Ошибка базы данных",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
