package com.example.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Утилита для валидации данных
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9+\\-() ]{7,}$"
    );

    /**
     * Проверить валидность email
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email не может быть пустым");
        }
        if (email.length() > 100) {
            return new ValidationResult(false, "Email не может быть длиннее 100 символов");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ValidationResult(false, "Неверный формат email");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Проверить валидность имени
     */
    public static ValidationResult validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + " не может быть пустым");
        }
        if (name.length() > 50) {
            return new ValidationResult(false, fieldName + " не может быть длиннее 50 символов");
        }
        if (!name.matches("^[а-яА-ЯёЁa-zA-Z\\s'-]+$")) {
            return new ValidationResult(false, fieldName + " может содержать только буквы");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Проверить валидность телефона
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(false, "Телефон не может быть пустым");
        }
        if (phone.length() > 20) {
            return new ValidationResult(false, "Телефон не может быть длиннее 20 символов");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return new ValidationResult(false, "Неверный формат телефона");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Проверить валидность зарплаты
     */
    public static ValidationResult validateSalary(BigDecimal salary) {
        if (salary == null) {
            return new ValidationResult(false, "Зарплата не может быть пустой");
        }
        if (salary.compareTo(BigDecimal.ZERO) <= 0) {
            return new ValidationResult(false, "Зарплата должна быть больше 0");
        }
        if (salary.compareTo(new BigDecimal("999999.99")) > 0) {
            return new ValidationResult(false, "Зарплата не может быть больше 999999.99");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Проверить валидность даты
     */
    public static ValidationResult validateHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            return new ValidationResult(false, "Дата приема не может быть пустой");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            return new ValidationResult(false, "Дата приема не может быть в будущем");
        }
        if (hireDate.isBefore(LocalDate.of(1970, 1, 1))) {
            return new ValidationResult(false, "Дата приема слишком старая");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Проверить валидность должности
     */
    public static ValidationResult validatePosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            return new ValidationResult(false, "Должность не может быть пустой");
        }
        if (position.length() > 100) {
            return new ValidationResult(false, "Должность не может быть длиннее 100 символов");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Проверить валидность названия отдела
     */
    public static ValidationResult validateDepartmentName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Название отдела не может быть пустым");
        }
        if (name.length() > 100) {
            return new ValidationResult(false, "Название отдела не может быть длиннее 100 символов");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Класс результата валидации
     */
    public static class ValidationResult {
        public final boolean valid;
        public final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
    }
}
