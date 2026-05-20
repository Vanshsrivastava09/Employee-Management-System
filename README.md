<<<<<<< HEAD
# Employee Management System (EMS HR)

A modern Employee Management System (HRMS) developed using Java Spring Boot, HTML, CSS, JavaScript, and MySQL. The project simulates a real-world HR dashboard with modules for employee management, departments, attendance, leaves, salary management, and admin operations.

## Live Demo

🔗 Live Project: https://employee-management-system-09.vercel.app/

## Features

* Employee Management
* Department Management
* Attendance Tracking
* Leave Management
* Salary Management
* HR Dashboard Analytics
* Responsive Admin Panel
* CRUD Operations
* REST API Architecture
* Modern HRMS User Interface

## Tech Stack

### Frontend

* HTML5
* CSS3
* JavaScript

### Backend

* Java
* Spring Boot
* REST APIs
* Gradle

### Database

* MySQL

## Modules

### Dashboard

* Employee statistics
* Department overview
* Attendance insights
* Request tracking
* Analytics cards

### Employees

* Add employees
* Edit employee records
* Delete employees
* Employee search & filters

### Departments

* Department management
* Employee assignment
* Department analytics

### Attendance

* Attendance tracking
* Attendance records management

### Leaves

* Leave request system
* Leave approval workflow

### Salary Management

* Salary records
* Payroll overview

## Project Structure

```bash id="4f7m9d"
backend/
frontend/
database/
README.md
```

## Installation & Setup

### Clone Repository

```bash id="8t5h1m"
git clone YOUR_GITHUB_REPOSITORY_LINK
```

### Backend Setup

```bash id="p8d3n2"
cd backend
./gradlew build
./gradlew bootRun
```

### Frontend Setup

```bash id="c6w1r4"
cd frontend
python -m http.server 5500
```

Open in browser:

```bash id="d4m2x7"
http://localhost:5500
```

## Deployment

### Frontend Deployment

* Deployed on Vercel

### Backend

* Spring Boot backend architecture with REST APIs

## Screenshots

Add screenshots of:

* Dashboard
* Employees Page
* Departments Page
* Login Page

## Future Enhancements

* JWT Authentication
* Role-Based Access Control
* Email Notifications
* Cloud Database Integration
* Advanced Analytics Dashboard

## Skills Learned

* Full Stack Web Development
* REST API Integration
* Spring Boot Backend Development
* Database Management
* Frontend UI Development
* CRUD Operations
* Git & GitHub Workflow
* Deployment using Vercel

## Author

Vansh Srivastava
B.Tech CSCE Student | Java Full Stack & Data Analytics Enthusiast
=======
# Employee Management System

Beginner-friendly Java Full Stack project for a fresher interview portfolio.

- Backend: Spring Boot 3.3, Gradle Wrapper, Java 17-compatible code
- Frontend: HTML, CSS, JavaScript
- Database: MySQL
- Auth: simple admin login with `HttpSession`
- UI: HRMS-style college major project admin panel

## Corrected Project Structure

```text
Employee-Management System/
  backend/
    build.gradle
    settings.gradle
    gradlew
    gradlew.bat
    gradle/wrapper/
      gradle-wrapper.jar
      gradle-wrapper.properties
    src/main/java/com/example/employeems/
      EmployeeMsApplication.java
      config/
        AuthFilter.java
        CorsConfig.java
        DataSeeder.java
        SecurityConfig.java
      controller/
        AuthController.java
        DepartmentController.java
        EmployeeController.java
      dto/
      entity/
        AdminUser.java
        Department.java
        Employee.java
      exception/
      repository/
      service/
      util/
    src/main/resources/application.properties
  database/
    schema.sql
    data.sql
  frontend/
    index.html
    signup.html
    employee-login.html
    dashboard.html
    employees.html
    departments.html
    app.js
    api.js
    auth.js
    employees.js
    departments.js
    styles.css
```

