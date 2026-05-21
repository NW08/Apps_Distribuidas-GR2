CREATE DATABASE IF NOT EXISTS base_empleados
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE base_empleados;

CREATE TABLE IF NOT EXISTS personal
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    cargo  VARCHAR(30),
    sueldo DECIMAL(10, 2)
)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

INSERT INTO personal (nombre, cargo, sueldo)
VALUES ('Juan Pérez', 'Gerente', 2500.00),
       ('María García', 'Analista', 1800.50),
       ('Luis López', 'Desarrollador', 2000.75),
       ('Ana Torres', 'Diseñadora', 1750.00),
       ('Pedro Ruiz', 'Tester', 1600.00),
       ('Carmen Vega', 'Contadora', 1950.00),
       ('Carlos Molina', 'Administrador', 2100.00),
       ('Laura Salazar', 'Secretaria', 1500.00),
       ('Andrés Viteri', 'Soporte Técnico', 1600.00),
       ('Fernanda Chávez', 'Marketing', 1850.00),
       ('Esteban Paredes', 'Ventas', 1700.00),
       ('Gina Ruiz', 'Ventas', 1700.00),
       ('Roberto Jara', 'Seguridad', 1400.00),
       ('Martha Rivas', 'Asistente', 1550.00),
       ('Diego Castro', 'Auditor', 2000.00),
       ('Daniela Cedeño', 'Recursos Humanos', 1900.00),
       ('Kevin Mora', 'Desarrollador', 2050.00),
       ('Lucía Méndez', 'Diseñadora', 1780.00),
       ('Pablo Ortiz', 'Tester', 1650.00),
       ('Rocío Arias', 'Soporte Técnico', 1625.00),
       ('Hernán Silva', 'Administrador', 2150.00),
       ('Silvia Bravo', 'Analista', 1850.00),
       ('Sofía Jiménez', 'Ventas', 1680.00),
       ('Ramiro León', 'Gerente', 2550.00),
       ('Dayana Gómez', 'Contadora', 1920.00),
       ('Jorge Tapia', 'Auditor', 1980.00),
       ('Beatriz Cobo', 'Secretaria', 1520.00),
       ('Fabián Torres', 'Soporte Técnico', 1610.00),
       ('Melanie Rosero', 'Recursos Humanos', 1930.00),
       ('Iván Méndez', 'Marketing', 1875.00);
