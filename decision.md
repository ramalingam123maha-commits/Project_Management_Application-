# ProjectFlow – Decision Log

Every technical decision is recorded here with its context and rationale.
Format: **ID – Title** · Status · Decision · Reason · Alternatives considered.

| ID | Title | Status | Decision | Reason | Alternatives |
|----|-------|--------|----------|--------|--------------|
| D-001 | Overall style | Accepted | Monorepo with `backend/` and `frontend/`, communicating via REST/JSON | Simple to set up, review and deploy together | Separate repos, GraphQL |
| D-002 | Backend stack | Accepted | Java 17, Spring Boot 3.3, Maven | Requested stack; Java 17 LTS; Maven is the most common Spring build tool | Gradle |
| D-003 | Backend layering | Accepted | Controller → Service → Repository → Entity, with DTOs at the boundary | Separation of concerns; entities (e.g. password hash) never exposed | Exposing entities directly |
| D-004 | Database & migrations | Accepted | MySQL 8 with Flyway versioned migrations; `ddl-auto=validate` | Reproducible schema, safe upgrades | Hibernate auto-DDL |
| D-005 | Authentication | Accepted | Stateless JWT (HS256, secret from env var, short expiry) + BCrypt password hashing | Fits SPA + REST; no server sessions | Session cookies, OAuth2 provider |
| D-006 | Authorization | Accepted | Roles ADMIN / PROJECT_MANAGER / MEMBER via `@PreAuthorize`, plus ownership/membership checks in services | Role checks alone cannot prevent IDOR; server is the source of truth | UI-only checks |
| D-007 | Default role & first admin | Accepted | Self-registration creates MEMBER; initial ADMIN seeded from env vars; only Admin changes roles | Prevents privilege escalation at signup | Role chosen at registration |
| D-008 | Team membership | Accepted | PM/Admin adds existing registered users to a project by email | Keeps scope focused; no mail server required | Email invitation tokens (future) |
| D-009 | Error handling | Accepted | Global `@RestControllerAdvice` returning uniform `ApiError` JSON; no stack traces leaked | Consistent client handling, secure | Per-controller handling |
| D-010 | Validation | Accepted | Bean Validation on DTOs (authoritative) + Zod schemas in frontend (UX) | Defense in depth, instant feedback | Backend-only |
| D-011 | Frontend stack | Accepted | React 18 + TypeScript + Vite, React Router 6, TanStack Query, Axios, react-hook-form + Zod, Tailwind CSS | Requested stack; fast tooling; cached server state; responsive utility CSS | CRA, Redux, MUI |
| D-012 | Testing | Accepted | Backend: JUnit 5, Mockito, MockMvc with H2 test profile. Frontend: Vitest + React Testing Library | Fast, standard tooling; covers permission matrix and forms | Testcontainers (optional later) |
| D-013 | Local environment | Accepted | `docker-compose.yml` for MySQL + backend + frontend; `.env.example` with no real secrets | One-command setup; no secrets in git | Manual install only |
| D-014 | Process | Accepted | Every created item is logged in `Worklog.md`; every decision in `decision.md` | Requested by project owner for traceability | — |
| D-015 | Scaffold first | Accepted | Create all folders and empty files before writing any content | Requested by user; structure reviewable up-front | Build files incrementally |
| D-016 | Spring Boot version & JJWT | Accepted | Spring Boot 3.3.4 (Java 17 LTS baseline) and JJWT 0.12.6 | Modern Spring Boot 3.x with jakarta.* namespace; latest stable JJWT API with modular runtime artifacts | Spring Boot 2.x, Spring Authorization Server |
| D-017 | Database configuration & Flyway | Accepted | MySQL Hikari pool driven by environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`); Flyway runs on startup with `baseline-on-migrate: true`; Hibernate `ddl-auto: validate` | Ensures safe, reproducible schema changes without Hibernate altering production schemas; fully container- and 12-factor-ready | `ddl-auto: update`, manual SQL scripts |
