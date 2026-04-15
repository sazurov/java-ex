# Catalog App

Desktop-приложение (Swing) для управления товарами и категориями.

## Запуск на Windows (PowerShell)

## Требования

- Java 17+
- Maven 3.9+
- Docker Desktop (для быстрого старта PostgreSQL)

Проверьте, что инструменты доступны в PowerShell:

```powershell
java -version
mvn -version
docker --version
```

## 1) Перейти в папку проекта

```powershell
cd "C:\Users\User\Desktop\java-ex\1\catalog-app"
```

## 2) Поднять базу данных (рекомендуемый вариант)

Если вы уже запускали контейнер раньше, удалите его и поднимите заново на порту `55432`.
Так вы избежите конфликтов с локальным PostgreSQL на `5432`.

```powershell
docker rm -f catalog-db
docker run --name catalog-db `
  -e POSTGRES_DB=catalog `
  -e POSTGRES_USER=catalog `
  -e POSTGRES_PASSWORD=catalog `
  -p 55432:5432 -d postgres:16
```

## 3) Дождаться готовности PostgreSQL

```powershell
do {
  Start-Sleep -Seconds 2
  docker exec catalog-db pg_isready -U catalog -d catalog | Out-Host
} until ($LASTEXITCODE -eq 0)
```

## 4) Применить схему

```powershell
Get-Content .\src\main\resources\schema.sql | docker exec -i catalog-db psql -U catalog -d catalog
```

## 5) Настроить подключение для запуска приложения

В текущей сессии PowerShell:

```powershell
$env:CATALOG_DB_URL="jdbc:postgresql://127.0.0.1:55432/catalog"
$env:CATALOG_DB_USER="catalog"
$env:CATALOG_DB_PASSWORD="catalog"
```

## 6) Запуск приложения

```powershell
mvn compile exec:java
```

## Быстрая проверка подключения

```powershell
docker exec -i catalog-db psql -U catalog -d catalog -c "\dt"
```

В выводе должны быть таблицы `categories` и `products`.
