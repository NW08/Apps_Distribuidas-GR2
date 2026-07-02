CREATE DATABASE IF NOT EXISTS paises
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE paises;

CREATE TABLE entrenadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    entrenador VARCHAR(100) NOT NULL,
    ranking_fifa INT NOT NULL DEFAULT 0,
    participaciones INT NOT NULL DEFAULT 0,
    campeonatos INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO entrenadores (id, nombre, entrenador, ranking_fifa, participaciones, campeonatos) VALUES
(1, 'Argentina', 'Lionel Scaloni', 1, 18, 3),
(2, 'Brasil', 'Dorival Júnior', 3, 22, 5),
(3, 'Alemania', 'Julian Nagelsmann', 10, 20, 4),
(4, 'Francia', 'Didier Deschamps', 2, 16, 2),
(5, 'España', 'Luis de la Fuente', 8, 16, 1),
(6, 'Inglaterra', 'Gareth Southgate', 4, 16, 1),
(7, 'Italia', 'Luciano Spalletti', 9, 18, 4),
(8, 'Países Bajos', 'Ronald Koeman', 7, 11, 0),
(9, 'Portugal', 'Roberto Martínez', 6, 8, 0),
(10, 'Bélgica', 'Rudi García', 9, 13, 0),
(11, 'México', 'Jaime Lozano', 12, 17, 0),
(12, 'Estados Unidos', 'Gregg Berhalter', 11, 11, 0),
(13, 'Canadá', 'John Herdman', 30, 2, 0),
(14, 'Uruguay', 'Marcelo Bielsa', 14, 14, 2),
(15, 'Croacia', 'Zlatko Dalić', 15, 6, 0),
(16, 'Colombia', 'Néstor Lorenzo', 13, 6, 0),
(17, 'Chile', 'Ricardo Gareca', 20, 9, 0),
(18, 'Ecuador', 'Félix Sánchez', 32, 4, 0),
(19, 'Perú', 'Juan Reynoso', 25, 5, 0),
(20, 'Paraguay', 'Daniel Garnero', 35, 8, 0),
(21, 'Japón', 'Hajime Moriyasu', 18, 7, 0),
(22, 'Corea del Sur', 'Jürgen Klinsmann', 22, 11, 0),
(23, 'Australia', 'Graham Arnold', 27, 6, 0),
(24, 'Marruecos', 'Walid Regragui', 8, 6, 0),
(25, 'Senegal', 'Aliou Cissé', 19, 3, 0),
(26, 'Egipto', 'Rui Vitória', 29, 3, 0),
(27, 'Nigeria', 'José Peseiro', 28, 6, 0),
(28, 'Ghana', 'Chris Hughton', 24, 4, 0),
(29, 'Turquía', 'Vincenzo Montella', 26, 3, 0),
(30, 'Suiza', 'Murat Yakin', 16, 12, 0);