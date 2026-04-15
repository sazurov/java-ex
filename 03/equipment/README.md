# Система учета оборудования

Desktop-приложение (Java Swing) для учета оборудования и категорий с хранением данных в PostgreSQL.

## Возможности

- CRUD для категорий
- CRUD для оборудования
- Поиск по инвентарному номеру, названию, локации
- Фильтрация оборудования по категории, статусу и диапазону стоимости
- Валидация данных при добавлении/редактировании
- Базовая статистика с графиками:
  - распределение по категориям
  - распределение по статусам
  - общая и средняя стоимость

## Требования

- Java 11+
- Maven 3.6+
- Docker Desktop

Проверка:

```powershell
java -version
mvn -version
docker --version
```

## Запуск на Windows (PowerShell)

### 1) Перейти в папку проекта

```powershell
cd "C:\Users\User\Desktop\java-ex\03\equipment"
```

### 2) Поднять PostgreSQL в Docker

Проект использует порт `55434`, чтобы не конфликтовать с локальным PostgreSQL.

```powershell
docker rm -f equipment-db
docker run --name equipment-db `
  -e POSTGRES_DB=equipment_db `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -p 55434:5432 -d postgres:16
```

### 3) Дождаться готовности БД

```powershell
do {
  Start-Sleep -Seconds 2
  docker exec equipment-db pg_isready -U postgres -d equipment_db | Out-Host
} until ($LASTEXITCODE -eq 0)
```

### 4) Проверить параметры подключения

Файл: `src/main/java/com/example/database/DatabaseConnection.java`

```java
private static final String URL = "jdbc:postgresql://localhost:55434/equipment_db";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

### 5) Собрать и запустить

```powershell
mvn clean package
mvn exec:java "-Dexec.mainClass=com.example.Main"
```

Если PowerShell показывает ошибку `Unknown lifecycle phase ".mainClass=..."`, используйте:

```powershell
mvn --% exec:java -Dexec.mainClass=com.example.Main
```

## Быстрые команды для БД

```powershell
docker stop equipment-db
docker start equipment-db
docker rm -f equipment-db
```

## Структура проекта

```text
equipment/
├── pom.xml
├── README.md
├── run-windows.bat
└── src/main/java/com/example/
    ├── Main.java
    ├── database/
    │   └── DatabaseConnection.java
    ├── model/
    │   ├── Category.java
    │   └── Equipment.java
    ├── dao/
    │   ├── CategoryDAO.java
    │   └── EquipmentDAO.java
    ├── util/
    │   └── ValidationUtil.java
    └── ui/
        ├── MainFrame.java
        ├── dialog/
        │   ├── CategoryDialog.java
        │   └── EquipmentDialog.java
        └── panel/
            ├── CategoryPanel.java
            ├── EquipmentPanel.java
            └── AnalyticsPanel.java
```
