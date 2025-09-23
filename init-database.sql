-- SportHub Database Initialization Script
-- This script will be executed when MySQL container starts

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS sporthub_db;

-- Use the database
USE sporthub_db;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'sporthub_user'@'%' IDENTIFIED BY 'userpass';
GRANT ALL PRIVILEGES ON sporthub_db.* TO 'sporthub_user'@'%';

-- Create basic tables for microservices
-- User Service Tables
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bio TEXT,
    profile_image_url VARCHAR(255),
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Court Service Tables
CREATE TABLE IF NOT EXISTS courts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    location VARCHAR(255) NOT NULL,
    city VARCHAR(50) NOT NULL,
    sport_type VARCHAR(50) NOT NULL,
    hourly_rate DECIMAL(10,2) NOT NULL,
    capacity INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS court_amenities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    court_id BIGINT NOT NULL,
    amenity_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE CASCADE
);

-- Reservation Service Tables
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    court_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_reservations (user_id),
    INDEX idx_court_reservations (court_id),
    INDEX idx_reservation_date (reservation_date)
);

-- Payment Service Tables
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_payments (user_id),
    INDEX idx_payment_status (status)
);

-- Notification Service Tables
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_notifications (user_id),
    INDEX idx_unread_notifications (user_id, is_read)
);

-- Insert sample data
INSERT IGNORE INTO users (username, email, password, first_name, last_name, phone) VALUES
('admin', 'admin@sporthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyifkPNTAti', 'Admin', 'User', '+905551234567'),
('testuser', 'test@sporthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyifkPNTAti', 'Test', 'User', '+905551234568');

INSERT IGNORE INTO courts (name, description, location, city, sport_type, hourly_rate, capacity) VALUES
('Center Court Tennis', 'Professional tennis court with LED lighting', 'Merkez Mahallesi, Spor Sokak No:1', 'Istanbul', 'TENNIS', 150.00, 4),
('Basketball Arena', 'Indoor basketball court with wooden floor', 'Spor Kompleksi, Basketbol Salonu', 'Istanbul', 'BASKETBALL', 200.00, 10),
('Football Field A', 'Natural grass football field', 'Stadyum Kompleksi, A Sahası', 'Istanbul', 'FOOTBALL', 300.00, 22);

INSERT IGNORE INTO court_amenities (court_id, amenity_name) VALUES
(1, 'LED Lighting'), (1, 'Changing Room'), (1, 'Parking'),
(2, 'Air Conditioning'), (2, 'Sound System'), (2, 'Scoreboard'),
(3, 'Natural Grass'), (3, 'Goal Posts'), (3, 'Bench');

-- Flush privileges
FLUSH PRIVILEGES;

-- Show created tables
SHOW TABLES;
