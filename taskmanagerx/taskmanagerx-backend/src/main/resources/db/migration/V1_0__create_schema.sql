-- init tables
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS tasks;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    profile_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    auth_provider VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- add user and admin roles to roles table
MERGE INTO roles (id, name)
    KEY(name)
    VALUES (RANDOM_UUID(), 'ROLE_USER');

MERGE INTO roles (id, name)
    KEY(name)
    VALUES (RANDOM_UUID(), 'ROLE_ADMIN');

-- (temp) solution for adding admin
INSERT INTO users (id, name, email, password, profile_image, created_at, auth_provider)
VALUES (RANDOM_UUID(), 'admin', 'admin@example.moc',
        '$2a$10$atOzquGlgkP9NUmG/IdlTu..85YWpBqozAmFYVOYZqOTP16htr8AG',
        NULL, CURRENT_TIMESTAMP, 'DEFAULT');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@example.moc' AND r.name IN ('ROLE_USER', 'ROLE_ADMIN');