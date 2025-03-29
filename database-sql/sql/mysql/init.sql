USE `telecom`;

CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    access_token VARCHAR(255),
    access_token_expires_at TIMESTAMP,
    refresh_token VARCHAR(255)
);

# INSERT INTO `users` (username, email, password)
# VALUES ('admin', 'admin@telecom.com', 'admin123');