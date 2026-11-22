package com.spiderweb.nse.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Global OpenAPI (Swagger) configuration for the Money Matters application.
 *
 * <p>
 * Registers:
 * <ul>
 *   <li>Top-level {@link OpenAPI} metadata — title, description, version, contact, license, servers.</li>
 *   <li>A {@link GroupedOpenApi} for each logical module so Swagger UI shows separate API groups.</li>
 * </ul>
 *
 * <p>
 * Swagger UI: {@code http://localhost:<port>/swagger-ui.html}<br>
 * Raw OpenAPI JSON: {@code http://localhost:<port>/v3/api-docs}
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    // -------------------------------------------------------------------------
    // Global API info
    // -------------------------------------------------------------------------

    /**
     * Top-level OpenAPI descriptor shared across all module groups.
     *
     * <p>Contains application-wide metadata: title, version, contact, license, and server URLs.</p>
     */
    @Bean
    public OpenAPI moneyMattersOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Money Matters API")
                        .description("""
                                **Money Matters** is a financial data platform that aggregates and exposes
                                market information from Indian stock exchanges.

                                ### Available Modules
                                | Module | Description |
                                |--------|-------------|
                                | **NSE** | National Stock Exchange of India — listed equity data |

                                ### Data Sources
                                - NSE listed companies: `EQUITY_L.csv` from [nsearchives.nseindia.com](https://nsearchives.nseindia.com)

                                ### Notes
                                - All market data is refreshed daily at **07:00 AM IST**.
                                - Timestamps are returned in **ISO-8601 UTC** format.
                                """)
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("SpiderWeb")
                                .email("support@spiderweb.com")
                                .url("https://github.com/dineshkumarmoorthi19/money-matters"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://github.com/dineshkumarmoorthi19/money-matters#readme"));
    }

    // -------------------------------------------------------------------------
    // Module-level API groups
    // -------------------------------------------------------------------------

    /**
     * OpenAPI group for the NSE (National Stock Exchange) module.
     *
     * <p>
     * Groups all endpoints under the {@code /nse/**} path prefix and tagged with {@code NSE}.
     * Visible in Swagger UI via the top-right group selector.
     * </p>
     */
    @Bean
    public GroupedOpenApi nseApiGroup() {
        return GroupedOpenApi.builder()
                .group("nse")
                .displayName("NSE — National Stock Exchange")
                .pathsToMatch("/nse/**")
                .build();
    }

}
