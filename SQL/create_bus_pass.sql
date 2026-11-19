-- ============================================================================
-- SQL Setup Script: Bus Pass Persistence Store
-- Student Name: Geetika T
-- Register Number: 192511469
-- Subject: Programming in Java (CSA09) - Assignment 5
-- Course Outcome: CO5 - Java I/O and JDBC
-- ============================================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS transport_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE transport_db;

-- 2. Drop existing table for fresh setup
DROP TABLE IF EXISTS bus_pass;

-- 3. Create bus_pass table with proper types and constraints
CREATE TABLE bus_pass (
    pass_id         VARCHAR(20)     NOT NULL,
    reg_no          VARCHAR(20)     NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    route_no        VARCHAR(20)     NOT NULL,
    boarding_stop   VARCHAR(100)    NOT NULL,
    validity_date   DATE            NOT NULL,
    amount_paid     DECIMAL(10, 2)  NOT NULL,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    -- Primary Key Constraint
    CONSTRAINT pk_bus_pass PRIMARY KEY (pass_id),

    -- Domain Check Constraints
    CONSTRAINT chk_positive_amount CHECK (amount_paid >= 0.00),
    CONSTRAINT chk_pass_id_format  CHECK (pass_id LIKE 'P%')
) ENGINE = InnoDB;

-- 4. Create Secondary Indices for High-Frequency Query Filtering
CREATE INDEX idx_bus_pass_route ON bus_pass(route_no);
CREATE INDEX idx_bus_pass_regno ON bus_pass(reg_no);

-- 5. Create users table for authentication tests (Task 05 / Task 07)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  DEFAULT 'STUDENT'
) ENGINE = InnoDB;

INSERT INTO users (username, password, role) VALUES
('admin', 'Admin$Secure2026', 'ADMIN'),
('geetika', 'Geetika@Pass123', 'STUDENT'),
('student', 'Student2026!', 'STUDENT');

-- 6. Create wallet tables for transaction tests (Task 05)
DROP TABLE IF EXISTS hostel_wallet;
CREATE TABLE hostel_wallet (
    reg_no  VARCHAR(20) PRIMARY KEY,
    balance DECIMAL(10, 2) NOT NULL CHECK (balance >= 0.00)
) ENGINE = InnoDB;

DROP TABLE IF EXISTS canteen_wallet;
CREATE TABLE canteen_wallet (
    reg_no  VARCHAR(20) PRIMARY KEY,
    balance DECIMAL(10, 2) NOT NULL CHECK (balance >= 0.00)
) ENGINE = InnoDB;

INSERT INTO hostel_wallet (reg_no, balance) VALUES ('192511469', 1200.00);
INSERT INTO canteen_wallet (reg_no, balance) VALUES ('192511469', 150.00);

-- Verification Query
SHOW TABLES;
DESCRIBE bus_pass;
