# Application Module (`application`)

The `application` module is the executable Spring Boot entry point for the **Money Matters** platform. It aggregates all domain modules (`nse`, `bse`) and packages them into a single runnable fat JAR.

---

## Responsibilities

- **Bootstrapping**: Launches the Spring Boot context via `MoneyMattersApplication`.
- **Component Scanning**: Configured with `scanBasePackages = "com.spiderweb"` to discover controllers, services, and configurations from all modules.
- **Task Scheduling**: Activates Spring's `@EnableScheduling` engine for daily market data refreshes.
- **Central Configuration**: Houses the root `application.yaml` with server port, logging, OpenAPI, and exchange module settings.
- **Packaging**: Employs `spring-boot-maven-plugin` to generate the deployable archive.

---

## Running the Application

### Via Maven Wrapper
```powershell
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-25'
.\mvnw.cmd spring-boot:run -pl application
```

### Via Packaged JAR
```powershell
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-25'
.\mvnw.cmd clean package -DskipTests
java -jar application\target\application-0.0.1-SNAPSHOT.jar
```

---

## Accessing APIs & Swagger UI

Once running on the default port `8080`:

| Resource | URL |
|---|---|
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **OpenAPI Spec (All)** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| **OpenAPI Spec (NSE)** | [http://localhost:8080/v3/api-docs/nse](http://localhost:8080/v3/api-docs/nse) |
| **NSE Listed Companies** | [http://localhost:8080/nse/listed-companies](http://localhost:8080/nse/listed-companies) |

---

## Project Structure

```
application/
├── src/
│   ├── main/
│   │   ├── java/com/spiderweb/money_matters/
│   │   │   └── MoneyMattersApplication.java    # Spring Boot @SpringBootApplication entry point
│   │   └── resources/
│   │       └── application.yaml                # Central application configuration
│   └── test/
│       └── java/com/spiderweb/money_matters/
│           └── MoneyMattersApplicationTests.java# Full SpringBootTest context verification
└── pom.xml
```

---

## Testing

Run integration tests verifying the full application context and cross-module wiring:

```powershell
.\mvnw.cmd test -pl application
```
