USE test;

-- Entidad Client
CREATE TABLE client
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    birthday   DATE        NOT NULL
);

-- Entidad Card (Relación 1:1)
CREATE TABLE card
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance   DECIMAL(10, 2) DEFAULT 0.00 NOT NULL,
    client_id BIGINT UNIQUE               NOT NULL,

    CONSTRAINT fk_card_client
        FOREIGN KEY (client_id)
            REFERENCES client (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);
