# OpenMRS Core – Architecture and Project Overview

Author: Droid (Factory.ai)  
Date: 2025-10-02

## Executive Summary

OpenMRS Core is the foundational platform of the OpenMRS ecosystem, providing domain models, services, and the deployable web application needed to run a robust EMR (Electronic Medical Record) in diverse environments. It is a modular, layered Java application built with Spring and Hibernate and packaged as a traditional Java webapp (WAR). The system emphasizes extensibility (via modules), standardized data model and migrations (via Liquibase), search (via Hibernate Search with Lucene or Elasticsearch), and operational quality through comprehensive CI and test infrastructure. [1][2][3][5][6][7][8]

## High-Level Architecture

- Layered design with clear separation of concerns:
  - Presentation/Web Layer (Spring MVC) in `web/` and a deployable WAR in `webapp/` [4][5]
  - Service & Domain Layer in `api/` (Spring services, domain objects) [3]
  - Persistence Layer via Hibernate ORM + JDBC drivers for MySQL/MariaDB/PostgreSQL; H2 for dev/testing [2][3]
- Configuration & Lifecycle Management via Spring, Maven profiles, and container integrations (Jetty/Tomcat) [1][5]
- Database schema/versioning managed by Liquibase (plus project-specific guidance and snapshot workflow) [3][8]
- Full-text search via Hibernate Search with Lucene (default) or Elasticsearch (optional) [2][3][7]
- Caching with Infinispan (integrating with Hibernate) [2][3]
- Logging via SLF4J facade with Log4j2 implementation [2]
- Security hardening via OWASP CSRF Guard, Encoder; vulnerability reporting policy in SECURITY.md [2][5][9]

### Architecture Diagram

```mermaid
flowchart LR
  UI[Client/UI (Browser or External App)] --> WEB[Web Layer (Spring MVC, web/)]
  WEB --> SVC[Service Layer (Spring Beans, api/)]
  SVC --> ORM[Persistence (Hibernate ORM)]
  ORM --> DB[(Relational DB: MySQL/MariaDB/PostgreSQL)]
  SVC -.-> CACHE[(Infinispan Cache)]
  SVC -.-> SEARCH[Hibernate Search]
  SEARCH -->|default| LUCENE[(Lucene Index)]
  SEARCH -->|optional| ES[(Elasticsearch)]
  DB <-. migrations .-> LB[Liquibase]
```

## Module Structure and Responsibilities

OpenMRS Core is a multi-module Maven project: [2]

- `api/` – Core domain and business logic
  - Domain entities (Patient, Encounter, Obs, Concept, Order, User, Location, etc.) and their services
  - Spring configuration, transaction management, and validators
  - Hibernate mapping and integration; Hibernate Search configuration
  - Liquibase integration for applying updates at runtime
  - Dependencies include Spring (core, context, tx, orm, jdbc), Hibernate (core, envers, c3p0), Liquibase, Jackson, HAPI (HL7), Infinispan, SLF4J/Log4j2 [3]

- `web/` – Web-layer classes and resources
  - Spring MVC controllers, web utilities, web config (e.g., `openmrs-servlet.xml` lives under web resources) [4]
  - Depends on `api/` and adds Spring Web/WebMVC, JSP/JSTL, OWASP encoder/csrfguard [4]

- `webapp/` – The deployable web application
  - Packages the WAR (`openmrs.war`) with web.xml and application resources
  - Jetty and Cargo plugins enable local development and embedded container runs
  - WAR depends on `web/` and `api/` [1][5]

- `liquibase/` – Utilities and guidance for snapshotting and managing database changes
  - Readme explains snapshot strategy and how to generate/apply them; helper scripts provided [8]

- `tools/`, `test/`, `test-module/`
  - Tools: IDE formatter configs and build-time utilities
  - Test: shared testing POM/resources
  - Test-module: example module structure demonstrating extension points (referenced in the root POM) [2]

### Module Dependency Diagram

```mermaid
graph TD
  A[openmrs-core (root pom)] --> B[api]
  A --> C[web]
  A --> D[webapp]
  A --> E[liquibase]
  A --> F[tools]
  A --> G[test]
  A --> H[test-module]
  C --> B
  D --> C
  D --> B
```

## Data Model and Persistence

- ORM: Hibernate maps domain objects to relational tables; transactions managed by Spring [2][3]
- Databases: MySQL/MariaDB and PostgreSQL are primary targets; H2 is used for testing and dev profiles [2][3][5]
- Migrations: Liquibase applies ordered changesets; the project includes a documented snapshot strategy to keep upgrades fast and maintainable [8]
- Auditing: Hibernate Envers is included for auditing entity changes [2]
- Validation: Hibernate Validator and javax.validation API [2]

## Search and Indexing

- Hibernate Search provides full-text search:
  - Default Lucene backend for embedded indexing [2][3]
  - Optional Elasticsearch backend via a dedicated docker compose overlay; reindexing supported via admin UI/REST [1][7]

## Caching

- Infinispan is integrated (embedded) and used as a second-level cache with Hibernate; Spring integration present [2][3]

## Web and Presentation

