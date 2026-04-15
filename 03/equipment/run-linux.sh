#!/bin/bash
# Быстрый гайд для запуска приложения на Linux/Mac

echo "========================================"
echo "Система учета сотрудников и отделов"
echo "========================================"
echo

# Проверка наличия Maven
if ! command -v mvn &> /dev/null; then
    echo "ОШИБКА: Maven не установлен!"
    echo "Установите Maven: brew install maven (macOS) или apt-get install maven (Linux)"
    exit 1
fi

# Проверка наличия Java
if ! command -v java &> /dev/null; then
    echo "ОШИБКА: Java не установлена!"
    echo "Установите Java 11+: brew install openjdk@11 (macOS) или apt-get install openjdk-11-jdk (Linux)"
    exit 1
fi

echo "Java найдена..."
echo "Maven найден..."
echo

# Проверка подключения к PostgreSQL
echo "Проверка подключения к PostgreSQL..."
echo "Убедитесь, что:"
echo "1. PostgreSQL запущена"
echo "2. База данных 'employee_db' существует"
echo "3. Пользователь 'postgres' с паролем 'postgres'"
echo

# Очистка и сборка проекта
echo "Очистка и сборка проекта..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "ОШИБКА при сборке проекта!"
    exit 1
fi

echo
echo "Сборка успешна!"
echo "Запуск приложения..."
echo

# Запуск приложения
mvn exec:java -Dexec.mainClass="com.example.Main"
