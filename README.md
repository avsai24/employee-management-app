# Employee Management App

Full-stack assessment app: **Angular 19** + **Spring Boot 4.2** + **MySQL 8**.

Use it to manage employees (CRUD), view a dashboard, and (as admin) filter/export reports to Excel or PDF.

---

## What you get

| Area | Details |
|------|---------|
| Auth | JWT login/logout, access + refresh tokens, remember username, AuthGuard, HTTP interceptor |
| Roles | `ROLE_ADMIN` → full CRUD + Reports · `ava.patel` (`ROLE_USER`) → read-only own profile (EMP001 Ava Patel) |
| Session | 30-minute idle timeout with warning before logout |
| Employees | Search, sort, server-side pagination (10/page), add/edit/detail, soft delete + confirm |
| Employment | Full-time / Contract / Intern · inactive requires reason · end-date rules by type |
| Reports | Date/status/department filters, summary cards, Excel + PDF export (admin) |
| Docs | This README + `AI_USAGE_LOG.md` for assessment AI documentation |

---

## Prerequisites

Install before running:

1. **Node.js 20+** (includes npm) — https://nodejs.org  
2. **Java 17**  
3. **Docker Desktop** (for MySQL)  
4. Git (optional)

Check in a terminal:

```bash
node -v
npm -v
java -version
docker -v
```

On macOS, point Maven/Spring at Java 17:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

You do **not** need a global Maven install — the project includes `./mvnw`.

---

## Quick start (3 terminals)

From the project root: `employee-management-app/`

### 1) Start MySQL

```bash
docker compose up -d
```

| Setting | Value |
|---------|--------|
| Host port | **3307** (container 3306) |
| Database | `employee_db` |
| User | `empuser` |
| Password | `emppass` |

Port **3307** avoids clashes with a local MySQL already on 3306.

### 2) Start backend

```bash
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
./mvnw spring-boot:run
```

Wait for: `Started EmployeeManagementApplication`  
API base: http://localhost:8080  

First boot seeds admin + Ava Patel self-service accounts and sample employees.

### 3) Start frontend

```bash
cd frontend
npm install
npm start
```

App: http://localhost:4200  

---

## Demo logins

| Username | Password | Role | Access |
|----------|----------|------|--------|
| `admin` | `admin123` | `ROLE_ADMIN` | Full employee CRUD + **Reports** |
| `ava.patel` | `ava123` | `ROLE_USER` | Read-only **My Profile** for Ava Patel (EMP001). No add/edit/delete. No Reports |

Tip: log in as both to demo admin vs employee self-service.

---

## Smoke checklist

- [ ] Login with `admin` / `admin123`  
- [ ] Bad password shows an error  
- [ ] Employees: search, add, edit, delete (confirm dialog)  
- [ ] Reports: filter + download Excel and PDF  
- [ ] Login as `ava.patel` / `ava123` → My Profile only, Reports hidden  
- [ ] Logout → protected routes redirect to login  

---

## Architecture (simple)

```text
Browser (Angular :4200)
    → REST JSON + JWT
Spring Boot API (:8080)
    → JPA / Hibernate
MySQL (:3307)
```

| Layer | Tech |
|-------|------|
| Frontend | Angular 19 standalone, Material, Reactive Forms |
| Backend | Spring Boot 4.2, Security, Validation, Data JPA |
| Auth | BCrypt passwords, JWT access (1h) + refresh (7d) |
| Reports | Apache POI (Excel), OpenPDF (PDF) |
| DB | MySQL 8 via Docker Compose |

---

## API overview

| Method | Path | Notes |
|--------|------|--------|
| POST | `/api/auth/login` | Returns access + refresh JWT |
| POST | `/api/auth/refresh` | New tokens from refresh token |
| POST | `/api/auth/logout` | Client clears tokens (stateless JWT) |
| GET/POST/PUT/DELETE | `/api/employees` | CRUD (soft delete on DELETE) |
| GET | `/api/reports/employees` | Filtered page — **ADMIN** |
| GET | `/api/reports/employees/summary` | Aggregates (authenticated) |
| GET | `/api/reports/employees/export/excel` | Excel — **ADMIN** |
| GET | `/api/reports/employees/export/pdf` | PDF — **ADMIN** |

Send authenticated requests with:

```http
Authorization: Bearer <access_token>
```

---

## Tests

```bash
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw test
```

---

## Project layout

```text
employee-management-app/
├── frontend/              # Angular 19 + Material
│   └── src/app/
│       ├── core/          # auth, employees, reports services
│       ├── features/      # login, dashboard, employees, reports
│       └── layout/        # top nav shell
├── backend/               # Spring Boot REST API
│   └── src/main/java/com/assessment/employee/
│       ├── controller/    # Auth, Employee, Report
│       ├── service/       # business rules
│       ├── security/      # JWT filter + SecurityConfig
│       ├── entity/        # User, Employee
│       └── config/        # seed data
├── docker-compose.yml     # MySQL 8
├── AI_USAGE_LOG.md        # AI usage documentation (assessment)
└── README.md
```

---

## Troubleshooting

| Problem | What to try |
|---------|-------------|
| `Connection refused` to MySQL | Start Docker Desktop, then `docker compose up -d` |
| Port 8080 in use | Stop the other Java process using 8080 |
| Port 4200 in use | `npm start -- --port 4201` |
| Wrong Java version | Use Java **17** (`JAVA_HOME`) |
| Reports empty for “this month” | Widen the date filter; seed data may fall outside the default month |
| Stuck logged-in state | Logout, or clear site data for localhost:4200, then login again |
| `ava.patel` hits report URLs | Expected UI hide; API should return **403** for report list/exports |

---

## Assessment notes

- Keep **`AI_USAGE_LOG.md`** updated (tool, prompt, AI output, your changes, what you learned).  
- Prefer understanding the flow: **login → JWT → interceptor → API → MySQL**.  
- Soft delete means records are marked deleted, not physically removed.
