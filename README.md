# Employee Management System (EMS HR)

A modern Employee Management System (HRMS) developed using Java Spring Boot, HTML, CSS, JavaScript, and MySQL. The project simulates a real-world HR dashboard with modules for employee management, departments, attendance, leaves, salary management, and admin operations.

## Live Demo

🔗 Live Project: https://employee-management-system-09.vercel.app/

## Features

* **Public Landing Page** - Modern marketing site with hero, about, and features sections
* **Employee Management** - Complete employee profiles with department assignments
* **Department Management** - Organize employees by departments
* **Attendance Tracking** - Daily attendance records with analytics
* **Leave Management** - Streamlined leave request workflow
* **Salary Management** - Salary records and payment tracking
* **HR Dashboard Analytics** - Real-time insights and metrics
* **Password Reset Flow** - Secure forgot password for admins and employees
* **Responsive Admin Panel** - Mobile-friendly interface
* **CRUD Operations** - Full create, read, update, delete functionality
* **REST API Architecture** - Well-structured backend endpoints
* **Modern UI Design** - Consistent design system with indigo/purple theme

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

* MySQL (production) / H2 (development)

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

```bash
backend/
frontend/
database/
README.md
```

## Installation & Setup

### Clone Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_LINK
```

### Backend Setup

```bash
cd backend
./gradlew build
./gradlew bootRun
```

### Frontend Setup

```bash
cd frontend
python -m http.server 5500
```

Open in browser:

```bash
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

---

## Development Setup

### Quick Start (H2 Database)

For development, the app uses H2 in-memory database by default:

```bash
cd backend
./gradlew bootRun
```

The backend will start on `http://localhost:8080` with H2 database (no MySQL required).

### Production Setup (MySQL)

For production deployment with MySQL:

1. Set environment variables:
   ```bash
   export MYSQL_URL=jdbc:mysql://localhost:3306/employee_ms
   export MYSQL_USER=root
   export MYSQL_PASSWORD=your_password
   export ADMIN_USERNAME=admin
   export ADMIN_PASSWORD=your_admin_password
   ```

2. Run with production profile:
   ```bash
   cd backend
   ./gradlew bootRun --args='--spring.profiles.active=prod'
   ```

### Default Credentials

- **Username:** admin
- **Password:** admin123

### Frontend Setup

```bash
cd frontend
python -m http.server 5500
```

Open `http://localhost:5500/index.html` in your browser.

## Security Features

- BCrypt password hashing for all passwords
- Session-based authentication with 30-minute timeout
- Input validation using Jakarta Validation API
- Rate limiting (5 failed login attempts = 15-minute lockout)
- CORS configuration with restricted headers and methods
- Server-side error logging with SLF4J
- Environment-based configuration for secrets
- Global exception handler to prevent stack trace leakage
- Health check endpoint for monitoring
- Secure password reset flow with time-limited tokens
- Email enumeration protection

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout
- `GET /api/auth/me` - Check session status
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password with token
- `GET /api/health` - Health check

### Employees
- `GET /api/employees` - List employees
- `POST /api/employees` - Create employee
- `GET /api/employees/{id}` - Get employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

### Departments
- `GET /api/departments` - List departments
- `POST /api/departments` - Create department

### Other Modules
- `GET/POST/PUT/DELETE /api/attendances` - Attendance management
- `GET/POST/PUT/DELETE /api/leaves` - Leave requests
- `GET/POST/PUT/DELETE /api/salaries` - Salary records
- `GET/POST/PUT/DELETE /api/notices` - Issue notices
- `GET/POST/PUT/DELETE /api/requests` - Employee requests

## Deployment Checklist

Before deploying to production:

### 1. Environment Variables
Set these environment variables in your production environment:
- `MYSQL_URL` - MySQL connection string (e.g., `jdbc:mysql://your-host:3306/employee_ms`)
- `MYSQL_USER` - Database username
- `MYSQL_PASSWORD` - Database password (use strong password)
- `ADMIN_USERNAME` - Admin username (change from default)
- `ADMIN_PASSWORD` - Admin password (use strong password, minimum 12 characters)
- `ADMIN_EMAIL` - Admin email for password reset
- `SMTP_HOST` - SMTP server host (e.g., `smtp.gmail.com`)
- `SMTP_PORT` - SMTP server port (e.g., `587`)
- `SMTP_USERNAME` - SMTP authentication username
- `SMTP_PASSWORD` - SMTP authentication password
- `SMTP_FROM` - From email address for password reset emails
- `FRONTEND_URL` - Frontend URL for password reset links (e.g., `https://yourdomain.com`)
- `CORS_ALLOWED_ORIGINS` - Frontend domain(s) (comma-separated, e.g., `https://yourdomain.com`)

### 2. Database Setup
- Ensure MySQL 8.0+ is running on your production server
- Create database: `CREATE DATABASE employee_ms;`
- Run `database/schema.sql` to create tables
- Run `database/data.sql` for sample data (optional, for testing)
- Configure proper database backups

### 3. Security Configuration
- Set `spring.profiles.active=prod` in production
- Update CORS origins to production domain only
- Change default admin credentials immediately
- Enable HTTPS/TLS for all endpoints
- Configure firewall rules to restrict database access
- Set up database user with minimal required permissions

### 4. Build & Package
```bash
cd backend
./gradlew clean bootJar
```
This creates `backend/build/libs/employee-ms-*.jar`

### 5. Docker Deployment (Recommended)
```bash
# Build and start with Docker Compose
docker-compose up -d

# Or build individual images
docker build -t employee-ms-backend .
docker run -p 8080:8080 --env-file .env employee-ms-backend
```

### 6. Traditional Deployment
- Deploy `backend/build/libs/*.jar` to your server
- Deploy `frontend/` folder to static hosting (Vercel, Netlify, etc.)
- Update frontend `API_BASE` in `app.js` to production backend URL
- Configure reverse proxy (Nginx/Apache) for SSL termination

### 7. Monitoring & Logging
- Set up log aggregation (ELK, Splunk, etc.)
- Configure error tracking (Sentry, etc.)
- Monitor health check endpoint: `GET /api/health`
- Set up uptime monitoring
- Configure database connection pool monitoring

### 8. Backup Strategy
- Automated daily database backups
- Backup retention policy (e.g., 30 days)
- Test backup restoration process
- Document backup/restore procedures

## Troubleshooting

### Common Issues

**Backend won't start:**
- Check if port 8080 is already in use
- Verify MySQL connection string and credentials
- Check application logs for specific error messages
- Ensure Java 17+ is installed

**Login not working:**
- Verify admin credentials in database
- Check if session timeout is configured correctly
- Ensure CORS origins include your frontend domain
- Check browser console for JavaScript errors

**Database connection errors:**
- Verify MySQL is running and accessible
- Check firewall rules for database port (3306)
- Ensure database user has proper permissions
- Test connection string with MySQL client

**Frontend API calls failing:**
- Check `API_BASE` in `frontend/app.js` matches backend URL
- Verify CORS configuration allows your frontend domain
- Check browser network tab for request/response details
- Ensure backend is running and accessible

## Support & Contributing

For issues, questions, or contributions:
- Open an issue on GitHub
- Check existing documentation
- Review security best practices before deploying

## License

This project is for educational purposes. Please ensure compliance with your organization's security and data handling policies before using in production.
