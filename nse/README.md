# NSE Module (`nse`)

The `nse` module handles data ingestion, caching, and REST endpoints for the **National Stock Exchange of India (NSE)**.

---

## Features

- **Automated Data Sync**: Downloads official `EQUITY_L.csv` from NSE archives.
- **In-Memory Cache**: Zero-latency reads using `AtomicReference<List<ListedCompany>>`.
- **Scheduled Refresh**: Cron-driven daily refresh at `07:00 AM IST` plus eager warming on application startup.
- **Fail-Safe Caching**: If a network fetch or parse fails, the previous valid cache is retained.
- **Interactive OpenAPI / Swagger**: Tagged, grouped, and documented under Swagger UI.
- **Case-Insensitive Date Converter**: Handles all uppercase month formats (`15-MAY-2024`, `06-OCT-2008`) published by NSE.

---

## API Documentation

For the full endpoint specification, schema models, response codes, and examples, see:

📖 **[NSE API Documentation](docs/API.md)**

### Available Endpoints Summary

| Method | Endpoint | Description | Cache Source |
|---|---|---|---|
| `GET` | `/nse/listed-companies` | Get all listed equities on NSE (~2,600+ companies) | `EQUITY_L.csv` in-memory |

---

## Architecture & Components

```
nse/
├── docs/
│   └── API.md                       # API documentation in Markdown
├── src/
│   ├── main/
│   │   ├── java/com/spiderweb/money_matters/nse/
│   │   │   ├── client/
│   │   │   │   └── NseHttpClient.java            # Fetches CSV directly from NSE archives
│   │   │   ├── config/
│   │   │   │   ├── NseClientConfig.java          # RestClient bean provider
│   │   │   │   ├── NseProperties.java            # @ConfigurationProperties for nse.*
│   │   │   │   └── OpenApiConfig.java            # OpenAPI / Swagger UI group configuration
│   │   │   ├── controller/
│   │   │   │   └── NseController.java            # REST controller for /nse endpoints
│   │   │   ├── model/
│   │   │   │   ├── ListedCompany.java            # Model representing an NSE security
│   │   │   │   └── NseLocalDateConverter.java    # OpenCSV case-insensitive date converter
│   │   │   └── service/
│   │   │       └── NseListedCompaniesService.java# Caching & scheduled refresh logic
│   │   └── resources/
│   │       └── application.yaml                  # Module default properties
│   └── test/
│       └── java/com/spiderweb/money_matters/nse/
│           └── model/
│               └── NseLocalDateConverterTest.java# Unit tests for date parser
└── pom.xml
```

---

## Configuration Properties

Properties can be overridden in `application/src/main/resources/application.yaml` or via environment variables:

| Property | Default Value | Description |
|---|---|---|
| `nse.base-url` | `https://www.nseindia.com` | NSE homepage base URL used for fallback session cookies |
| `nse.csv-url` | `https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv` | Direct download URL for listed companies CSV |
| `nse.user-agent` | `Mozilla/5.0 ...` | Browser User-Agent header to avoid Akamai CDN blocks |
| `nse.refresh-cron` | `0 0 7 * * *` | Spring Cron expression for daily cache refresh |

---

## Running Module Tests

To run the unit tests within the `nse` module:

```powershell
.\mvnw.cmd test -pl nse
```
