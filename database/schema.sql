-- Employee Management System - MySQL Schema
-- Beginner-friendly, simple relational model.
-- Run this file in MySQL to create tables.

CREATE DATABASE IF NOT EXISTS employee_ms
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE employee_ms;

-- Departments table
CREATE TABLE IF NOT EXISTS departments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_departments_name (name)
);

-- Employees table
-- photo_url stores a backend-served URL like /api/employees/photos/{file}
CREATE TABLE IF NOT EXISTS employees (
  id BIGINT NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(150) NOT NULL,
  email VARCHAR(180) NOT NULL,
  phone VARCHAR(30),
  department_id BIGINT NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  photo_url VARCHAR(300),
  password_hash VARCHAR(255),
  reset_token VARCHAR(255),
  reset_token_expiry TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uk_employees_email (email),

  CONSTRAINT fk_employees_department
    FOREIGN KEY (department_id) REFERENCES departments(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

-- Seeded admin users for simple session-based authentication.
-- For a fresher project, we keep it readable.
-- Password is stored as BCrypt hash.
CREATE TABLE IF NOT EXISTS admin_users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(60) NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  email VARCHAR(180) NOT NULL,
  reset_token VARCHAR(255),
  reset_token_expiry TIMESTAMP NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_users_username (username),
  UNIQUE KEY uk_admin_users_email (email)
);

-- Salaries table
CREATE TABLE IF NOT EXISTS salaries (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  period VARCHAR(60),
  status VARCHAR(40),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_salaries_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Leave requests
CREATE TABLE IF NOT EXISTS leave_requests (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  start_date DATE,
  end_date DATE,
  reason VARCHAR(500),
  status VARCHAR(40),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Attendances
CREATE TABLE IF NOT EXISTS attendances (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  date DATE,
  status VARCHAR(40),
  note VARCHAR(250),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Issue notices
CREATE TABLE IF NOT EXISTS issue_notices (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  message TEXT,
  priority VARCHAR(20),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- Employee requests
CREATE TABLE IF NOT EXISTS employee_requests (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(1000),
  status VARCHAR(40),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_request_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE ON UPDATE CASCADE
);
