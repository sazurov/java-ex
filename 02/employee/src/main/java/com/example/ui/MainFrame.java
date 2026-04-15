package com.example.ui;

import com.example.database.DatabaseConnection;
import com.example.ui.panel.AnalyticsPanel;
import com.example.ui.panel.DepartmentPanel;
import com.example.ui.panel.EmployeePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно приложения
 */
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    
    private JTabbedPane tabbedPane;

    public MainFrame() {
        initComponents();
        initializeDatabase();
    }

    private void initComponents() {
        setTitle("Система учета сотрудников и отделов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Вкладки
        tabbedPane = new JTabbedPane();
        
        // Вкладка "Сотрудники"
        tabbedPane.addTab("👥 Сотрудники", new EmployeePanel());
        
        // Вкладка "Отделы"
        tabbedPane.addTab("🏢 Отделы", new DepartmentPanel());
        
        // Вкладка "Аналитика"
        tabbedPane.addTab("📊 Аналитика", new AnalyticsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Строка меню
        createMenuBar();
        
        // Строка состояния
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.add(new JLabel("Добро пожаловать в систему учета сотрудников"));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Меню "Справка"
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
            "Система учета сотрудников и отделов\n" +
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
