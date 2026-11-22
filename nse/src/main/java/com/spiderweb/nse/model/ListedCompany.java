package com.spiderweb.nse.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single equity security listed on the National Stock Exchange (NSE) of India.
 *
 * <p>
 * Fields are mapped directly from the official {@code EQUITY_L.csv} file published at:<br>
 * {@code https://nsearchives.nseindia.com/content/equities/EQUITY_L.csv}
 * </p>
 */
@Data
@NoArgsConstructor
@Schema(
        name = "ListedCompany",
        description = "Details of a single equity security listed on the National Stock Exchange (NSE) of India, "
                + "sourced from the official EQUITY_L.csv file."
)
public class ListedCompany {

    /**
     * The NSE trading symbol (e.g., {@code RELIANCE}, {@code TCS}).
     */
    @CsvBindByName(column = "SYMBOL", required = true)
    @Schema(
            description = "Unique NSE trading symbol for the security.",
            example = "RELIANCE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String symbol;

    /**
     * The full registered name of the company.
     */
    @CsvBindByName(column = "NAME OF COMPANY", required = true)
    @Schema(
            description = "Full registered name of the listed company.",
            example = "Reliance Industries Limited",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    /**
     * The market segment / series (e.g., {@code EQ} for Equity, {@code BE} for Book Entry).
     */
    @CsvBindByName(column = "SERIES")
    @Schema(
            description = "Market segment series code. Common values: EQ (Equity), BE (Book Entry / T+1 settlement), "
                    + "BL (Block Deal), IL (Institutional), IV (Inverse), SM (SME).",
            example = "EQ",
            allowableValues = {"EQ", "BE", "BL", "IL", "IV", "SM", "N", "W"}
    )
    private String series;

    /**
     * The date on which the security was first listed on NSE.
     * CSV format is {@code dd-MMM-yyyy} (e.g., {@code 06-OCT-2008}).
     */
    @com.opencsv.bean.CsvCustomBindByName(column = "DATE OF LISTING", converter = NseLocalDateConverter.class)
    @Schema(
            description = "Date on which the security was first listed on NSE. Format: yyyy-MM-dd (ISO 8601).",
            example = "2008-10-06",
            type = "string",
            format = "date"
    )
    private LocalDate dateOfListing;

    /**
     * Paid-up value per share (in INR).
     */
    @CsvBindByName(column = "PAID UP VALUE")
    @Schema(
            description = "Paid-up value per share in Indian Rupees (INR).",
            example = "10.00"
    )
    private BigDecimal paidUpValue;

    /**
     * Minimum trading lot size.
     */
    @CsvBindByName(column = "MARKET LOT")
    @Schema(
            description = "Minimum number of shares that must be traded in a single transaction (market lot size).",
            example = "1"
    )
    private Integer marketLot;

    /**
     * International Securities Identification Number (e.g., {@code INE002A01018}).
     */
    @CsvBindByName(column = "ISIN NUMBER")
    @Schema(
            description = "ISIN (International Securities Identification Number) — a 12-character alphanumeric "
                    + "code that uniquely identifies the security globally.",
            example = "INE002A01018",
            pattern = "^[A-Z]{2}[A-Z0-9]{9}[0-9]$"
    )
    private String isinNumber;

    /**
     * Face value of the security (in INR).
     */
    @CsvBindByName(column = "FACE VALUE")
    @Schema(
            description = "Face (par) value of the security in Indian Rupees (INR).",
            example = "10.00"
    )
    private BigDecimal faceValue;

}