`pom.xml` was removed so VS Code and the Java extensions treat the backend as one clean Gradle project.

## What Was Fixed

- Restored proper Gradle wrapper files, including `gradle-wrapper.jar`.
- Replaced the broken wrapper scripts with standard Gradle-generated scripts.
- Removed Maven/Gradle conflict by deleting `backend/pom.xml`.
- Changed Java config so Java 21 on your PC can compile Java 17-compatible bytecode.
- Removed unnecessary Lombok config because the project uses normal getters/setters.
- Removed incorrect Spring Session JDBC config. The project now uses default servlet `HttpSession`.
- Added `SecurityConfig` with a BCrypt `PasswordEncoder` bean.
- Added `DataSeeder` to create demo admin, departments, and employee records.
- Fixed frontend login form JavaScript.
- Fixed frontend HTML escaping helper.
- Rebuilt the frontend as a practical HRMS UI: login, signup, dashboard widgets, employees table, department cards, responsive sidebar, toasts, loaders, and custom delete confirmation.
- Added `POST /api/departments` so departments can be created from the UI.

## Database Setup

Start MySQL first.

Option A: let Spring Boot create/update tables automatically:

```powershell
cd "C:\Users\KIIT0001\Desktop\Employee-Management System\backend"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="your_mysql_password"
.\gradlew.bat bootRun
```

Option B: manually create schema and sample data:

```powershell
mysql -u root -p < "..\database\schema.sql"
mysql -u root -p < "..\database\data.sql"
```

The app uses:

```properties
spring.datasource.url=${MYSQL_URL:jdbc:mysql://localhost:3306/employee_ms?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:}
```

Default login:

```text
username: admin
password: admin123
```

## Run Backend In VS Code

Open this folder in VS Code:

```powershell
cd "C:\Users\KIIT0001\Desktop\Employee-Management System"
code .
```

Then run:

```powershell
cd backend
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="your_mysql_password"
.\gradlew.bat bootRun
```

Backend URL:

```text
http://localhost:8080
```

Build check:

```powershell
cd backend
.\gradlew.bat clean build
```

## Run Frontend

Use a static server so browser cookies work correctly.

With VS Code Live Server:

1. Install the "Live Server" extension.
2. Right-click `frontend/index.html`.
3. Click "Open with Live Server".
4. Use `http://localhost:5500/frontend/index.html` or the URL shown by Live Server.

With Python:

```powershell
cd "C:\Users\KIIT0001\Desktop\Employee-Management System\frontend"
python -m http.server 5500
```

Then open:

```text
http://localhost:5500/index.html
```

Screens to check:

- `http://localhost:5500/index.html` - HR login
- `http://localhost:5500/signup.html` - HR signup UI
- `http://localhost:5500/employee-login.html` - employee login UI
- `http://localhost:5500/dashboard.html` - dashboard
- `http://localhost:5500/employees.html` - employee CRUD
- `http://localhost:5500/departments.html` - department page

## Important Backend Files

- Controller: receives HTTP requests and returns JSON APIs.
- Service: contains business logic and validation.
- Repository: talks to MySQL using Spring Data JPA.
- Entity: maps Java classes to database tables.
- DTO: request/response objects used by the frontend.
- `DataSeeder`: inserts default demo data at startup.
- `AuthFilter`: protects `/api/**` routes except `/api/auth/**`.

## API Quick Test

Login:

```powershell
curl -i -c cookies.txt -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" http://localhost:8080/api/auth/login
```

Get employees:

```powershell
curl -b cookies.txt http://localhost:8080/api/employees
```

New HRMS module APIs (created):

