# Система учета сотрудников и отделов

Полнофункциональное десктопное приложение для управления сотрудниками и отделами с использованием Java Swing и PostgreSQL.

## Характеристики

### CRUD операции
- **Сотрудники**: Добавление, просмотр, редактирование, удаление
- **Отделы**: Добавление, просмотр, редактирование, удаление

### Функциональность
- 🔍 **Поиск** по сотрудникам (имя, фамилия, email, должность)
- 🔍 **Поиск** по отделам (название, описание)
- 📊 **Фильтрация** по отделам и диапазону зарплаты
- ✓ **Валидация** всех вводимых данных
- 📈 **Аналитика** с графиками:
  - Распределение сотрудников по отделам
  - Общая зарплата по отделам
  - Средняя зарплата по отделам
  - Статистика по компании

## Требования

- Java 11+
- Maven 3.6+
- Docker Desktop (рекомендуется для быстрого старта PostgreSQL)

Проверьте инструменты в PowerShell:

```powershell
java -version
mvn -version
docker --version
```

## Запуск на Windows (PowerShell)

### 1) Перейти в папку проекта

```powershell
cd "C:\Users\User\Desktop\java-ex\02\employee"
```

### 2) Поднять PostgreSQL в Docker на отдельном порту

В проекте используется порт `55433`, чтобы избежать конфликтов с локальным PostgreSQL на `5432`.

```powershell
docker rm -f employee-db
docker run --name employee-db `
  -e POSTGRES_DB=employee_db `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -p 55433:5432 -d postgres:16
```

### 3) Дождаться готовности БД

```powershell
do {
  Start-Sleep -Seconds 2
  docker exec employee-db pg_isready -U postgres -d employee_db | Out-Host
} until ($LASTEXITCODE -eq 0)
```

### 4) Проверить параметры подключения в приложении

Файл: `src/main/java/com/example/database/DatabaseConnection.java`

```java
private static final String URL = "jdbc:postgresql://localhost:55433/employee_db";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

### 5) Сборка и запуск приложения

```powershell
mvn clean package
mvn exec:java "-Dexec.mainClass=com.example.Main"
```

Если в PowerShell возникает ошибка `Unknown lifecycle phase ".mainClass=..."`, запустите так:

```powershell
mvn --% exec:java -Dexec.mainClass=com.example.Main
```

## Быстрые команды

Остановить/запустить БД:

```powershell
docker stop employee-db
docker start employee-db
```

Удалить БД-контейнер:

```powershell
docker rm -f employee-db
```

## Структура проекта

```
employee-system/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/example/
                ├── Main.java
                ├── database/
                │   └── DatabaseConnection.java
                ├── model/
                │   ├── Department.java
                │   └── Employee.java
                ├── dao/
                │   ├── DepartmentDAO.java
                │   └── EmployeeDAO.java
                ├── util/
                │   └── ValidationUtil.java
                └── ui/
                    ├── MainFrame.java
                    ├── dialog/
                    │   ├── DepartmentDialog.java
                    │   └── EmployeeDialog.java
                    └── panel/
                        ├── DepartmentPanel.java
                        ├── EmployeePanel.java
                        └── AnalyticsPanel.java
```

## Используемые технологии

- **Java Swing** - GUI фреймворк
- **PostgreSQL** - Релационная база данных
- **JDBC** - Драйвер для работы с БД
- **JFreeChart** - Библиотека для создания графиков
- **SLF4J** - Логирование
- **Maven** - Управление проектом

## Использование приложения

### Вкладка "Сотрудники"

1. **Добавить сотрудника**: Нажмите "+ Добавить"
2. **Редактировать**: Выберите сотрудника и нажмите "✎ Редактировать"
3. **Удалить**: Выберите сотрудника и нажмите "✗ Удалить"
4. **Поиск**: Введите текст и нажмите "Поиск"
5. **Фильтрация**: Выберите отдел и/или диапазон зарплаты

### Вкладка "Отделы"

1. **Добавить отдел**: Нажмите "+ Добавить"
2. **Редактировать**: Выберите отдел и нажмите "✎ Редактировать"
3. **Удалить**: Выберите отдел и нажмите "✗ Удалить"
4. **Поиск**: Введите текст и нажмите "Поиск"

### Вкладка "Аналитика"

Просмотрите аналитику по:
- Распределению сотрудников
- Зарплатам по отделам
- Общей статистике компании

## Валидация данных

Приложение проверяет:
- **Email**: Формат email-адреса
- **Имя/Фамилия**: Только буквы и пробелы (до 50 символов)
- **Телефон**: Формат телефонного номера
- **Зарплата**: Положительное число (до 999999.99)
- **Дата приема**: Не в будущем
- **Должность**: Не пустая (до 100 символов)
- **Название отдела**: Не пустое (до 100 символов)

## Примечания

- При удалении отдела все сотрудники этого отдела также будут удалены (каскадное удаление)
- Email должен быть уникальным
- Поиск выполняется без учета регистра

## Отладка

Приложение логирует все операции в консоль. Для просмотра логов используйте:

```
INFO [DatabaseConnection] База данных успешно инициализирована
INFO [DepartmentDAO] Отдел успешно создан: ...
```

## Лицензия

MIT License

## Автор

Разработано в 2024 году
