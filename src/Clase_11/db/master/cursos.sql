-- Crear la base de datos con UTF-8
CREATE DATABASE IF NOT EXISTS `cursos-programDB`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `cursos-programDB`;

-- Crear tabla cursos
CREATE TABLE IF NOT EXISTS cursos
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    descripcion        TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    duracion           VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    precio             DECIMAL(10, 2),
    estado             ENUM ('disponible','no disponible','agotado') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    usuarios_accedidos INT DEFAULT 0
);

-- Crear tabla usuarios
CREATE TABLE IF NOT EXISTS usuarios
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100),
    email          VARCHAR(150) UNIQUE,
    password       VARCHAR(255),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos de ejemplo en cursos
INSERT INTO cursos (nombre, descripcion, duracion, precio, estado, usuarios_accedidos)
VALUES ('Curso de HTML Básico', 'Aprende a estructurar páginas web con HTML.', '10 horas', 0.00, 'disponible', 120),
       ('Curso de CSS Básico', 'Domina estilos y diseño web con CSS.', '12 horas', 0.00, 'disponible', 95),
       ('Curso de JavaScript Inicial', 'Introducción a la programación con JS.', '15 horas', 0.00, 'disponible', 80),
       ('Curso de Git y GitHub', 'Control de versiones y colaboración.', '8 horas', 0.00, 'agotado', 60),
       ('Curso de Node.js Básico', 'Construye aplicaciones backend simples.', '20 horas', 0.00, 'no disponible', 40);


-- Creación del usuario administrador
INSERT INTO usuarios (nombre, email, password)
VALUES ('Administrador', 'admin@cursos.com', 'admin123');
