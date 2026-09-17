# Spring Boot Starter 0

An opinionated Spring Boot CRUD starter for building a small product catalog and user management API with MySQL, Flyway, Thymeleaf, and MapStruct.

It is intentionally practical: the app boots, serves a simple home page, exposes REST endpoints for users and products, seeds demo data when enabled, and keeps schema changes versioned.

## What’s inside

- Spring Boot 4.1
- Java 25
- Maven
- MySQL + Flyway migrations
- REST APIs for users and products
- Thymeleaf home page and a simple `/hello` endpoint
- DTO mapping with MapStruct
- Validation and centralized exception handling
- Optional dev data seeding

## Features

### Users

- list users with optional sorting
- fetch a user by id
- register a user
- update or delete a user
- change password

### Products

- list products, optionally filtered by category
- fetch a product by id
- create, update, and delete products

### Web

- `GET /` renders a basic Thymeleaf home page
- `GET /hello` returns a simple JSON greeting

## Project layout

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

## Requirements

- JDK 25
- Maven
- MySQL 8+

## Configuration

Application settings live in `src/main/resources/application.yml` and can be overridden with environment variables or a local `.env` file.

Key values:

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

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## API quick tour

| Method | Path | Description |
| --- | --- | --- |
| GET | `/users` | List users |
| GET | `/users/{id}` | Get one user |
| POST | `/users` | Register a user |
| PUT | `/users/{id}` | Update a user |
| DELETE | `/users/{id}` | Delete a user |
| POST | `/users/{id}/change-password` | Change password |
| GET | `/products` | List products |
| GET | `/products?categoryId=1` | List products by category |
| GET | `/products/{id}` | Get one product |
| POST | `/products` | Create a product |
| PUT | `/products/{id}` | Update a product |
| DELETE | `/products/{id}` | Delete a product |
| GET | `/hello` | Return a greeting |

## Database and migrations

Flyway manages schema changes from `src/main/resources/db/migration`. The default setup expects a local MySQL database, and the Maven Flyway plugin reuses the same credentials as the app.

Common commands:

```bash
./mvnw flyway:info
./mvnw flyway:migrate
./mvnw flyway:clean
```

Use `clean` only in disposable environments.

## Notes

- Passwords are stored plainly in the current implementation; replace that before production use.
- Demo data can be enabled through `APP_SEED_ENABLED=true`.
- This starter is best suited as a learning or internal prototype base.

