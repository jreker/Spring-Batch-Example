# Spring Batch Example
This is a simple example of how to use spring batch with Chunks and one Tasklet Step.

## Description 
This spring batch application reads all entries from the "verkehrsstraftaten.csv" file in the java resource folder.
It filters all entries lower than 100€ via the item processor. The last step is the write into the mariadb.

## Tech Stack
- Spring Boot 4.0.3
- Spring Batch 6.0.2
- MySQL Connector/J 9.6.0

## Prerequisites
- Java 17+
- Maven
- Docker (Compose)

## Run
1. Start Database container from the folder "Docker"
```
docker-compose up
```
2. Compile and run the Java App.

## Changelog

### [2.0.0] - 2026-02-22
- Upgraded Spring Boot 3.0.0 → 4.0.3
- Upgraded Spring Batch 5 → 6.0.2
- Upgraded MySQL Connector/J 5.1.40 → 9.6.0 (new `com.mysql:mysql-connector-j` artifact)
- Migrated all Spring Batch 6 package renames (`org.springframework.batch.item.*` → `...infrastructure.item.*` etc.)
- Replaced removed `JobExecutionListenerSupport` with `JobExecutionListener` interface
- Switched to `spring-boot-starter-batch-jdbc` for JDBC-backed job repository

### [1.0.0] - 2023-01-01
- Initial release
- Spring Boot 3.0.0 with Spring Batch 5
- CSV to MySQL batch job with chunk processing and tasklet step

## Security

A full codebase security review was conducted (February 2026). No vulnerabilities were found.

- SQL queries use parameterized statements — no SQL injection risk
- No web endpoints, sessions, or authentication logic — minimal attack surface
- No unsafe deserialization, command execution, or XML parsing
- CSV input is loaded from a static classpath resource — no path traversal risk
- No sensitive data is written to logs

## Sample Data
The source of the sample data is GOVDATA:
https://www.govdata.de/web/guest/suchen/-/details/bussgelder-fliessender-verkehr-2021-der-stadt-aachen

