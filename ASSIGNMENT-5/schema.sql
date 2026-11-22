CREATE DATABASE IF NOT EXISTS co5_transport;
USE co5_transport;
CREATE TABLE IF NOT EXISTS bus_pass (
  pass_id VARCHAR(20) PRIMARY KEY,
  register_no VARCHAR(30) NOT NULL,
  holder_name VARCHAR(100) NOT NULL,
  route_no VARCHAR(20) NOT NULL,
  boarding_stop VARCHAR(100) NOT NULL,
  validity_date DATE NOT NULL,
  amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0)
);
CREATE TABLE IF NOT EXISTS student (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  register_no VARCHAR(30) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150)
);
CREATE TABLE IF NOT EXISTS wallet (
  wallet_id BIGINT PRIMARY KEY,
  balance DECIMAL(12,2) NOT NULL CHECK (balance >= 0)
);
CREATE TABLE IF NOT EXISTS app_user (
  username VARCHAR(80) PRIMARY KEY,
  password_hash VARCHAR(255) NOT NULL
);
