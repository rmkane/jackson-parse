<!-- omit in toc -->
# Jackson Parse

A Spring Boot application demonstrating dual support for XML and JSON serialization/deserialization using Jackson.

<!-- omit in toc -->
## Table of Cntents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [API Endpoint](#api-endpoint)
  - [POST `/api/person`](#post-apiperson)
- [Running the Application](#running-the-application)
- [Testing](#testing)
  - [Unit Tests](#unit-tests)
  - [Integration Tests](#integration-tests)
  - [Code Coverage](#code-coverage)
- [Configuration](#configuration)
- [Model Structure](#model-structure)

## Overview

This application provides a REST API endpoint that accepts and returns data in both XML and JSON formats. The same endpoint can handle either format based on the `Content-Type` and `Accept` headers, making it flexible for clients using different data formats.

## Features

- **Dual Format Support**: Accepts and returns both XML and JSON
- **Jackson-based**: Uses Jackson for parsing (no JAXB or Jakarta dependencies)
- **LocalDateTime Support**: Proper handling of date/time fields with custom formatting
- **Flexible Input/Output**: Send JSON, receive XML (or vice versa) - any combination works
- **Unknown Properties**: Handles unknown properties gracefully via `@JsonAnyGetter`/`@JsonAnySetter`

## Technology Stack

- Spring Boot 3.5.6
- JDK 17
- Jackson (JSON and XML data formats)
- Maven

## API Endpoint

### POST `/api/person`

Creates/processes a person object. Accepts both XML and JSON, and can return either format.

**Request Headers:**

- `Content-Type`: `application/json` or `application/xml`
- `Accept`: `application/json` or `application/xml`

**Request Body Examples:**

**JSON:**

```json
{
  "version": "1.1",
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "addresses": [
    {
      "street": "123 Main St",
      "city": "Springfield",
      "zipCode": "12345",
      "primary": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-20T14:45:00"
    }
  ],
  "birthDate": "1990-05-15T08:00:00",
  "registeredAt": "2024-01-01T12:00:00"
}
```

**XML:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<person version="1.1">
    <id>1</id>
    <name>John Doe</name>
    <email>john.doe@example.com</email>
    <address primary="true">
        <street>123 Main St</street>
        <city>Springfield</city>
        <zipCode>12345</zipCode>
        <createdAt>2024-01-15T10:30:00</createdAt>
        <updatedAt>2024-01-20T14:45:00</updatedAt>
    </address>
    <birthDate>1990-05-15T08:00:00</birthDate>
    <registeredAt>2024-01-01T12:00:00</registeredAt>
</person>
```

**Note:** In XML, the `primary` field is serialized as an attribute on the `<address>` element, while in JSON it remains a regular property.

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080 (configurable in `application.yml`).

## Testing

### Unit Tests

Run unit tests:

```bash
mvn test
```

### Integration Tests

Run integration tests (requires the application to be running):

```bash
mvn test -Pintegration-tests
```

Integration tests verify all combinations:

- JSON input → JSON output
- JSON input → XML output
- XML input → JSON output
- XML input → XML output

### Code Coverage

Code coverage is measured using JaCoCo. The project maintains **100% code coverage** for all business logic.

**Generate Coverage Report:**

```bash
mvn clean test jacoco:report
```

**View Coverage Report:**

Open the HTML report in your browser:

```bash
open target/site/jacoco/index.html  # macOS
# or
xdg-open target/site/jacoco/index.html  # Linux
```

**Coverage with Integration Tests:**

```bash
mvn clean test -Pintegration-tests jacoco:report
```

**Coverage Exclusions:**

The `main` method in `JacksonParseApplication` is excluded from coverage as it's a standard Spring Boot entry point with no business logic.

**Coverage Thresholds:**

The build is configured to enforce 100% coverage. If coverage drops below this threshold, the build will fail. To check coverage thresholds:

```bash
mvn jacoco:check
```

The thresholds are configured in `pom.xml` and enforce:

- 100% instruction coverage
- 100% branch coverage
- 100% line coverage
- 100% method coverage
- 0 missed classes

## Configuration

Jackson is configured in `JacksonConfig` to:

- Use `JavaTimeModule` for `LocalDateTime` support
- Disable writing dates as timestamps
- Enable `FAIL_ON_UNKNOWN_PROPERTIES` (with `@JsonAnyGetter`/`@JsonAnySetter` for flexibility)

## Model Structure

- **Person**: Root entity with id, name, email, addresses, and date fields
- **Address**: Nested entity with street, city, zipCode, primary flag (XML attribute), and timestamps

Both models use Jackson annotations for XML/JSON mapping, ensuring consistent serialization across formats.
