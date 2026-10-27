# ProjectFlow

A project and task management web app.

- **Frontend:** React 18, TypeScript, Vite, Tailwind CSS, React Router, TanStack Query, react-hook-form with Zod
- **Backend:** Java 17, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA
- **Database:** MySQL 8, schema managed by Flyway

## Roles

| Role | What they can do |
|---|---|
| **Admin** | Everything. Sees all projects and tasks, changes user roles, creates projects. |
| **Project Manager** | Creates projects. In projects they own: edit and delete the project, manage members, create, edit, assign and delete tasks. |
| **Member** | Sees projects they belong to. Changes the status of tasks assigned to them. Comments on tasks in their projects. |

New sign-ups are Members. An Admin promotes users on the **Users** page. A user who is not a member of a project gets "not found" for it.

## Run with Docker

Requires Docker with Compose.

1. Copy `.env.example` to `.env`.
2. Replace every `CHANGE-ME`. Generate the JWT secret with `openssl rand -base64 48`.
3. Run `docker compose up --build`.
4. Open `http://localhost:3000` (or the `WEB_PORT` you set) and sign in with `ADMIN_INITIAL_EMAIL` and `ADMIN_INITIAL_PASSWORD`.

Compose refuses to start if a required secret is missing. Only the web port is published; MySQL and the backend are internal.

> Status: the Docker files have not been built or run yet (no Docker in the authoring environment). If a build fails, please report it.

## Run for development

Requires Java 17, Maven, Node 20 and a MySQL 8 database.

**Database**

```sql
CREATE DATABASE projectflow CHARACTER SET utf8mb4;
CREATE USER 'projectflow'@'%' IDENTIFIED BY 'projectflow';
GRANT ALL ON projectflow.* TO 'projectflow'@'%';
```

**Backend** (port 8080)

```
cd backend
mvn spring-boot:run
```

Flyway creates the tables on first start. A first Admin is created from `ADMIN_INITIAL_*`. In development the defaults are `admin@projectflow.local` / `Admin@123456`. Change them for anything that is not a local machine.

**Frontend** (port 5173)

```
cd frontend
npm install
npm run dev
```

The frontend reads `VITE_API_URL` (default `http://localhost:8080`). See `frontend/.env.example`.

## Environment variables

Backend:

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME` | `localhost`, `3306`, `projectflow` | Database location |
| `DB_USER`, `DB_PASSWORD` | `projectflow`, `projectflow` | Database login |
| `JWT_SECRET` | development-only value | Base64 signing key, at least 32 bytes. **Always set your own outside development.** |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Browser origins allowed to call the API |
| `ADMIN_INITIAL_NAME`, `ADMIN_INITIAL_EMAIL`, `ADMIN_INITIAL_PASSWORD` | see above | First Admin account |

Frontend: `VITE_API_URL` (build time). The Docker image sets it empty so the browser calls `/api` on the same address and nginx forwards it.

Docker Compose also uses `DB_ROOT_PASSWORD` and `WEB_PORT`. See `.env.example`.

## Tests

```
cd backend && mvn test          # 39 tests, in-memory H2 database, no MySQL needed
cd frontend && npm test         # 14 tests
cd frontend && npm run build    # type check and production build
```

Backend reports are written to `backend/target/surefire-reports/`.

## API

All endpoints are under `/api` and return JSON. Except for register and login, send `Authorization: Bearer <token>`.

Errors look like `{ "timestamp", "status", "error", "message", "path", "fieldErrors" }`. `fieldErrors` is present for validation failures (400).

| Method and path | Who | Purpose |
|---|---|---|
| `POST /auth/register` | public | Create a Member account, returns token and user |
| `POST /auth/login` | public | Returns token and user |
| `GET /auth/me` | signed in | Current user |
| `GET /users[?role=]`, `GET /users/{id}` | Admin | List and read users |
| `GET /users/roles` | Admin | Available roles |
| `PATCH /users/{id}/role` | Admin | Change a role (not your own) |
| `GET /projects`, `GET /projects/{id}` | members of the project, Admin | List and read |
| `POST /projects` | Admin, Project Manager | Create |
| `PUT /projects/{id}`, `DELETE /projects/{id}` | Admin, owning PM | Edit and delete |
| `GET /projects/{id}/members` | project members | List members |
| `POST /projects/{id}/members` | Admin, owning PM | Add by email |
| `DELETE /projects/{id}/members/{userId}` | Admin, owning PM | Remove (their tasks become unassigned) |
| `GET /projects/{id}/tasks[?status=]` | project members | List tasks |
| `POST /projects/{id}/tasks` | Admin, owning PM | Create task |
| `GET /tasks/my` | signed in | Tasks assigned to me |
| `GET /tasks/{id}` | project members | Read task |
| `PUT /tasks/{id}`, `DELETE /tasks/{id}` | Admin, owning PM | Edit and delete |
| `PATCH /tasks/{id}/status` | Admin, owning PM, assignee | Change status |
| `PATCH /tasks/{id}/assignee?assigneeId=` | Admin, owning PM | Assign (omit the id to unassign) |
| `GET /tasks/{id}/comments`, `POST /tasks/{id}/comments` | project members | Read and add comments |
| `DELETE /tasks/{id}/comments/{commentId}` | comment author, Admin, owning PM | Delete a comment |
| `GET /dashboard` | signed in | Counts and deadlines, scoped to the role |

## Security notes

- Passwords are hashed with BCrypt. Tokens are signed JWTs.
- Access is enforced on the server for every request. Hidden buttons in the UI are only a convenience.
- The token is kept in the browser's `localStorage`, so a cross-site scripting flaw would expose it. User text is rendered as plain text to avoid that.
- Set a strong `JWT_SECRET` and Admin password before any real use.

## Project layout

```
backend/    Spring Boot API (controller, service, repository, domain, security)
frontend/   React app (pages, components, api, context, validation, __tests__)
docker-compose.yml, .env.example
Plan.md, Worklog.md, decision.md   Build checklist, change log, decisions
```
