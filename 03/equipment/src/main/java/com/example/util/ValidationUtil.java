package com.example.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern INVENTORY_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_/]{3,50}$");

    public static ValidationResult validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Название категории не может быть пустым");
        }
        if (name.trim().length() > 100) {
            return new ValidationResult(false, "Название категории слишком длинное (максимум 100 символов)");
        }
        return new ValidationResult(true, "");
    }

    public static ValidationResult validateEquipmentName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Название оборудования не может быть пустым");
        }
        if (name.trim().length() > 150) {
            return new ValidationResult(false, "Название оборудования слишком длинное (максимум 150 символов)");
        }
        return new ValidationResult(true, "");
    }

    public static ValidationResult validateInventoryNumber(String inventoryNumber) {
        if (inventoryNumber == null || inventoryNumber.trim().isEmpty()) {
            return new ValidationResult(false, "Инвентарный номер не может быть пустым");
        }
        if (!INVENTORY_PATTERN.matcher(inventoryNumber.trim()).matches()) {
            return new ValidationResult(false, "Инвентарный номер: 3-50 символов, буквы/цифры и - _ /");
        }
        return new ValidationResult(true, "");
    }

    public static ValidationResult validateCost(BigDecimal cost) {
        if (cost == null) {
            return new ValidationResult(false, "Стоимость не может быть пустой");
        }
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            return new ValidationResult(false, "Стоимость не может быть отрицательной");
        }
        if (cost.compareTo(new BigDecimal("999999999.99")) > 0) {
            return new ValidationResult(false, "Стоимость слишком большая");
        }
        return new ValidationResult(true, "");
    }

    public static ValidationResult validatePurchaseDate(LocalDate purchaseDate) {
        if (purchaseDate != null && purchaseDate.isAfter(LocalDate.now())) {
            return new ValidationResult(false, "Дата покупки не может быть в будущем");
        }
        return new ValidationResult(true, "");
    }

    public static ValidationResult validateLocation(String location) {
        if (location != null && location.trim().length() > 150) {
            return new ValidationResult(false, "Локация слишком длинная (максимум 150 символов)");
        }
        return new ValidationResult(true, "");
    }

    public static class ValidationResult {
        public final boolean valid;
        public final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
    }
}
