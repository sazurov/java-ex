package com.example;

import com.example.ui.MainFrame;

import javax.swing.*;

/**
 * Точка входа в приложение
 */
public class Main {
    public static void main(String[] args) {
        // Запускаем приложение в потоке Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Установить look and feel системы
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Создаем и показываем главное окно
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
