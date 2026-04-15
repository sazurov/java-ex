-- Скрипт для создания и заполнения тестовыми данными

-- Создание отделов
INSERT INTO departments (name, description) VALUES
('IT', 'Отдел информационных технологий'),
('HR', 'Отдел человеческих ресурсов'),
('Sales', 'Отдел продаж'),
('Finance', 'Финансовый отдел'),
('Marketing', 'Отдел маркетинга');

-- Создание сотрудников

-- IT Отдел
INSERT INTO employees (first_name, last_name, email, phone, salary, hire_date, position, department_id) VALUES
('Иван', 'Петров', 'ivan.petrov@company.com', '+7-999-123-45-67', 85000.00, '2020-01-15', 'Старший разработчик', 1),
('Мария', 'Сидорова', 'maria.sidorova@company.com', '+7-999-234-56-78', 75000.00, '2021-03-20', 'Разработчик', 1),
('Петр', 'Иванов', 'petr.ivanov@company.com', '+7-999-345-67-89', 65000.00, '2022-06-10', 'Младший разработчик', 1),
('Анна', 'Васильева', 'anna.vasileva@company.com', '+7-999-456-78-90', 70000.00, '2021-09-15', 'Frontend разработчик', 1);

-- HR Отдел
INSERT INTO employees (first_name, last_name, email, phone, salary, hire_date, position, department_id) VALUES
('Светлана', 'Морозова', 'svetlana.morozova@company.com', '+7-999-567-89-01', 60000.00, '2020-05-12', 'HR менеджер', 2),
('Олег', 'Смирнов', 'oleg.smirnov@company.com', '+7-999-678-90-12', 55000.00, '2021-08-22', 'Специалист по подбору', 2);

-- Sales Отдел
INSERT INTO employees (first_name, last_name, email, phone, salary, hire_date, position, department_id) VALUES
('Сергей', 'Козлов', 'sergey.kozlov@company.com', '+7-999-789-01-23', 70000.00, '2020-02-10', 'Менеджер продаж', 3),
('Елена', 'Федорова', 'elena.fedorova@company.com', '+7-999-890-12-34', 65000.00, '2021-04-15', 'Менеджер продаж', 3),
('Владимир', 'Новиков', 'vladimir.novikov@company.com', '+7-999-901-23-45', 72000.00, '2020-10-01', 'Руководитель отдела продаж', 3);

-- Finance Отдел
INSERT INTO employees (first_name, last_name, email, phone, salary, hire_date, position, department_id) VALUES
('Людмила', 'Соколова', 'ludmila.sokolova@company.com', '+7-999-012-34-56', 68000.00, '2019-11-15', 'Финансовый аналитик', 4),
('Юрий', 'Егоров', 'yuri.egorov@company.com', '+7-999-123-01-23', 80000.00, '2019-01-10', 'Главный бухгалтер', 4),
('Алла', 'Ромашова', 'alla.romashova@company.com', '+7-999-234-12-34', 62000.00, '2021-05-20', 'Бухгалтер', 4);

-- Marketing Отдел
INSERT INTO employees (first_name, last_name, email, phone, salary, hire_date, position, department_id) VALUES
('Дмитрий', 'Лебедев', 'dmitry.lebedev@company.com', '+7-999-345-23-45', 75000.00, '2020-07-12', 'SMM менеджер', 5),
('Наталья', 'Волкова', 'natalia.volkova@company.com', '+7-999-456-34-56', 68000.00, '2021-02-15', 'Контент-менеджер', 5),
('Виктор', 'Орлов', 'victor.orlov@company.com', '+7-999-567-45-67', 78000.00, '2019-09-20', 'Руководитель отдела маркетинга', 5);
