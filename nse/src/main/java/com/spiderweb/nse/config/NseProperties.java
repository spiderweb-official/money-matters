package com.spiderweb.nse.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized configuration properties for the NSE data integration module.
 * <p>
 * All properties are prefixed with {@code nse} in {@code application.yaml}.
 * </p>
 *
 * <pre>
 * nse:
 *   base-url: https://www.nseindia.com
 *   csv-url: https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv
 *   user-agent: "Mozilla/5.0 ..."
 *   refresh-cron: "0 0 7 * * *"
 * </pre>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nse")
public class NseProperties {

    /**
     * NSE homepage URL used to warm up the HTTP session and acquire cookies.
     * Default: {@code https://www.nseindia.com}
     */
    private String baseUrl = "https://www.nseindia.com";

    /**
     * Direct URL for the EQUITY_L.csv file containing all listed equities.
     * Default: {@code https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv}
     */
    private String csvUrl = "https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv";

    /**
     * Browser-like User-Agent string sent with all NSE requests to avoid 403 blocks.
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /**
     * Spring cron expression controlling how often the listed companies cache is refreshed.
     * Default: every day at 07:00 AM (IST markets open at 09:15 AM).
     */
    private String refreshCron = "0 0 7 * * *";

}
