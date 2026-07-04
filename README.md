# HireHub Backend (2-Role Version)

Job portal backend built with Spring Boot + PostgreSQL. Two roles: **EMPLOYER** and **JOBSEEKER**.

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Security + JWT (jjwt 0.11.5)
- Spring Data JPA + PostgreSQL
- BCrypt password encryption
- Maven

## Architecture (5 layers)
```
Controller  -> handles HTTP requests
Service     -> business logic, ownership checks
Repository  -> Spring Data JPA, talks to DB
Model       -> JPA entities (User, Job, Application)
DTO + Security -> request/response DTOs, JwtUtil, JwtAuthFilter, SecurityConfig
```

## Run Locally

### 1. Create the database
```sql
CREATE DATABASE hirehub;
```

### 2. Set environment variables (or edit application.properties directly)
```
DB_URL=jdbc:postgresql://localhost:5432/hirehub
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=some-long-random-secret-string-at-least-32-characters
```

### 3. Run
```bash
mvn spring-boot:run
```
App starts on `http://localhost:8080`. Tables are auto-created by Hibernate (`ddl-auto=update`).

## API Endpoints

**Auth (public)**
- `POST /auth/register` — body: `{name, email, password, role}` (role: EMPLOYER or JOBSEEKER)
- `POST /auth/login` — body: `{email, password}` → returns JWT token

**Jobs**
- `GET /api/jobs` — public, list all open jobs
- `GET /api/jobs/search?keyword=backend` — public
- `GET /api/jobs/{id}` — public
- `POST /api/jobs` — EMPLOYER only
- `PUT /api/jobs/{id}` — EMPLOYER only (own job)
- `DELETE /api/jobs/{id}` — EMPLOYER only (own job)
- `PATCH /api/jobs/{id}/close` — EMPLOYER only (own job)
- `GET /api/jobs/my` — EMPLOYER only

**Applications**
- `POST /api/applications/{jobId}` — JOBSEEKER only, apply to a job
- `DELETE /api/applications/{id}` — JOBSEEKER only, withdraw own application
- `GET /api/applications/my` — JOBSEEKER only
- `GET /api/applications/job/{jobId}` — EMPLOYER only, view applicants
- `PUT /api/applications/{id}/status` — EMPLOYER only, body: `{status: "SHORTLISTED"}`

For any protected endpoint, send the JWT in the header:
```
Authorization: Bearer <token>
```

## Deploy to Render

1. Push this project to a GitHub repo.
2. On Render: **New → PostgreSQL** — create a free Postgres instance, copy its internal connection details.
3. On Render: **New → Web Service** — connect your GitHub repo. Render will detect the `Dockerfile` automatically.
4. Add environment variables in the Render dashboard:
   - `DB_URL` (from step 2, format: `jdbc:postgresql://<host>:5432/<dbname>`)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET` (any long random string)
5. Deploy. You'll get a live URL like `https://hirehub-api.onrender.com`.

Note: Render's free tier spins down after inactivity — first request after idle time takes ~30-50 seconds to wake up.
