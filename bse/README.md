# BSE Module (`bse`)

The `bse` module is dedicated to integrating market data from the **Bombay Stock Exchange (BSE)**.

---

## Current Status

- **Status:** Initial scaffolding / boilerplate module.
- **Role:** Library module consumed by the root `application` runner module.

---

## Planned Capabilities

1. **BSE Equity Directory**: Ingestion and caching of listed securities from BSE India archives.
2. **BSE Bhavcopy Processing**: Daily bhavcopy data ingestion for closing prices, volume, and turnover.
3. **Indices**: BSE Sensex, BSE 100, BSE 500 benchmark feeds.
4. **REST APIs & OpenAPI**: Dedicated `/bse/**` endpoints grouped into Swagger UI alongside `/nse/**`.

---

## Project Structure

```
bse/
├── src/
│   ├── main/
│   │   └── java/com/spiderweb/
│   │       └── Main.java       # Initial starter entry
│   └── test/
└── pom.xml
```

---

## Building and Testing

```powershell
.\mvnw.cmd test -pl bse
```
