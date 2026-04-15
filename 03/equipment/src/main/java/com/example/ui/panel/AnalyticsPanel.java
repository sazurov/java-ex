package com.example.ui.panel;

import com.example.dao.EquipmentDAO;
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

public class AnalyticsPanel extends JPanel {
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();

    public AnalyticsPanel() {
        refreshData();
    }

    private void initComponents() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createCategoryPieChart());
        add(createStatusBarChart());
        add(createCostByCategoryChart());
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

    private ChartPanel createCategoryPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        List<Object[]> data = equipmentDAO.getCountByCategory();
        for (Object[] row : data) {
            String categoryName = (String) row[0];
            int count = (Integer) row[1];
            if (count > 0) {
                dataset.setValue(categoryName, count);
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Распределение оборудования по категориям",
                dataset,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }

    private ChartPanel createStatusBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Object[]> data = equipmentDAO.getCountByStatus();
        for (Object[] row : data) {
            String status = (String) row[0];
            int count = (Integer) row[1];
            if (count > 0) {
                dataset.addValue(count, "Количество", status);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Количество оборудования по статусам",
                "Статус",
                "Количество",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }

    private ChartPanel createCostByCategoryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] row : equipmentDAO.getCountByCategory()) {
            String categoryName = (String) row[0];
            int count = (Integer) row[1];
            dataset.addValue(count, "Единиц", categoryName);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Количество единиц по категориям",
                "Категория",
                "Единиц",
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
        stats.append("=== ОБЩАЯ СТАТИСТИКА ===\n\n");
        int totalEquipment = equipmentDAO.getTotalCount();
        BigDecimal totalCost = equipmentDAO.getTotalCost();
        BigDecimal avgCost = totalEquipment == 0 ? BigDecimal.ZERO :
                totalCost.divide(BigDecimal.valueOf(totalEquipment), 2, BigDecimal.ROUND_HALF_UP);

        stats.append(String.format("Всего единиц оборудования: %d\n", totalEquipment));
        stats.append(String.format("Общая стоимость: %.2f\n", totalCost));
        stats.append(String.format("Средняя стоимость: %.2f\n", avgCost));
        stats.append("\n=== ПО СТАТУСАМ ===\n");
        for (Object[] row : equipmentDAO.getCountByStatus()) {
            stats.append(String.format("%s: %d\n", row[0], row[1]));
        }

        stats.append("\n=== ПО КАТЕГОРИЯМ ===\n");
        for (Object[] row : equipmentDAO.getCountByCategory()) {
            stats.append(String.format("%s: %d\n", row[0], row[1]));
            stats.append("\n");
        }

        statsArea.setText(stats.toString());
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        return panel;
    }
}
