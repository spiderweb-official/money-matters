package com.spiderweb.money_matters.nse.controller;

import com.spiderweb.money_matters.nse.model.ListedCompany;
import com.spiderweb.money_matters.nse.service.NseListedCompaniesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * REST controller exposing NSE (National Stock Exchange of India) market data endpoints.
 *
 * <h3>Base path</h3>
 * <p>{@code /nse}</p>
 *
 * <h3>Data freshness</h3>
 * <p>
 * All data is served from an in-memory cache populated from NSE's official
 * {@code EQUITY_L.csv} file. The cache is refreshed daily at <strong>07:00 AM IST</strong>
 * and also on application startup. Each response includes an {@code X-Cache-Refreshed-At}
 * header indicating when the cache was last successfully populated.
 * </p>
 */
@RestController
@RequestMapping("/nse")
@RequiredArgsConstructor
@Tag(
        name = "NSE",
        description = """
                Endpoints for National Stock Exchange (NSE) of India market data.

                **Data source:** `EQUITY_L.csv` from [nsearchives.nseindia.com](https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv)

                **Cache refresh:** Daily at 07:00 AM IST (configurable via `nse.refresh-cron` in `application.yaml`).

                **Coverage:** All equity securities currently listed on the NSE, across all series (EQ, BE, SME, etc.).
                """
)
public class NseController {

    private final NseListedCompaniesService nseListedCompaniesService;

    // -------------------------------------------------------------------------
    // GET /nse/listed-companies
    // -------------------------------------------------------------------------

    /**
     * Returns the complete list of equity securities currently listed on the NSE.
     *
     * <p>
     * Data is served from an in-memory cache. The response includes the
     * {@code X-Cache-Refreshed-At} header so clients can detect stale data
     * without polling a separate status endpoint.
     * </p>
     *
     * @return {@code 200 OK} with a JSON array of {@link ListedCompany} objects, or
     *         {@code 204 No Content} if the cache has not yet been populated
     *         (e.g., immediately after a cold start where the initial fetch failed).
     */
    @GetMapping(value = "/listed-companies", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all NSE listed companies",
            description = """
                    Returns the **complete list** of equity securities currently listed on the
                    National Stock Exchange (NSE) of India.

                    ### Response fields
                    | Field | Type | Description |
                    |-------|------|-------------|
                    | `symbol` | string | NSE trading ticker (e.g. `RELIANCE`) |
                    | `name` | string | Full registered company name |
                    | `series` | string | Market segment (`EQ`, `BE`, `SM`, …) |
                    | `dateOfListing` | date | Date first listed on NSE |
                    | `paidUpValue` | decimal | Paid-up value per share (INR) |
                    | `marketLot` | integer | Minimum tradeable lot size |
                    | `isinNumber` | string | 12-character ISIN code |
                    | `faceValue` | decimal | Face (par) value per share (INR) |

                    ### Response headers
                    | Header | Description |
                    |--------|-------------|
                    | `X-Cache-Refreshed-At` | ISO-8601 UTC timestamp of the last successful cache refresh |

                    ### Notes
                    - The list typically contains **5,000 – 6,000** securities.
                    - If the cache has not yet been populated (cold start failure), `204 No Content` is returned.
                    - Data is refreshed from NSE at **07:00 AM IST** every day.
                    """,
            operationId = "getNseListedCompanies"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the full list of NSE-listed companies.",
                    headers = {
                            @Header(
                                    name = "X-Cache-Refreshed-At",
                                    description = "ISO-8601 UTC timestamp of when the in-memory cache was last successfully refreshed from NSE.",
                                    schema = @Schema(type = "string", format = "date-time", example = "2026-11-22T01:30:00Z")
                            )
                    },
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ListedCompany.class),
                                    minItems = 1
                            ),
                            examples = @ExampleObject(
                                    name = "sample",
                                    summary = "Two sample records",
                                    value = """
                                            [
                                              {
                                                "symbol": "RELIANCE",
                                                "name": "Reliance Industries Limited",
                                                "series": "EQ",
                                                "dateOfListing": "1995-11-29",
                                                "paidUpValue": 10.00,
                                                "marketLot": 1,
                                                "isinNumber": "INE002A01018",
                                                "faceValue": 10.00
                                              },
                                              {
                                                "symbol": "TCS",
                                                "name": "Tata Consultancy Services Limited",
                                                "series": "EQ",
                                                "dateOfListing": "2004-08-25",
                                                "paidUpValue": 1.00,
                                                "marketLot": 1,
                                                "isinNumber": "INE467B01029",
                                                "faceValue": 1.00
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Cache not yet populated. This can happen immediately after a cold start if the "
                            + "initial fetch from NSE failed. Retry after a few seconds.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error. Check application logs for details.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = """
                                            {
                                              "timestamp": "2026-11-22T14:00:00Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "path": "/nse/listed-companies"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<List<ListedCompany>> getListedCompanies() {
        List<ListedCompany> companies = nseListedCompaniesService.getListedCompanies();

        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Instant refreshedAt = nseListedCompaniesService.getLastRefreshedAt();
        return ResponseEntity.ok()
                .header("X-Cache-Refreshed-At", refreshedAt != null ? refreshedAt.toString() : "unknown")
                .body(companies);
    }

}
