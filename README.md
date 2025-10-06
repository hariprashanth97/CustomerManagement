# Customer Management App

Simple Java Swing application that manages customers and up to 3 addresses per customer.

## Features
- DB schema created automatically on first run.
- Customer fields: id, short_name, full_name, city, postal_code.
- Address (max 3 per customer): line1, line2, line3; each line limited to 80 chars.
- Postal code validation (only digits, 5-8 length). Customize at `Validator.isValidPostal`.
- Add / Edit / Delete customers; Manage addresses (Add / Edit / Delete) with max-3 enforcement.

## Build & Run
You need Java 11+ and Maven.

To build:
```
mvn clean package
```

To run the app (after build):
```
java -jar target/customer-management-app-1.0.0.jar
```

Alternatively, run from IDE by launching `com.example.app.CustomerManagementApp`.

## Project structure
- `com.example.app` - CustomerManagement (Main class)
- `com.example.db` - Database bootstrap and DAOs
- `com.example.model` - Customer and Address models
- `com.example.util` - Validator utilities
- `com.example.view` - Customer and Address Panels(Swing UI)

