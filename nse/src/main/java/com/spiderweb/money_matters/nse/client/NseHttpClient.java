package com.spiderweb.money_matters.nse.client;

import com.spiderweb.nse.config.NseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Low-level HTTP client responsible for fetching the EQUITY_L.csv file from NSE archives.
 *
 * <h3>Why a two-step request?</h3>
 * <p>
 * NSE's CDN (Akamai) blocks automated requests that arrive without valid browser-like cookies.
 * The session cookies ({@code nsit}, {@code bm_sv}, etc.) are issued only after a legitimate
 * browser-style GET to the NSE homepage. This client replicates that handshake:
 * </p>
 * <ol>
 *   <li>GET {@code https://www.nseindia.com} → capture {@code Set-Cookie} headers.</li>
 *   <li>GET the CSV URL carrying those cookies + a {@code Referer} header.</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NseHttpClient {

    private final NseProperties properties;
    private final RestClient restClient;

    public String fetchEquityCsv() {
        log.info("Fetching EQUITY_L.csv directly from: {}", properties.getCsvUrl());
        try {
            String csv = downloadCsv(null);
            if (csv != null && !csv.isBlank()) {
                log.info("Successfully downloaded EQUITY_L.csv directly.");
                return csv;
            }
        } catch (Exception ex) {
            log.warn("Direct CSV download failed: {}. Attempting session warm-up fallback...", ex.getMessage());
        }

        log.info("Warming up NSE session via homepage: {}", properties.getBaseUrl());
        String cookieHeader = warmUpAndGetCookies();

        log.info("Fetching EQUITY_L.csv with cookies from: {}", properties.getCsvUrl());
        return downloadCsv(cookieHeader);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Hits the NSE homepage to initialise a session and returns a {@code Cookie} header value
     * built from all cookies received in the response.
     */
    private String warmUpAndGetCookies() {
        ResponseEntity<Void> response = restClient.get()
                .uri(properties.getBaseUrl())
                .header(HttpHeaders.USER_AGENT, properties.getUserAgent())
                .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
                .retrieve()
                .toBodilessEntity();

        List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookies == null || setCookies.isEmpty()) {
            log.warn("No cookies received from NSE homepage warm-up — CSV fetch may be blocked.");
            return "";
        }

        // Build a single Cookie header value: name=value; name=value; ...
        String cookieHeader = setCookies.stream()
                .map(raw -> raw.split(";")[0].strip())   // keep only name=value, discard attributes
                .collect(Collectors.joining("; "));

        log.debug("Acquired {} cookie(s) from NSE homepage.", setCookies.size());
        return cookieHeader;
    }

    /**
     * Downloads the EQUITY_L.csv file using the pre-acquired session cookies.
     *
     * @param cookieHeader the {@code Cookie} request header value.
     * @return raw CSV text.
     */
    private String downloadCsv(String cookieHeader) {
        RestClient.RequestHeadersSpec<?> spec = restClient.get()
                .uri(properties.getCsvUrl())
                .header(HttpHeaders.USER_AGENT, properties.getUserAgent())
                .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,text/csv")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9");

        if (cookieHeader != null && !cookieHeader.isBlank()) {
            spec = spec.header(HttpHeaders.COOKIE, cookieHeader)
                       .header(HttpHeaders.REFERER, properties.getBaseUrl());
        }

        return spec.retrieve().body(String.class);
    }

}
