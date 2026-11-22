# Money Matters

**Money Matters** is a financial data platform designed to aggregate, normalize, and expose market information from major Indian stock exchanges (NSE and BSE) via high-performance RESTful APIs.

---

## Architecture & Multi-Module Layout

The project is structured as a Maven multi-module architecture:

```
money-matters/
├── pom.xml                     # Root aggregator & dependencyManagement parent POM
├── README.md                   # Platform documentation
├── application/                # Executable Spring Boot web application module
│   ├── README.md               # Application module guide
│   └── src/
├── nse/                        # National Stock Exchange (NSE) data integration module
│   ├── README.md               # NSE module guide
│   ├── docs/
│   │   └── API.md              # NSE REST API documentation (Markdown)
│   └── src/
└── bse/                        # Bombay Stock Exchange (BSE) market data module
    ├── README.md               # BSE module guide
    └── src/
```

### Modules at a Glance

| Module | Role | Documentation |
|---|---|---|
| **`application`** | Primary Spring Boot runner; packages executable fat JAR and hosts central configuration | [Application Guide](application/README.md) |
| **`nse`** | Ingests listed equity data from NSE archives, caches in memory, and exposes REST endpoints | [NSE Guide](nse/README.md) · [API Docs](nse/docs/API.md) |
| **`bse`** | Integration module for Bombay Stock Exchange data | [BSE Guide](bse/README.md) |

---

## Tech Stack

- **Java**: 25 (OpenJDK 25 LTS)
- **Framework**: Spring Boot 4.1.3 (Spring Framework 7.0.11)
- **API Documentation**: Springdoc OpenAPI / Swagger UI 3.2.0 (OpenAPI 3.1.0)
- **Data Parsing**: OpenCSV 5.9
- **Build Tool**: Apache Maven (via Maven Wrapper `mvnw`)

---

## Getting Started

### Prerequisites

- **Java 25 JDK** installed (ensure `JAVA_HOME` points to JDK 25).
- Git.

### 1. Build the Reactor

```powershell
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-25'
.\mvnw.cmd clean package
```

### 2. Run the Application

```powershell
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-25'
.\mvnw.cmd spring-boot:run -pl application
```

Alternatively, run the packaged fat JAR directly:

```powershell
java -jar application\target\application-0.0.1-SNAPSHOT.jar
```

---

## Interactive API Documentation (Swagger UI)

When the application is running, Swagger UI and OpenAPI specifications are available at:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **All APIs OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **NSE Module OpenAPI Spec**: [http://localhost:8080/v3/api-docs/nse](http://localhost:8080/v3/api-docs/nse)

### Module-Level API Documentation

- **NSE API Spec**: [`nse/docs/API.md`](nse/docs/API.md)

---

## Running Tests

Run all unit and integration tests across the reactor:

```powershell
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-25'
.\mvnw.cmd test
```
