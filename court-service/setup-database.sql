-- SportHub Court Service Database Setup
-- MySQL 8.0+ için uyumlu

-- Veritabanı oluştur
CREATE DATABASE IF NOT EXISTS sporthub_courts
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Veritabanını kullan
USE sporthub_courts;

-- Veritabanı oluşturuldu mesajı
SELECT 'SportHub Courts database created successfully!' AS message;

-- Mevcut tabloları göster (Spring Boot JPA ile oluşturulacak)
SHOW TABLES;

-- Veritabanı bilgilerini göster
SELECT 
    SCHEMA_NAME as database_name,
    DEFAULT_CHARACTER_SET_NAME as charset,
    DEFAULT_COLLATION_NAME as collation
FROM INFORMATION_SCHEMA.SCHEMATA 
WHERE SCHEMA_NAME = 'sporthub_courts';

-- Kullanıcı yetkilerini kontrol et
SHOW GRANTS FOR CURRENT_USER();
