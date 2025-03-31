# SCA
A Spring Boot application for managing SWIFT codes. This project parses SWIFT code data (from an Excel), stores it in a PostgreSQL database, and exposes a RESTful API for retrieving, adding, and deleting SWIFT code records.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Setup Instructions](#setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Local Database Setup](#local-database-setup)
  - [Local Environment Configuration](#local-environment-configuration)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
  - [Running Locally](#running-locally)
  - [Running with Docker](#running-with-docker)
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
- **Database:** PostgreSQL ver.16 (tested with version 42.7.2 driver)
- **Excel Parsing:** Apache POI 5.2.3
- **Build Tool:** Gradle with Kotlin DSL
- **Testing:** JUnit 5 and Spring Boot Starter Test
- **Containerization:** Docker and Docker Compose
- **IDE:** IntelliJ IDEA Community Edition 2024.1

## Project Structure
```bash
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── swiftcodes
│   │   │       └── service
│   │   │           └── app
│   │   │               ├── APIController.java
│   │   │               ├── APIDTO.java
│   │   │               ├── SwiftCode.java
│   │   │               ├── SwiftCodeService.java
│   │   │               └── SwiftCodesApplication.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── SWIFT_CODES.xlsx
│   └── test
│       ├── java
│       │   └── swiftcodes
│       │       └── service
│       │           └── app
│       │               ├── APIControllerIntegrationTest.java
│       │               ├── SwiftCodeServiceTest.java
│       │               └── SwiftCodeIntegrationTest.java
│       └── resources
│           └── application-test.properties
├── build.gradle.kts
└── README.md
```

## Setup Instructions

### Prerequisites

- **Java 21 JDK**
- **PostgreSQL** (ensure that PostgreSQL is installed and running)
- **Docker & Docker Compose** (for containerized deployment)
- **Gradle** (using the Gradle Wrapper is recommended)

### Local Database Setup

Run these commands as superuser (postgres) on your psql server to create the production user and database:
```sql
CREATE USER wojtek WITH PASSWORD 'postgres';

CREATE DATABASE swiftdb;

GRANT ALL PRIVILEGES ON DATABASE swiftdb TO wojtek;
```
Run these commands to create the test user and database (if using test properties):
```sql
CREATE USER test WITH PASSWORD 'test';

CREATE DATABASE testdb;

GRANT ALL PRIVILEGES ON DATABASE testdb TO test;
```

### Local Environment Configuration

All enviorment configurations of the app u might need to change to your preferences are located in an `application.properties` file (in `src/main/resources`)

Configuration for testing is located in `application-test.properties` file (in src/test/resources)

### Building the Project

- Clone the repository to your machine
- To build the project, run:

```bash
./gradlew clean build
```
This command compiles the project, runs tests, and packages the application.

## Running the Application

### Running Locally
You can run the application locally using the Gradle BootRun task (remember to install Gradle before):

```bash
./gradlew bootRun
```
The app will start on http://localhost:8080

Once the application starts, you can use the following `curl` commands to interact with the API:

#### Available Endpoints
- **Using Windows(Powershell)**

  - **Retrieve SWIFT Code Details**
    ```powershell
    curl.exe -X GET "http://localhost:8080/v1/swift-codes/BCECCLRMXXX"
    ```
  
  - **Retrieve All SWIFT Codes for a Specific Country**  
  
    ```powershell
    curl.exe -X GET "http://localhost:8080/v1/swift-codes/country/PL"
    ```
  
  - **Add a New SWIFT Code**  
  
    ```powershell
    curl.exe -X POST "http://localhost:8080/v1/swift-codes" `
       -H "Content-Type: application/json" `
       -H "Accept: application/json" `
       -d '{\"swiftCode\": \"TESTCLRMIXXX\", \"bankName\": \"Test Bank\", \"address\": \"\", \"countryISO2\": \"CL\", \"countryName\": \"Chile\", \"isHeadquarter\": true}'
    ```
  
  - **Delete a SWIFT Code**  
  
    ```powershell
    curl.exe -X DELETE "http://localhost:8080/v1/swift-codes/TESTCLRMIXXX" `
       -H "Content-Type: application/json" `
       -H "Accept: application/json"
    ```
  
  - **Additionaly to Gracefully Shutdown the App**  
  
    ```powershell
    curl.exe -X POST http://localhost:8080/actuator/shutdown
    ```
- **Using Linux(Bash)**
    - **Retrieve SWIFT Code Details**
    ```bash
    curl -X GET "http://localhost:8080/v1/swift-codes/BCECCLRMXXX"
    ```
  
  - **Retrieve All SWIFT Codes for a Specific Country**  
  
    ```bash
    curl -X GET "http://localhost:8080/v1/swift-codes/country/PL"
    ```
  
  - **Add a New SWIFT Code**  
  
    ```bash
    curl -X POST "http://localhost:8080/v1/swift-codes" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d '{"swiftCode": "TESTCLRMIXXX", "bankName": "Test Bank", "address": "", "countryISO2": "CL", "countryName": "Chile", "isHeadquarter": true}'
    ```
  
  - **Delete a SWIFT Code**  
  
    ```bash
    curl -X DELETE "http://localhost:8080/v1/swift-codes/TESTCLRMIXXX" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json"
    ```
  
  - **Additionaly to Gracefully Shutdown the App**  
  
    ```bash
    curl -X POST "http://localhost:8080/actuator/shutdown"
    ```

## Running with Docker

A `docker-compose.yml` and `docker-compose.test.yml` files are configured for running the application and test enviorment along with PostgreSQL

### To run using Docker Compose, execute:
```bash
docker-compose build
docker-compose up
```

### Shutting Down the Containers
To stop and remove the running containers, use the following command:

```bash
docker-compose down -v
```

## Testing

### Running Tests Locally
- Clone the repository to your machine
- To run all tests, in terminal execute:

```bash
./gradlew test
```

### Running Tests on Docker

#### Run Tests
```bash
docker-compose -f docker-compose.test.yml build
docker-compose -f docker-compose.test.yml up
```

#### Shutting Down the Containers
To stop and remove the test containers, use the following command:

```bash
docker-compose -f docker-compose.test.yml down -v
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
- **Gradle:** Using Gradle 8.7 with Kotlin DSL  
- **Docker & Docker Compose:** Ensure Docker is installed and running on your machine  
