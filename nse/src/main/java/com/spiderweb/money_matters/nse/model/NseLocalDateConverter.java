package com.spiderweb.money_matters.nse.model;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 * OpenCSV custom converter to parse case-insensitive listing dates in the format {@code dd-MMM-yyyy}
 * (e.g., {@code 06-OCT-2008}, {@code 15-MAY-2024}).
 */
public class NseLocalDateConverter extends AbstractBeanField<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("dd-MMM-yyyy")
            .toFormatter(Locale.ENGLISH);

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value == null || value.isBlank() || "-".equals(value.trim())) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), FORMATTER);
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException(value, LocalDate.class, e.getMessage());
        }
    }

}
