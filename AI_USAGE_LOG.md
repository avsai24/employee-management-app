# AI Usage Log

Required for the assessment. Log each meaningful AI interaction.

| Date | Tool | Prompt / question | What AI generated | What I changed & why | What I learned |
|------|------|-------------------|-------------------|----------------------|----------------|
| 2026-11-20 | Cursor | Scaffold Angular + Spring Boot assessment app locally | Project structure guidance, install steps | Used Spring Boot 4.2 (Initializr default), mapped MySQL to 3307 because local mysqld already uses 3306 | Confirm port conflicts before binding Docker |
| 2026-11-20 | Cursor | Implement JWT login/logout for assessment | Auth entities, SecurityFilterChain, Angular login + guard + interceptor | Adjusted DaoAuthenticationProvider for Spring Security 7 constructor style; kept JWT in localStorage per brief | Spring Boot 4 / Security 7 changed provider setup vs older tutorials |
| 2026-11-20 | Cursor | Build Employee CRUD with pagination/search | JPA entity/service/controller + Angular Material table/dialogs | Used soft-delete, DTO validation, debounced search (300ms), page size 10 | Keep uniqueness checks scoped to non-deleted rows |
| 2026-11-20 | Cursor | Add report filters + Excel/PDF export | ReportController/Service with Apache POI + OpenPDF; Angular reports page | Default date range = current month; adjusted seed hire dates so demos aren't empty | Export filtered dataset, not just current page |
| 2026-11-20 | Cursor | Polish README, tests, idle timeout | Unit tests for EmployeeService/JwtService; H2 test profile; idle timeout service | Used Mockito for service tests instead of only contextLoads; warning at 29 min then logout at 30 | Assessment wants documented AI use + basic backend tests |
| 2026-11-20 | Cursor | Employment type, inactive end date/reason, list End Date column | Entity/DTO/form/detail/list updates; contract end vs employment end labeling | Kept one End Date column in list; detail hides N/A fields for active full-time | Domain polish beyond core CRUD still helps interview demo |
| 2026-11-20 | Cursor | Close UC gaps: refresh token, roles, phone validation | `/api/auth/refresh`, interceptor retry, ROLE_USER seed, roleGuard, phone pattern | Reports API restricted to ADMIN; summary left authenticated so USER dashboard KPIs still work | Map soft/optional acceptance criteria explicitly so nothing is left ambiguous |
