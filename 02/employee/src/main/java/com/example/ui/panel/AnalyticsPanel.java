package com.example.ui.panel;

import com.example.dao.EmployeeDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Панель с аналитикой и графиками
 */
public class AnalyticsPanel extends JPanel {
    private EmployeeDAO employeeDAO = new EmployeeDAO();

    public AnalyticsPanel() {
        refreshData();
    }

    private void initComponents() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // График 1: Распределение сотрудников по отделам (круговая диаграмма)
        add(createDepartmentPieChart());
        
        // График 2: Зарплаты по отделам (столбчатая диаграмма)
        add(createSalaryBarChart());
        
        // График 3: Средняя зарплата по отделам
        add(createAverageSalaryChart());
        
        // График 4: Статистика
        add(createStatisticsPanel());
    }

    /**
     * Пересобрать аналитику из актуальных данных БД.
     */
    public void refreshData() {
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }

    private ChartPanel createDepartmentPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        List<Object[]> data = employeeDAO.getSalaryByDepartment();
        for (Object[] row : data) {
            String deptName = (String) row[0];
            int count = (Integer) row[1];
            if (count > 0) {
                dataset.setValue(deptName, count);
            }
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Распределение сотрудников по отделам",
            dataset,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }

    private ChartPanel createSalaryBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        List<Object[]> data = employeeDAO.getSalaryByDepartment();
        for (Object[] row : data) {
            String deptName = (String) row[0];
            BigDecimal totalSalary = (BigDecimal) row[3];
            if (totalSalary != null) {
                dataset.addValue(totalSalary, "Общая зарплата", deptName);
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Общая зарплата по отделам",
            "Отдел",
            "Сумма зарплаты",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }

    private ChartPanel createAverageSalaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        List<Object[]> data = employeeDAO.getSalaryByDepartment();
        for (Object[] row : data) {
            String deptName = (String) row[0];
            BigDecimal avgSalary = (BigDecimal) row[2];
            if (avgSalary != null) {
                dataset.addValue(avgSalary, "Средняя зарплата", deptName);
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Средняя зарплата по отделам",
            "Отдел",
            "Средняя зарплата",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Статистика"));
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== СТАТИСТИКА ===\n\n");
        
        List<Object[]> data = employeeDAO.getSalaryByDepartment();
        
        int totalEmployees = 0;
        BigDecimal totalSalary = BigDecimal.ZERO;
        BigDecimal maxSalary = BigDecimal.ZERO;
        BigDecimal minSalary = null;
        
        for (Object[] row : data) {
            String deptName = (String) row[0];
            int count = (Integer) row[1];
            BigDecimal avgSalary = (BigDecimal) row[2];
            BigDecimal sumSalary = (BigDecimal) row[3];
            
            totalEmployees += count;
            if (sumSalary != null) {
                totalSalary = totalSalary.add(sumSalary);
            }
            
            stats.append(String.format("Отдел: %s\n", deptName));
            stats.append(String.format("  Сотрудников: %d\n", count));
            stats.append(String.format("  Средняя зарплата: %.2f\n", 
                avgSalary != null ? avgSalary : BigDecimal.ZERO));
            stats.append("\n");
        }
        
        stats.append("\n=== ИТОГО ===\n");
        stats.append(String.format("Всего сотрудников: %d\n", totalEmployees));
        stats.append(String.format("Общая зарплата: %.2f\n", totalSalary));
        if (totalEmployees > 0) {
            BigDecimal avgTotal = totalSalary.divide(new BigDecimal(totalEmployees), 2, BigDecimal.ROUND_HALF_UP);
            stats.append(String.format("Средняя зарплата: %.2f\n", avgTotal));
        }
        
        statsArea.setText(stats.toString());
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        
        return panel;
    }
}
