# Employee Management App

Assessment full-stack app built with **Angular 19**, **Spring Boot 4**, and **MySQL**.

## Features

- JWT login / logout with route guard + HTTP interceptor
- Remember-me username
- 30-minute idle timeout with warning
- Employee CRUD (search, sort, server-side pagination, soft delete)
- Reports with date/status/department filters, summary cards, Excel + PDF export
- AI usage log for assessment documentation

## Prerequisites

- Node.js 20+ / npm
- Java 17 (`export JAVA_HOME=$(/usr/libexec/java_home -v 17)`)
- Maven 3.9+ (or use `./mvnw`)
- Docker Desktop

## Quick start

```bash
# 1) MySQL on host port 3307 (3306 may already be used locally)
docker compose up -d

# 2) Backend
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw spring-boot:run

# 3) Frontend (new terminal)
cd frontend
npm install
npm start
```

| Service  | URL / connection                                      |
|----------|--------------------------------------------------------|
| Frontend | http://localhost:4200                                  |
| Backend  | http://localhost:8080                                  |
| MySQL    | `localhost:3307` / `employee_db` / `empuser` / `emppass` |

### Demo login

- Username: `admin`
- Password: `admin123`

## API overview

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Authenticate, return JWT |
| POST | `/api/auth/logout` | Client logout helper |
| GET/POST/PUT/DELETE | `/api/employees` | Employee CRUD |
| GET | `/api/reports/employees` | Filtered report page |
| GET | `/api/reports/employees/summary` | Aggregate summary |
| GET | `/api/reports/employees/export/excel` | Excel download |
| GET | `/api/reports/employees/export/pdf` | PDF download |

## Tests

```bash
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw test
```

## Project layout

```
employee-management-app/
├── frontend/           # Angular 19 + Material
├── backend/            # Spring Boot REST API
├── docker-compose.yml  # MySQL 8
├── AI_USAGE_LOG.md     # Required AI usage documentation
└── README.md
```

## Notes

- Use Java 17 for the backend even if a newer JDK is installed.
- MySQL is mapped to **3307** to avoid clashing with a local MySQL on 3306.
- Keep `AI_USAGE_LOG.md` updated for the assessment scoring rubric.
