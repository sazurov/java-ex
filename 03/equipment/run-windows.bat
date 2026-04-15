@echo off
REM Быстрый гайд для запуска приложения на Windows

echo ========================================
echo Система учета оборудования
echo ========================================
echo.

REM Проверка наличия Maven
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ОШИБКА: Maven не установлен!
    echo Пожалуйста, установите Maven и добавьте его в PATH
    pause
    exit /b 1
)

REM Проверка наличия Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ОШИБКА: Java не установлена!
    echo Пожалуйста, установите Java 11+ и добавьте её в PATH
    pause
    exit /b 1
)

echo Java найдена...
echo Maven найден...
echo.

REM Проверка подключения к PostgreSQL
echo Проверка подключения к PostgreSQL...
echo Убедитесь, что:
echo 1. PostgreSQL запущена
echo 2. База данных 'equipment_db' существует
echo 3. Пользователь 'postgres' с паролем 'postgres'
echo 4. Порт подключения в приложении: 55434
echo.

REM Очистка и сборка проекта
echo Очистка и сборка проекта...
call mvn clean package

if errorlevel 1 (
    echo ОШИБКА при сборке проекта!
    pause
    exit /b 1
)

echo.
echo Сборка успешна!
echo Запуск приложения...
echo.

REM Запуск приложения
call mvn exec:java -Dexec.mainClass="com.example.Main"

pause
