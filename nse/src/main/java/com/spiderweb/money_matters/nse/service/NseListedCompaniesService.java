package com.spiderweb.money_matters.nse.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.spiderweb.money_matters.nse.client.NseHttpClient;
import com.spiderweb.money_matters.nse.model.ListedCompany;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service that manages the in-memory cache of NSE-listed companies.
 *
 * <h3>Caching strategy</h3>
 * <ul>
 *   <li>An {@link AtomicReference} holds the latest parsed list — thread-safe and lock-free for reads.</li>
 *   <li>{@link PostConstruct} triggers an eager load on application startup.</li>
 *   <li>{@link Scheduled} triggers a daily refresh at 07:00 AM (configurable via {@code nse.refresh-cron}).</li>
 * </ul>
 *
 * <h3>Fault tolerance</h3>
 * <p>
 * If the fetch or parse fails (e.g., NSE is down or returns a 403), the error is logged and
 * the cache retains its previous value so existing callers continue to receive stale-but-valid data.
 * On initial startup failure, an empty list is returned until the next scheduled refresh succeeds.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NseListedCompaniesService {

    private final NseHttpClient nseHttpClient;

    /**
     * Thread-safe cache of the latest successfully parsed company list.
     * Initialised to an empty list so callers never receive {@code null}.
     */
    private final AtomicReference<List<ListedCompany>> cache = new AtomicReference<>(Collections.emptyList());

    /**
     * Timestamp of the last successful cache refresh (for observability).
     */
    private volatile Instant lastRefreshedAt;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the cached list of all NSE-listed companies.
     * <p>
     * The returned list is immutable. It reflects the state as of the last successful
     * refresh and is never {@code null}.
     * </p>
     *
     * @return an unmodifiable {@link List} of {@link ListedCompany}.
     */
    public List<ListedCompany> getListedCompanies() {
        return cache.get();
    }

    /**
     * Returns the {@link Instant} at which the cache was last successfully refreshed,
     * or {@code null} if no successful refresh has occurred yet.
     */
    public Instant getLastRefreshedAt() {
        return lastRefreshedAt;
    }

    // -------------------------------------------------------------------------
    // Cache refresh lifecycle
    // -------------------------------------------------------------------------

    /**
     * Eagerly loads the company list on application startup.
     * <p>
     * Runs synchronously during context initialisation. If this fails,
     * the application still starts with an empty cache.
     * </p>
     */
    @PostConstruct
    public void loadOnStartup() {
        log.info("NSE listed companies: performing initial cache load on startup.");
        refreshCache();
    }

    /**
     * Scheduled daily refresh. The cron expression is driven by {@code nse.refresh-cron}
     * (default: {@code 0 0 7 * * *} → every day at 07:00 AM).
     * <p>
     * {@code @Scheduled} with a cron string cannot directly read a property expression
     * into the annotation at compile time when using external {@code @ConfigurationProperties}.
     * We therefore hard-code the placeholder here; override via {@code application.yaml}.
     * </p>
     */
    @Scheduled(cron = "${nse.refresh-cron:0 0 7 * * *}")
    public void scheduledRefresh() {
        log.info("NSE listed companies: running scheduled daily cache refresh.");
        refreshCache();
    }

    // -------------------------------------------------------------------------
    // Core refresh logic
    // -------------------------------------------------------------------------

    /**
     * Fetches the EQUITY_L.csv from NSE, parses it into {@link ListedCompany} beans,
     * and atomically replaces the cache.
     *
     * <p>Any exception is caught and logged; the existing cache value is preserved on failure.</p>
     */
    private void refreshCache() {
        try {
            String rawCsv = nseHttpClient.fetchEquityCsv();
            if (rawCsv == null || rawCsv.isBlank()) {
                log.warn("NSE returned an empty or null CSV body. Cache not updated.");
                return;
            }

            List<ListedCompany> companies = parseCsv(rawCsv);
            cache.set(Collections.unmodifiableList(companies));
            lastRefreshedAt = Instant.now();

            log.info("NSE cache refreshed successfully. Total companies loaded: {}", companies.size());

        } catch (Exception ex) {
            log.error("Failed to refresh NSE listed companies cache. Retaining previous data. Cause: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Parses a raw CSV string into a list of {@link ListedCompany} beans using opencsv.
     *
     * <p>
     * {@code withIgnoreLeadingWhiteSpace(true)} handles the occasional leading space
     * in NSE's CSV header values. {@code withIgnoreEmptyLine(true)} skips blank trailing lines.
     * </p>
     *
     * @param rawCsv the full CSV text from EQUITY_L.csv.
     * @return parsed list of {@link ListedCompany} objects.
     */
    private List<ListedCompany> parseCsv(String rawCsv) {
        return new CsvToBeanBuilder<ListedCompany>(new StringReader(rawCsv))
                .withType(ListedCompany.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withSkipLines(0)      // first row is header — opencsv reads it automatically via @CsvBindByName
                .build()
                .parse();
    }

}
