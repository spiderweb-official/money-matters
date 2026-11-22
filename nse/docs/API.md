# NSE Module API Documentation

> **Base Path:** `/nse`  
> **OpenAPI Group:** `nse` (`NSE — National Stock Exchange`)  
> **Swagger UI URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
> **OpenAPI JSON Spec:** [http://localhost:8080/v3/api-docs/nse](http://localhost:8080/v3/api-docs/nse)  

---

## Overview

The **NSE (National Stock Exchange of India)** module provides RESTful APIs for accessing financial and security listings from the official NSE archives.

All data is cached in memory and refreshed automatically each morning at **07:00 AM IST** (configurable via `nse.refresh-cron`). An eager cache warming is performed on application startup to ensure immediate responsiveness.

---

## Endpoints

### 1. Get All Listed Companies

Retrieves the complete list of equity securities actively listed on the National Stock Exchange of India.

```http
GET /nse/listed-companies
```

- **Operation ID:** `getNseListedCompanies`
- **Tag:** `NSE`
- **Data Source:** `EQUITY_L.csv` from `https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv`

#### Request Parameters
*None.*

#### Request Headers
| Header | Type | Required | Description |
|---|---|---|---|
| `Accept` | `string` | Optional | Defaults to `application/json` |

---

#### Response

##### `200 OK`
Returned when the cache has successfully populated and contains listed companies.

**Response Headers:**
| Header | Type | Description |
|---|---|---|
| `X-Cache-Refreshed-At` | `string` (ISO-8601 UTC) | Timestamp of when the cache was last refreshed from NSE archives (e.g. `2026-11-22T16:04:53.111Z`). |

**Response Body:** `application/json` (Array of objects)

| Field | Type | Required | Description | Example |
|---|---|---|---|---|
| `symbol` | `string` | Yes | Unique NSE trading ticker symbol | `"RELIANCE"` |
| `name` | `string` | Yes | Official registered company name | `"Reliance Industries Limited"` |
| `series` | `string` | No | Market segment series code (`EQ`, `BE`, `SM`, etc.) | `"EQ"` |
| `dateOfListing` | `string` (`date`) | No | First listing date on NSE (`yyyy-MM-dd`) | `"1995-11-29"` |
| `paidUpValue` | `number` (`decimal`) | No | Paid-up value per share in Indian Rupees (INR) | `10.0` |
| `marketLot` | `integer` (`int32`) | No | Minimum trading lot size | `1` |
| `isinNumber` | `string` | No | 12-character global ISIN code | `"INE002A01018"` |
| `faceValue` | `number` (`decimal`) | No | Face / par value per share in Indian Rupees (INR) | `10.0` |

**Sample Response Body:**
```json
[
  {
    "symbol": "RELIANCE",
    "name": "Reliance Industries Limited",
    "series": "EQ",
    "dateOfListing": "1995-11-29",
    "paidUpValue": 10.0,
    "marketLot": 1,
    "isinNumber": "INE002A01018",
    "faceValue": 10.0
  },
  {
    "symbol": "TCS",
    "name": "Tata Consultancy Services Limited",
    "series": "EQ",
    "dateOfListing": "2004-08-25",
    "paidUpValue": 1.0,
    "marketLot": 1,
    "isinNumber": "INE467B01029",
    "faceValue": 1.0
  },
  {
    "symbol": "INFY",
    "name": "Infosys Limited",
    "series": "EQ",
    "dateOfListing": "1995-02-08",
    "paidUpValue": 5.0,
    "marketLot": 1,
    "isinNumber": "INE009A01021",
    "faceValue": 5.0
  }
]
```

##### `204 No Content`
Returned if the cache is currently empty (e.g. cold start when the network fetch fails and no previous data is available).

##### `500 Internal Server Error`
Returned on unexpected server processing errors.

```json
{
  "timestamp": "2026-11-22T16:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/nse/listed-companies"
}
```

---

## Example Invocations

### cURL
```bash
curl -i -X GET "http://localhost:8080/nse/listed-companies" \
     -H "Accept: application/json"
```

### PowerShell
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8080/nse/listed-companies" -Method GET
$response | Select-Object -First 5
```

### JavaScript (fetch)
```javascript
const response = await fetch('http://localhost:8080/nse/listed-companies', {
  headers: { 'Accept': 'application/json' }
});

const refreshedAt = response.headers.get('X-Cache-Refreshed-At');
const companies = await response.json();
console.log(`Loaded ${companies.length} companies (Last refreshed: ${refreshedAt})`);
```
