package com.spiderweb.nse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Spring configuration for the NSE module's HTTP infrastructure.
 */
@Configuration
public class NseClientConfig {

    /**
     * Provides a shared {@link RestClient} instance for the NSE module.
     * <p>
     * A dedicated bean (rather than auto-wiring {@code RestClient.Builder} directly in the client)
     * allows future customisation — e.g., adding a custom timeout, logging interceptors, or
     * a retry handler — without changing {@code NseHttpClient}.
     * </p>
     */
    @Bean
    public RestClient nseRestClient() {
        return RestClient.create();
    }

}