- Salaries: `GET /api/salaries`, `GET /api/salaries/{id}`, `POST /api/salaries` (body: employeeId, amount, period, status), `PUT /api/salaries/{id}`, `DELETE /api/salaries/{id}`
- Leaves: `GET /api/leaves`, `GET /api/leaves/{id}`, `POST /api/leaves` (body: employeeId, startDate, endDate, reason), `PUT /api/leaves/{id}/status` (body: status), `DELETE /api/leaves/{id}`
- Attendances: `GET /api/attendances`, `GET /api/attendances/{id}`, `POST /api/attendances` (body: employeeId, date, status, note), `PUT /api/attendances/{id}`, `DELETE /api/attendances/{id}`
- Issue Notices: `GET /api/notices`, `GET /api/notices/{id}`, `POST /api/notices`, `PUT /api/notices/{id}`, `DELETE /api/notices/{id}`
- Requests: `GET /api/requests`, `GET /api/requests/{id}`, `POST /api/requests` (body: employeeId, title, description), `PUT /api/requests/{id}/status` (body: status), `DELETE /api/requests/{id}`

Frontend pages added:

- `frontend/salaries.html` and `frontend/salaries.js`
- `frontend/notices.html` and `frontend/notices.js`
- `frontend/leaves.html` and `frontend/leaves.js`
- `frontend/attendances.html` and `frontend/attendances.js`
- `frontend/requests.html` and `frontend/requests.js`

Routing / Sidebar:

- Sidebar links in `frontend/dashboard.html` and other pages link to the new pages.
- Active sidebar state is handled in `frontend/app.js` by comparing the current path to link hrefs.


Create department:

```powershell
curl -b cookies.txt -H "Content-Type: application/json" -d "{\"name\":\"Project K\"}" http://localhost:8080/api/departments
```

## Common Error Fixes

`Access denied for user 'root'@'localhost'`:

```powershell
$env:MYSQL_PASSWORD="your_actual_mysql_password"
.\gradlew.bat bootRun
```

`java command could not be found`:

- Install JDK 17 or JDK 21.
- Restart VS Code.
- Run `java -version`.

Port `8080` already in use:

```properties
server.port=8081
```

Frontend cannot login:

- Backend must be running on `http://localhost:8080`.
- Frontend must be served from `http://localhost:5500`.
- Check `app.cors.allowed-origins` in `application.properties`.

Gradle download blocked:

- Run VS Code terminal as normal user with internet enabled.
- Run `.\gradlew.bat clean build` once so dependencies are cached.

## Deployment Notes

Backend:

1. Build with `.\gradlew.bat clean bootJar`.
2. Deploy `backend/build/libs/employee-management-system-1.0.0.jar`.
3. Set production environment variables:
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
   - `MYSQL_URL`
4. Set CORS to your deployed frontend domain.

Frontend:

1. Deploy the `frontend/` folder to Netlify, Vercel, or any static hosting.
2. Update `frontend/app.js` `API_BASE` to the deployed backend URL.

For production, change `spring.jpa.hibernate.ddl-auto` from `update` to `validate` after the schema is stable.

## Interview Explanation

This project follows a simple layered Spring Boot architecture. Controllers expose REST APIs, services contain business logic, repositories handle MySQL operations through Spring Data JPA, and entities map Java classes to database tables. The frontend is a plain HTML/CSS/JavaScript HRMS admin panel with login, dashboard widgets, employee CRUD, department management, responsive layout, validation messages, loaders, toasts, and confirmation modals.

## Resume-Ready Description

Employee Management System - Built a full stack HRMS web application using Spring Boot, Gradle, MySQL, HTML, CSS, and JavaScript. Implemented session-based HR admin login, employee CRUD, department management, dashboard analytics, REST APIs, JPA repositories, sample data seeding, responsive enterprise UI, and deployment-ready configuration.

## GitHub Upload

```powershell
cd "C:\Users\KIIT0001\Desktop\Employee-Management System"
git init
git add .
git commit -m "Build HRMS employee management system"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/employee-management-system.git
git push -u origin main
```
>>>>>>> 748d43d (feat(hrms): add HRMS modules and frontend (local repo))
