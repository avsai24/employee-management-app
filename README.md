# Employee Management App

Full-stack assessment app: **Angular 19 + Spring Boot + MySQL**.

## Prerequisites

- Node.js 20+ / npm
- Java 17 (`/usr/libexec/java_home -v 17`)
- Maven 3.9+
- Docker Desktop

## Quick start

```bash
# 1) Start MySQL (port 3307 — host 3306 may already be in use)
docker compose up -d

# 2) Backend
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw spring-boot:run

# 3) Frontend (new terminal)
cd frontend
npm start
```

- Frontend: http://localhost:4200  
- Backend: http://localhost:8080  
- MySQL: `localhost:3307` / db `employee_db` / user `empuser` / pass `emppass`

## Project layout

```
employee-management-app/
├── frontend/          # Angular 19 + Material
├── backend/           # Spring Boot REST API
├── docker-compose.yml # MySQL 8
└── AI_USAGE_LOG.md    # Required AI usage documentation
```