- Spring MVC controllers and views (JSP/JSTL support in core webapp), plus Velocity utilities for legacy pages [2][4][5]
- Security: OWASP CSRFGuard, Encoder; servlet filters and web.xml configuration [2][4][5]
- Deployment: WAR to Tomcat/Jetty; local development via `mvn jetty:run` or Cargo [1][5]

## Build, Testing, and CI/CD

- Build: Apache Maven with multi-module configuration; Java 8+ (CI verifies 8, 11, 17, 21) [1][2][6]
- Testing: JUnit 4 and 5, Mockito, Hamcrest; DBUnit for DB tests; Testcontainers for database integration tests [2]
- Coverage: JaCoCo with reporting to Coveralls on CI [6]
- Static analysis & quality: Checkstyle; CodeQL (workflow present in repo); Sonar profiles [2][6]
- CI: GitHub Actions builds on Ubuntu and Windows across multiple JDKs; caches Maven dependencies [6]

## Packaging, Operations, and Deployment

- Artifact: `webapp/target/openmrs.war` built via `mvn clean package` [1]
- Local Run: `mvn jetty:run` under `webapp/`; configurable ports [1]
- Docker: Compose files to run DB + app; supports “dev”, “nightly”, and production images; optional Elasticsearch stack [1][7]
- Config: Environment variables control DB host, credentials, admin password, etc., in compose [7]

## Security and Governance

- Security policy and contact: `SECURITY.md` [9]
- Dependencies include OWASP encoder and csrfguard; logging via SLF4J/Log4j2; regular dependency updates handled in CI/build scripts [2][6]

## Contribution and Community Workflow

- Developer onboarding and build instructions in README; links to wiki and SDK [1]
- Contribution process: JIRA issues, PR guidelines, coding conventions, review process documented in `CONTRIBUTING.md` [10]
- Internationalization: messages properties; translations managed via Transifex (see README) [1]

## Notable Technology Stack

- Frameworks: Spring 5.x (core, web, tx, orm), Hibernate 5.6.x, Hibernate Envers [2]
- Search: Hibernate Search 6.2.x; Lucene 8.11.x; optional Elasticsearch backend [2]
- DB: MySQL/MariaDB, PostgreSQL; H2 for tests [2]
- Caching: Infinispan 13.x [2]
- Serialization: Jackson 2.19.x; XStream (XML) [2]
- HL7: HAPI (v2.x structures) [2][3]
- Logging: SLF4J 1.7.x with Log4j 2.22.x [2]
- Build/CI: Maven, GitHub Actions, JaCoCo, Coveralls [1][2][6]

## Getting Started (Developer Quickstart)

1) Prereqs: JDK 8+ and Maven installed; verify with `mvn -version` [1]
2) Build: `mvn clean package` at repo root; WAR is produced at `webapp/target/openmrs.war` [1]
3) Run (dev):
   - Jetty: `cd webapp && mvn jetty:run` (override port via `-Djetty.http.port=`) [1]
   - Cargo: `mvn cargo:run` (override port via `-Dcargo.servlet.port=`) [1]
4) Docker (dev): `docker compose build && docker compose up` (use MVN_ARGS to tweak the build; optional ES overlay) [1][7]

## Appendix: How the Pieces Fit at Runtime

Request lifecycle example:
1) HTTP request enters webapp (web.xml) and is routed to a Spring MVC controller in `web/` [4][5]
2) Controller invokes service interfaces in `api/`, which apply business rules/validation [3]
3) Services use DAOs/Hibernate to interact with the DB; transactions managed by Spring [3]
4) On writes, Liquibase-managed schema ensures compatibility; Hibernate Search updates indexes [2][3][8]
5) Reads may leverage second-level cache (Infinispan) and search indexes for performance [2][3]

---

## Sources

1. README.md – repository purpose, build, run, Docker, module overview  
   https://github.com/openmrs/openmrs-core/blob/master/README.md
2. Root pom.xml – modules, dependency management, versions, plugins, quality tools  
   https://github.com/openmrs/openmrs-core/blob/master/pom.xml
3. api/pom.xml – API module dependencies (Spring, Hibernate, Liquibase, Infinispan, HAPI, Jackson, etc.)  
   https://github.com/openmrs/openmrs-core/blob/master/api/pom.xml
4. web/pom.xml – Web module dependencies (Spring Web/MVC, JSP/JSTL, OWASP libs)  
   https://github.com/openmrs/openmrs-core/blob/master/web/pom.xml
5. webapp/pom.xml – WAR packaging, Jetty/Cargo run configs  
   https://github.com/openmrs/openmrs-core/blob/master/webapp/pom.xml
6. .github/workflows/build.yaml – CI matrix, Java versions, coverage upload  
   https://github.com/openmrs/openmrs-core/blob/master/.github/workflows/build.yaml
7. docker-compose.yml – App + DB services, environment, healthchecks; ES overlay mentioned in README  
   https://github.com/openmrs/openmrs-core/blob/master/docker-compose.yml
8. liquibase/README.md – Snapshot strategy and migration workflow  
   https://github.com/openmrs/openmrs-core/blob/master/liquibase/README.md
9. SECURITY.md – Security reporting policy  
   https://github.com/openmrs/openmrs-core/blob/master/SECURITY.md
10. CONTRIBUTING.md – Contribution workflow and conventions  
   https://github.com/openmrs/openmrs-core/blob/master/CONTRIBUTING.md
