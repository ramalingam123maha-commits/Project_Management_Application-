# ProjectFlow – Project Plan & Checklist

## 1. Requirements (from the user's request)
- Full-stack project & task management app named **ProjectFlow**
- Register, login, create projects, add team members, assign tasks, track progress, dashboards
- Roles: **Admin**, **Project Manager**, **Member** with appropriate permissions
- Stack: **React + TypeScript** (frontend), **Java Spring Boot** (backend), **MySQL** (database)
- Input validation, error handling, authentication, authorization, responsive UI, automated tests, setup docs
- Every created item logged in `Worklog.md`; every decision logged in `decision.md`

## 2. Languages & Technologies
| Layer | Language | Frameworks / Tools |
|---|---|---|
| Frontend | TypeScript, TSX, HTML, CSS | React 18, Vite 5, React Router 6, TanStack Query 5, Axios, react-hook-form, Zod, Tailwind CSS 3 |
| Frontend tests | TypeScript | Vitest, React Testing Library |
| Backend | Java 17 | Spring Boot 3.3 (Web, Security, Data JPA, Validation), jjwt, Maven |
| Backend tests | Java | JUnit 5, Mockito, MockMvc, H2 |
| Database | SQL | MySQL 8, Flyway migrations |
| Config | YAML, JSON, XML | `application.yml`, `package.json`, `pom.xml` |
| DevOps | Dockerfile, YAML | Docker, Docker Compose, Nginx |
| Docs | Markdown | Readme, Worklog, decision, Plan |

## 3. Procedure Followed So Far
1. **Analysed requirements** from your first message (features, roles, stack, quality needs).
2. **Inspected the repository** – it contained only `Readme.md`.
3. **Designed the architecture** – 3-tier (React SPA → Spring Boot REST → MySQL), role matrix, data model, API endpoints, file responsibilities.
4. **Per your comment:** created `Worklog.md` (activity log) and `decision.md` (decision log), recorded decisions D-001 → D-014.
5. **Per your comment ("not create anything"):** presented the full architecture without writing code.
6. **Per your comment ("folders and files first"):** created all folders and 124 **empty** files; logged in Worklog #4–#6 and decision D-015.
7. **Per your comment:** created this `Plan.md` with the procedure and checklist.

## 4. Checklist

### Phase 0 – Planning & Documentation
- [x] Analyse requirements
- [x] Design architecture, data model, role matrix, API
- [x] Create `Worklog.md`
- [x] Create `decision.md`
- [x] Create `Plan.md` (this file)

### Phase 1 – Scaffolding
- [x] Create backend folder structure & empty files
- [x] Create frontend folder structure & empty files
- [x] Create root files (`docker-compose.yml`, `.env.example`, `.gitignore`)

### Phase 2 – Backend Foundation
- [x] `pom.xml` dependencies
- [x] `application.yml` (env-driven config)
- [x] `V1__init.sql` Flyway schema
- [x] Entities & enums (`domain/`)
- [x] Repositories (`repository/`)

### Phase 3 – Backend Security
- [x] `JwtService`, `JwtAuthenticationFilter`, `UserPrincipal`, `CustomUserDetailsService`
- [x] `RestAuthEntryPoint` (JSON 401)
- [x] `SecurityConfig` (stateless, CORS, BCrypt, route rules)
- [x] `AdminSeeder` (first admin from env vars)

### Phase 4 – Backend Features
- [x] DTOs with Bean Validation + `DtoMapper`
- [x] Exceptions + `GlobalExceptionHandler` + `ApiError`
- [x] `AuthService` / `AuthController` (register, login, me)
- [x] `UserService` / `UserController` (admin role management)
- [x] `ProjectService` / `ProjectController` (CRUD + members)
- [x] `TaskService` / `TaskController` (CRUD, assign, status)
- [x] `CommentService` (task comments)
- [x] `DashboardService` / `DashboardController` (role-scoped stats)

### Phase 5 – Backend Tests
- [x] `JwtServiceTest`
- [x] Service unit tests (Auth, Project, Task)
- [x] Controller integration tests (auth, validation, permission matrix)
- [x] All backend tests passing (`mvn test`)

### Phase 6 – Frontend Foundation
- [x] `package.json`, `tsconfig.json`, `vite.config.ts`, Tailwind/PostCSS, `index.html`
- [x] `types/index.ts`
- [x] `api/client.ts` + resource API modules
- [x] `AuthContext`, `ProtectedRoute`, `RoleGuard`, `usePermissions`
- [x] `validation/schemas.ts`, `utils/errors.ts`

### Phase 7 – Frontend UI
- [x] Layout (AppLayout, Navbar, Sidebar – responsive)
- [x] Common components
- [x] Login & Register pages
- [x] Dashboard page + widgets
- [x] Projects list / detail + members management
- [x] Task board (Kanban), task form, task detail, comments
- [x] My Tasks page, Admin Users page, 404 page

### Phase 8 – Frontend Tests
- [x] LoginPage, ProtectedRoute, TaskBoard tests
- [x] Type check & build passing (`tsc`, `vite build`)

### Phase 9 – DevOps & Docs
- [x] Backend & frontend Dockerfiles, `nginx.conf`
- [x] `docker-compose.yml`, `.env.example`, `.gitignore`
- [x] `Readme.md` – setup, env vars, run, test, API, roles
- [x] Final end-to-end verification (API on H2; MySQL, Docker and browser not available)

### Ongoing (every step)
- [x] Update `Worklog.md` for every item created
- [x] Update `decision.md` for every decision taken
- [x] Tick items in this checklist
