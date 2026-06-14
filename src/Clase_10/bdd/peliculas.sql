-- Create database with full UTF-8 support
CREATE DATABASE IF NOT EXISTS cinema
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE cinema;

-- Single table containing all data
CREATE TABLE movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    director VARCHAR(100) NOT NULL,
    release_year INT NOT NULL,
    genre VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert all 21 records directly into the single table
INSERT INTO movies (title, director, release_year, genre) VALUES
('Inception', 'Christopher Nolan', 2010, 'Science Fiction'),
('Parasite', 'Bong Joon-ho', 2019, 'Drama'),
('Interstellar', 'Christopher Nolan', 2014, 'Science Fiction'),
('The Godfather', 'Francis Ford Coppola', 1972, 'Crime'),
('Spirited Away', 'Hayao Miyazaki', 2001, 'Animation'),
('The Dark Knight', 'Christopher Nolan', 2008, 'Action'),
('Pulp Fiction', 'Quentin Tarantino', 1994, 'Crime'),
('The Shawshank Redemption', 'Frank Darabont', 1994, 'Drama'),
('Fight Club', 'David Fincher', 1999, 'Drama'),
('Forrest Gump', 'Robert Zemeckis', 1994, 'Drama'),
('The Matrix', 'Lana and Lilly Wachowski', 1999, 'Science Fiction'),
('City of God', 'Fernando Meirelles', 2002, 'Crime'),
('La La Land', 'Damien Chazelle', 2016, 'Musical'),
('Gladiator', 'Ridley Scott', 2000, 'Action'),
('Titanic', 'James Cameron', 1997, 'Romance'),
('The Lord of the Rings: The Fellowship of the Ring', 'Peter Jackson', 2001, 'Fantasy'),
('Goodfellas', 'Martin Scorsese', 1990, 'Crime'),
('Se7en', 'David Fincher', 1995, 'Thriller'),
('The Silence of the Lambs', 'Jonathan Demme', 1991, 'Thriller'),
('Avatar', 'James Cameron', 2009, 'Science Fiction'),
('Whiplash', 'Damien Chazelle', 2014, 'Drama');