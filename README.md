# Spring Boot Starter 0

A Spring Boot fundamentals project demonstrating modern configuration patterns, environment-based secrets management, and database migration setup using Flyway.

## Overview

This project is designed to teach and practice the core building blocks of a Spring Boot application:

- Spring Boot application bootstrap
- YAML-based configuration
- externalized secrets via `.env`
- MySQL database connectivity
- Flyway database migrations
- configuration binding with `@ConfigurationProperties`
- layered project organization with controllers, services, and config classes

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Maven
- MySQL 8.4
- Flyway
- Docker Compose

## Prerequisites

Before running the project, make sure you have:

- JDK 25 or newer installed
- Maven available via the included `mvnw` wrapper
- Docker Desktop or Docker Engine available if you want to run MySQL in a container

## Project Structure

```text
spring-boot-starter-0/
├── .env.example
├── .env
├── .gitignore
├── docker-compose.yml
├── pom.xml
├── mvnw
├── README.md
├── src/
│   └── main/
│       ├── java/
│       │   └── sn/sdley/springbootstarter0/
│       │       ├── config/
│       │       │   └── AppProperties.java
│       │       ├── controller/
│       │       │   └── HomeController.java
│       │       ├── service/
│       │       │   ├── OrderService.java
│       │       │   ├── PayPalPaymentService.java
│       │       │   ├── PaymentService.java
│       │       │   └── StripePaymentService.java
│       │       └── SpringBootStarter0Application.java
│       └── resources/
│           ├── application.yml
│           └── static/
│               └── index.html
└── target/
```

## Configuration and Secrets Management

The project uses Spring Boot YAML configuration in `src/main/resources/application.yml` and reads environment-specific values from a local `.env` file.

### 1. Create your local environment file

Copy the example file and fill in your own values:

```bash
cp .env.example .env
```

Example `.env`:

```env
APP_NAME=spring-boot-starter-0
DB_URL=jdbc:mysql://localhost:3306/app_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
DB_USERNAME=app_user
DB_PASSWORD=app_password
JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
APP_PAGE_SIZE=10
FLYWAY_USER=root
FLYWAY_PASSWORD=root
```

Important best practices:

- Commit only `.env.example`
- Never commit `.env`
- Keep production credentials in a secure secret manager or environment injection system
- Use defaults only for non-sensitive, safe values

The repository `.gitignore` excludes `.env` files so secrets stay out of Git history.

## Database Setup

This app expects a MySQL instance running locally.

### Option 1: Docker Compose

Start the database container:

```bash
docker compose up -d mysql
```

The configuration in `docker-compose.yml` starts MySQL with:

- database: `app_db`
- username: `app_user`
- password: `app_password`

### Option 2: Local MySQL

If you already have MySQL installed and running, make sure the database and credentials match your `.env` file.

## Running the Application

Run the application with Maven:

```bash
./mvnw spring-boot:run
```

The app starts on:

```text
http://localhost:8080
```

## Flyway Maven Plugin Setup

Flyway is configured in `pom.xml` so it can read the same database credentials used by the application without hardcoding sensitive values in source control.

This project uses the `properties-maven-plugin` during the `initialize` phase to load values from `.env`, then the Flyway plugin uses those properties.

### Why this approach is preferred

- secrets are not copied into the POM
- the same credentials are reused across the app and database migration tooling
- local development remains simple
- CI and production environments can inject values through environment variables

### Maven configuration example

```xml
<properties>
    <DB_URL>jdbc:mysql://localhost:3306/app_db?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC&amp;createDatabaseIfNotExist=true</DB_URL>
    <DB_USERNAME>app_user</DB_USERNAME>
    <DB_PASSWORD>app_password</DB_PASSWORD>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>properties-maven-plugin</artifactId>
            <version>1.2.1</version>
            <executions>
                <execution>
                    <phase>initialize</phase>
                    <goals>
                        <goal>read-project-properties</goal>
                    </goals>
                    <configuration>
                        <files>
                            <file>${project.basedir}/.env</file>
                        </files>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <plugin>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-maven-plugin</artifactId>
            <configuration>
                <url>${DB_URL}</url>
                <user>${DB_USERNAME}</user>
                <password>${DB_PASSWORD}</password>
                <cleanDisabled>false</cleanDisabled>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Running Flyway commands

Common Flyway operations:

```bash
./mvnw flyway:info
./mvnw flyway:migrate
./mvnw flyway:clean
```

Use `flyway:clean` only in development or disposable environments. Do not use it in production unless you are intentionally resetting the database.

## Application Configuration Pattern

The project uses a typed configuration bean for nested application values:

```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Page page = new Page();
}
```

This matches the YAML structure:

```yaml
app:
  page:
    size: ${APP_PAGE_SIZE:10}
```

This is cleaner and safer than repeatedly referencing raw placeholder keys in many classes.

## Notes

- The application reads environment properties from `.env` using Spring Boot import rules.
- The Flyway plugin uses the same base values to avoid config drift.
- This project is intentionally simple and instructional, making it a good starting point for learning Spring fundamentals.

## Contributing

If you are extending the project:

1. keep configuration externalized
2. avoid committing secrets
3. use typed configuration objects for application properties
4. keep migrations versioned and explicit
5. prefer environment variables or secret managers over hardcoded values
