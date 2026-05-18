# TODO - Employee Management System (Full Stack)

## Step 1: Database
- [ ] Create `database/schema.sql` (tables: departments, employees, admins)
- [ ] Create `database/data.sql` (sample departments + employee records + seeded admin)

## Step 2: Backend (Spring Boot)
- [ ] Create `backend/pom.xml` with Spring Boot Web + Validation + JPA + MySQL + Lombok (optional) + DevTools (optional)
- [ ] Create Spring Boot project structure:
  - [ ] Entities: `Employee`, `Department`, `AdminUser`
  - [ ] Repositories: `EmployeeRepository`, `DepartmentRepository`, `AdminUserRepository`
  - [ ] Services: `EmployeeService`, `AuthService`, `AnalyticsService`, `CsvExportService`
  - [ ] Controllers: `AuthController`, `EmployeeController`, `DepartmentController`, `AnalyticsController`
  - [ ] DTOs + request/response models
  - [ ] Global exception handling (`@ControllerAdvice`)
  - [ ] CORS config for frontend
  - [ ] Session-based authentication endpoints

- [ ] Create application configuration `backend/src/main/resources/application.properties`

## Step 3: Frontend (HTML/CSS/JS)
- [ ] Create `frontend/` structure:
  - [ ] Pages: `index.html`, `dashboard.html`, `employees.html`
  - [ ] Shared assets: `styles.css`, `app.js`, `api.js`, `auth.js`, `employees.js`
- [ ] Implement UI features:
  - [ ] Login validation (JS)
  - [ ] Sidebar navigation + responsive layout
  - [ ] Dark/light mode toggle
  - [ ] Employee CRUD (add/edit/delete) with forms & validation
  - [ ] Search + filter by department + pagination
  - [ ] Toast notifications + loading animations
  - [ ] Profile image upload (preview + upload request)
  - [ ] CSV export button

## Step 4: Documentation
- [ ] Create root `README.md`:
  - [ ] Local setup instructions (MySQL + backend + frontend)
  - [ ] Environment variables
  - [ ] API documentation (endpoints + example payloads)
  - [ ] Deployment guide (Netlify/Vercel for frontend, Render for backend, MySQL config)
  - [ ] Seeded admin credentials and login flow explanation
- [ ] Add `docs/` notes (optional) like screenshot placeholders

## Step 5: Verification
- [ ] Run backend build/compile (verify no compilation errors)
- [ ] Quick manual API checks via browser (or simple curl) for login + CRUD
- [ ] Ensure CORS and session cookies work correctly
