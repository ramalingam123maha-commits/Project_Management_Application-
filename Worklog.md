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
| 11 | 2026-10-27 | Backend | `backend/src/main/java/com/projectflow/domain/` | Implemented JPA domain entities (`User`, `Project`, `ProjectMember`, `Task`, `TaskComment`) and enums (`Role`, `ProjectStatus`, `TaskStatus`, `TaskPriority`) mapped to Flyway V1 schema with lifecycle audit hooks (`@PrePersist`, `@PreUpdate`), lazy associations, and composite key mapping for `ProjectMember`. |
| 12 | 2026-10-27 | Backend | `backend/src/main/java/com/projectflow/repository/` | Implemented Spring Data JPA repositories (`UserRepository`, `ProjectRepository`, `ProjectMemberRepository`, `TaskRepository`, `TaskCommentRepository`) with JPQL `JOIN FETCH` queries to prevent N+1 queries, dashboard aggregation counters, and membership query methods. |
| 13 | 2026-10-27 | Backend | `security/` (`JwtService`, `JwtAuthenticationFilter`, `UserPrincipal`, `CustomUserDetailsService`, `RestAuthEntryPoint`) | Implemented JWT (HS256, JJWT 0.12) token creation/validation, Bearer-token filter that reloads the user from the DB on each request, `UserDetails` adapter with `ROLE_` authority, and a JSON 401 entry point. |
| 14 | 2026-10-27 | Backend | `config/SecurityConfig.java`, `config/AdminSeeder.java`, `ProjectFlowApplication.java` | Stateless security filter chain (CSRF off, CORS from config, `/api/auth/register` and `/api/auth/login` public, everything else authenticated), BCrypt encoder, method security enabled; idempotent first-admin seeder from `app.admin.*`; Spring Boot entry point. Verified with `mvn compile` (Java 17). |
| 15 | 2026-10-27 | Backend | `dto/` (14 records) and `mapper/DtoMapper.java` | Created Java 17 records with Jakarta validation annotations for Auth, User, Project, Task, and Dashboard payloads. DtoMapper converts entities to DTOs while stripping internal fields like password hashes. Verified with `mvn compile`. |
