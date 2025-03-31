# SCA
A Spring Boot application for managing SWIFT codes. This project parses SWIFT code data (from an Excel or CSV file), stores it in a PostgreSQL database, and exposes a RESTful API for retrieving, adding, and deleting SWIFT code records.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Setup Instructions](#setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Local Environment Configuration](#local-environment-configuration)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
  - [Running Locally](#running-locally)
  - [Containerizing with Docker Compose](#containerizing-with-docker-compose)
- [Testing](#testing)
- [Dependencies and Versions](#dependencies-and-versions)

## Overview

This application reads SWIFT code data from a spreadsheet, processes and stores it in a PostgreSQL database, and provides RESTful endpoints to:

- Retrieve a single SWIFT code’s details (with branch information if it is a headquarter).
- Retrieve all SWIFT codes for a specific country.
- Add new SWIFT code entries.
- Delete SWIFT code entries.

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.3.5
- **JPA & ORM:** Hibernate 6.5.3.Final with Jakarta Persistence
- **Database:** PostgreSQL (tested with version 42.7.2 driver)
- **Excel Parsing:** Apache POI 5.2.3
- **Build Tool:** Gradle with Kotlin DSL
- **Testing:** JUnit 5 and Spring Boot Starter Test
- **Containerization:** Docker and Docker Compose

## Setup Instructions

### Prerequisites

- **Java 21 JDK**
- **PostgreSQL** (ensure that PostgreSQL is installed and running)
- **Docker & Docker Compose** (for containerized deployment)
- **Gradle** (using the Gradle Wrapper is recommended)

### Database Setup

Create the PostgreSQL database manually (if not already created):
```sql
CREATE DATABASE swiftdb;
```
Or for testing purposes (if using test properties):
```sql
CREATE DATABASE testdb;
```

### Local Environment Configuration

Create an `application.properties` file (in `src/main/resources`) with the following content:

```properties
# Application Properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/swiftdb}
spring.datasource.username=${DB_USERNAME:wojtek}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.show-sql=true

# JSON output formatting
spring.jackson.serialization.indent-output=true

# Server settings
server.port=8080

For testing, create an application-test.properties file (in src/test/resources):

# Test-specific properties (using an embedded or separate test database)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/testdb}
spring.datasource.username=${DB_USERNAME:test}
spring.datasource.password=${DB_PASSWORD:test}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Building the Project

To build the project, run:

```bash
./gradlew clean build
```

This command compiles the project, runs tests, and packages the application.

## Running the Application

### Running Locally

You can run the application locally using the Gradle BootRun task:

```bash
./gradlew bootRun
```
The app will start on http://localhost:8080

## Containerizing with Docker Compose

A sample `docker-compose.yml` file for running the application along with PostgreSQL:

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:postgresql://db:5432/swiftdb
      - DB_USERNAME=wojtek
      - DB_PASSWORD=postgres
    depends_on:
      - db

  db:
    image: postgres:13
    environment:
      POSTGRES_DB: swiftdb
      POSTGRES_USER: wojtek
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
```

To run using Docker Compose, execute:
docker-compose up --build

## Testing

### Running Tests Locally

To run all tests, execute:

```bash
./gradlew test
```

## Test Coverage

The test suite covers:
- **Unit tests** for `SwiftCodeServiceTest.java`
- **Integration tests** for `APIControllerIntegrationTest.java` and `SwiftCodeIntegrationTest.java`
- Edge cases such as invalid payloads, empty fields, and not-found scenarios.

## Dependencies and Versions

- **Spring Boot:** 3.3.5  
- **Spring Data JPA:** (via Spring Boot Starter Data JPA)  
- **PostgreSQL Driver:** 42.7.2  
- **Apache POI (for Excel):** 5.2.3  
- **JUnit 5:** As specified via `libs.junit` (check your Gradle version catalogs)  
- **Guava:** (as per your dependency configuration)  
- **Gradle:** Using Gradle 8.7 with Kotlin DSL  
- **Docker & Docker Compose:** Ensure Docker is installed and running on your machine  
