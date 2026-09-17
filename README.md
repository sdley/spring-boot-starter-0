# Spring Boot Starter 0

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Maven](https://img.shields.io/badge/Maven-4.0.0-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**A polished Spring Boot starter for real-world CRUD apps.**  
Build and run a clean product catalog and user management backend with MySQL, Flyway, Thymeleaf, MapStruct, validation, and a straightforward REST API.

---

## Highlights

- **Modern stack:** Java 25, Spring Boot 4.1, Maven
- **Real app structure:** controllers, DTOs, entities, mappers, repositories, validation
- **REST-first:** user and product endpoints with standard CRUD flows
- **Database-ready:** MySQL + Flyway migrations
- **Developer-friendly:** optional seed data, env-based config, clean layering
- **Web support:** Thymeleaf home page and a `/hello` endpoint

---

## What it does

This project is designed as a practical starter, not a toy demo. It currently supports:

- managing users with create, read, update, delete, and password change flows
- managing products with category-aware listing and CRUD operations
- serving a simple home page at `/`
- returning a JSON greeting from `/hello`
- loading schema changes through versioned Flyway migrations

---

## Tech stack

| Layer | Tools |
| --- | --- |
| Backend | Spring Boot 4.1, Java 25 |
| Build | Maven |
| Persistence | Spring Data JPA, MySQL |
| Migrations | Flyway |
| API | Spring Web |
| Mapping | MapStruct |
| UI | Thymeleaf |
| Validation | Spring Validation |

---

## Project structure

```text
src/main/java/sn/sdley/springbootstarter0/
├── config/
├── controller/
├── dtos/
├── entities/
├── mappers/
├── repositories/
└── validation/

src/main/resources/
├── application.yml
├── db/migration/
└── templates/
```

---

## Key endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/` | Render the home page |
| GET | `/hello` | Return a greeting |
| GET | `/users` | List users |
| GET | `/users/{id}` | Fetch a user |
| POST | `/users` | Register a user |
| PUT | `/users/{id}` | Update a user |
| DELETE | `/users/{id}` | Delete a user |
| POST | `/users/{id}/change-password` | Change a password |
| GET | `/products` | List products |
| GET | `/products?categoryId=1` | List products by category |
| GET | `/products/{id}` | Fetch a product |
| POST | `/products` | Create a product |
| PUT | `/products/{id}` | Update a product |
| DELETE | `/products/{id}` | Delete a product |

---

## Requirements

- JDK 25
- Maven
- MySQL 8+

---

## Configuration

Runtime settings are defined in `src/main/resources/application.yml` and can be overridden with environment variables or a local `.env` file.

Common values:

- `APP_NAME`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JPA_DATABASE_PLATFORM`
- `APP_PAGE_SIZE`
- `APP_SEED_ENABLED`
- `FLYWAY_USER`
- `FLYWAY_PASSWORD`

Example `.env`:

```env
APP_NAME=spring-boot-starter-0
DB_URL=jdbc:mysql://localhost:3306/app_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
DB_USERNAME=app_user
DB_PASSWORD=app_password
JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
APP_PAGE_SIZE=10
APP_SEED_ENABLED=false
FLYWAY_USER=root
FLYWAY_PASSWORD=root
```

> Keep `.env` out of version control and store real credentials in a secret manager outside local development.

---

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

```text
http://localhost:8080
```

---

## Database and migrations

Flyway manages schema evolution from `src/main/resources/db/migration`. The Maven Flyway plugin uses the same database credentials as the application to avoid config drift.

Useful commands:

```bash
./mvnw flyway:info
./mvnw flyway:migrate
./mvnw flyway:clean
```

Use `clean` only in disposable environments.

---

## Notes

- Passwords are stored plainly in the current implementation and should be hashed before production use.
- Demo data can be enabled with `APP_SEED_ENABLED=true`.
- The app is well suited as a learning base, internal starter, or prototype backend.
