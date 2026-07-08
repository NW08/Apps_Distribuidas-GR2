-- 1. Crear y usar la base de datos
DROP DATABASE IF EXISTS farmacia_demo;
CREATE DATABASE farmacia_demo;
USE farmacia_demo;

-- 2. Crear la tabla única de productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_comercial VARCHAR(100),
    principio_activo VARCHAR(100),
    categoria VARCHAR(50),
    laboratorio VARCHAR(100),
    precio DECIMAL(8,2),
    stock INT
);

-- 3. Poblar la base de datos con 30 registros
INSERT INTO productos (nombre_comercial, principio_activo, categoria, laboratorio, precio, stock) VALUES
('Tylenol', 'Paracetamol', 'Analgésico', 'Johnson & Johnson', 5.50, 100),
('Advil', 'Ibuprofeno', 'Antiinflamatorio', 'Pfizer', 6.75, 85),
('Amoxil', 'Amoxicilina', 'Antibiótico', 'GSK', 12.00, 40),
('Clarityne', 'Loratadina', 'Antihistamínico', 'Bayer', 8.20, 60),
('Nexium', 'Esomeprazol', 'Gastrointestinal', 'AstraZeneca', 15.50, 30),
('Aspirina', 'Ácido Acetilsalicílico', 'Analgésico', 'Bayer', 3.50, 150),
('Buscapina', 'Hioscina', 'Antiespasmódico', 'Sanofi', 7.00, 75),
('Vick VapoRub', 'Alcanfor/Mentol', 'Respiratorio', 'Procter & Gamble', 4.80, 120),
('Pepto-Bismol', 'Subsalicilato de bismuto', 'Gastrointestinal', 'Procter & Gamble', 6.00, 90),
('Panadol', 'Paracetamol', 'Analgésico', 'GSK', 4.50, 110),
('Voltaren', 'Diclofenaco', 'Antiinflamatorio', 'Novartis', 9.99, 50),
('Zyrtec', 'Cetirizina', 'Antihistamínico', 'Johnson & Johnson', 11.25, 45),
('Ciproxina', 'Ciprofloxacino', 'Antibiótico', 'Bayer', 18.00, 20),
('Lomecan', 'Clotrimazol', 'Antimicótico', 'Genomma Lab', 14.50, 35),
('Supradyn', 'Multivitamínico', 'Vitaminas', 'Bayer', 22.00, 25),
('Eutirox', 'Levotiroxina', 'Hormonal', 'Merck', 10.50, 80),
('Glucophage', 'Metformina', 'Antidiabético', 'Merck', 8.90, 65),
('Lipitor', 'Atorvastatina', 'Cardiovascular', 'Pfizer', 25.00, 15),
('Plavix', 'Clopidogrel', 'Cardiovascular', 'Sanofi', 28.50, 10),
('Ventolin', 'Salbutamol', 'Respiratorio', 'GSK', 13.75, 55),
('Riopan', 'Magaldrato', 'Gastrointestinal', 'Takeda', 11.00, 40),
('Barmicil', 'Betametasona', 'Dermatológico', 'Química Sons', 5.00, 100),
('Bedoyecta', 'Complejo B', 'Vitaminas', 'Grosman', 16.80, 45),
('Mucosolvan', 'Ambroxol', 'Respiratorio', 'Sanofi', 7.50, 60),
('Dramamine', 'Dimenhidrinato', 'Antiemético', 'Johnson & Johnson', 6.20, 70),
('Sal de Uvas Picot', 'Bicarbonato de sodio', 'Gastrointestinal', 'Reckitt', 2.50, 200),
('Loratadina Genérica', 'Loratadina', 'Antihistamínico', 'Genérico', 3.00, 150),
('Omeprazol Genérico', 'Omeprazol', 'Gastrointestinal', 'Genérico', 4.50, 100),
('Paracetamol Genérico', 'Paracetamol', 'Analgésico', 'Genérico', 1.50, 300),
('Ibuprofeno Genérico', 'Ibuprofeno', 'Antiinflamatorio', 'Genérico', 2.00, 250);