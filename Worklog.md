# ProjectFlow – Work Log

Chronological record of everything created or changed in this repository.
Every new file, module, or feature must be logged here when it is created.

| # | Date (UTC) | Area | Item | Description |
|---|------------|------|------|-------------|
| 1 | 2026-10-27 | Docs | `Worklog.md` | Created work log to track every artifact created in the project. |
| 2 | 2026-10-27 | Docs | `decision.md` | Created decision log (ADR-style) to record technical decisions. |
| 3 | 2026-10-27 | Design | Architecture | Defined project architecture (no code created yet). See `decision.md` D-001 – D-012. |
| 4 | 2026-10-27 | Scaffold | Root files | Created empty `docker-compose.yml`, `.env.example`, `.gitignore`. |
| 5 | 2026-10-27 | Scaffold | `backend/` | Created empty backend structure (config, security, domain, repository, dto, mapper, service, controller, exception, resources, tests). |
| 6 | 2026-10-27 | Scaffold | `frontend/` | Created empty frontend structure (config files, api, types, context, routes, hooks, validation, utils, components, pages, styles, tests). |
| 7 | 2026-10-27 | Docs | `Plan.md` | Created project plan: requirements, languages, procedure followed, phased checklist. |
| 8 | 2026-10-27 | Backend | `backend/pom.xml` | Configured Maven dependencies for Spring Boot 3.3.4, Java 17, Web, Security, Data JPA, Validation, MySQL, Flyway (core + mysql), JJWT 0.12.6, and test starters (H2, Spring Security Test). |
| 9 | 2026-10-27 | Backend | `backend/src/main/resources/application.yml` | Configured Spring Boot application settings: env-driven MySQL datasource, Flyway migration runner, JPA validation mode, JJWT configuration, CORS allowed origins, and initial Admin seeder properties. |
| 10 | 2026-10-27 | Database | `backend/src/main/resources/db/migration/V1__init.sql` | Created Flyway initial schema migration for `users`, `projects`, `project_members`, `tasks`, and `task_comments` with primary keys, foreign keys, check constraints, cascade/set null rules, and query indexes. |
