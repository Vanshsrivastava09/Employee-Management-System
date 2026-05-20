-- Employee Management System - Sample Data
-- Run after schema.sql

USE employee_ms;

-- Departments
INSERT INTO departments (name)
SELECT 'Engineering' FROM dual
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE name='Engineering');

INSERT INTO departments (name)
SELECT 'Human Resources' FROM dual
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE name='Human Resources');

INSERT INTO departments (name)
SELECT 'Marketing' FROM dual
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE name='Marketing');

-- Employees
-- Note: department_id is resolved using department name for portability.

INSERT INTO employees (full_name, email, phone, department_id, is_active, photo_url)
SELECT
  'Aarav Kumar',
  'aarav.kumar@example.com',
  '9876543210',
  d.id,
  1,
  NULL
FROM departments d
WHERE d.name = 'Engineering'
  AND NOT EXISTS (SELECT 1 FROM employees WHERE email='aarav.kumar@example.com');

INSERT INTO employees (full_name, email, phone, department_id, is_active, photo_url)
SELECT
  'Meera Sharma',
  'meera.sharma@example.com',
  '9123456780',
  d.id,
  1,
  NULL
FROM departments d
WHERE d.name = 'Human Resources'
  AND NOT EXISTS (SELECT 1 FROM employees WHERE email='meera.sharma@example.com');

INSERT INTO employees (full_name, email, phone, department_id, is_active, photo_url)
SELECT
  'Rohan Verma',
  'rohan.verma@example.com',
  '9988776655',
  d.id,
  0,
  NULL
FROM departments d
WHERE d.name = 'Marketing'
  AND NOT EXISTS (SELECT 1 FROM employees WHERE email='rohan.verma@example.com');

-- Admin users:
-- Admin is seeded in backend startup using BCrypt hash generation (readable + beginner-friendly).
-- If you want to seed here, generate a BCrypt hash and insert into admin_users.
-- For simplicity, we omit admin_users rows from data.sql.

-- Sample salaries
INSERT INTO salaries (employee_id, amount, period, status)
SELECT e.id, 50000.00, '2026-05', 'PENDING' FROM employees e WHERE e.email='aarav.kumar@example.com' AND NOT EXISTS (SELECT 1 FROM salaries s WHERE s.employee_id = e.id AND s.period='2026-05');

INSERT INTO salaries (employee_id, amount, period, status)
SELECT e.id, 45000.00, '2026-05', 'PAID' FROM employees e WHERE e.email='meera.sharma@example.com' AND NOT EXISTS (SELECT 1 FROM salaries s WHERE s.employee_id = e.id AND s.period='2026-05');

-- Sample leave requests
INSERT INTO leave_requests (employee_id, start_date, end_date, reason, status)
SELECT e.id, '2026-05-10', '2026-05-12', 'Medical leave', 'APPROVED' FROM employees e WHERE e.email='aarav.kumar@example.com' AND NOT EXISTS (SELECT 1 FROM leave_requests l WHERE l.employee_id=e.id AND l.start_date='2026-05-10');

INSERT INTO leave_requests (employee_id, start_date, end_date, reason, status)
SELECT e.id, '2026-06-01', '2026-06-03', 'Personal work', 'PENDING' FROM employees e WHERE e.email='meera.sharma@example.com' AND NOT EXISTS (SELECT 1 FROM leave_requests l WHERE l.employee_id=e.id AND l.start_date='2026-06-01');

-- Sample attendances
INSERT INTO attendances (employee_id, date, status, note)
SELECT e.id, CURDATE(), 'PRESENT', 'Checked in via web' FROM employees e WHERE e.email='aarav.kumar@example.com' AND NOT EXISTS (SELECT 1 FROM attendances a WHERE a.employee_id=e.id AND a.date=CURDATE());

-- Sample notices
INSERT INTO issue_notices (title, message, priority)
SELECT 'Payroll update', 'Payroll for May will be processed on 28th May.', 'MEDIUM' FROM dual WHERE NOT EXISTS (SELECT 1 FROM issue_notices WHERE title='Payroll update');

INSERT INTO issue_notices (title, message, priority)
SELECT 'Holiday Notice', 'Office closed on 2nd June for public holiday.', 'LOW' FROM dual WHERE NOT EXISTS (SELECT 1 FROM issue_notices WHERE title='Holiday Notice');

-- Sample employee requests
INSERT INTO employee_requests (employee_id, title, description, status)
SELECT e.id, 'Desk replacement', 'Requesting replacement of damaged desk', 'OPEN' FROM employees e WHERE e.email='rohan.verma@example.com' AND NOT EXISTS (SELECT 1 FROM employee_requests r WHERE r.employee_id=e.id AND r.title='Desk replacement');
