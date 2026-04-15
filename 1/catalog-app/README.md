# Catalog App

Desktop-приложение (Swing) для управления товарами и категориями.

## Требования

- Java 17+
- Maven 3.9+
- Docker (для быстрого старта PostgreSQL)

## 1) Поднять базу данных

```bash
docker run --name catalog-db \
  -e POSTGRES_DB=catalog \
  -e POSTGRES_USER=catalog \
  -e POSTGRES_PASSWORD=catalog \
  -p 5432:5432 -d postgres:16
```

## 2) Применить схему

```bash
docker exec -i catalog-db psql -U catalog -d catalog < src/main/resources/schema.sql
```

## 3) Настроить подключение (опционально)

По умолчанию приложение использует:

- `CATALOG_DB_URL=jdbc:postgresql://localhost:5432/catalog`
- `CATALOG_DB_USER=catalog`
- `CATALOG_DB_PASSWORD=catalog`

Можно переопределить через переменные окружения.

## 4) Запуск приложения

```bash
mvn compile exec:java
```
