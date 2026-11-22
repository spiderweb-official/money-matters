package com.spiderweb.nse.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NseLocalDateConverterTest {

    private final NseLocalDateConverter converter = new NseLocalDateConverter();

    @Test
    void testConvertValidUppercaseDates() throws Exception {
        Object result = converter.convert("15-MAY-2024");
        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 5, 15), result);

        result = converter.convert("06-OCT-2008");
        assertNotNull(result);
        assertEquals(LocalDate.of(2008, 10, 6), result);
    }

    @Test
    void testConvertEmptyOrDashValue() throws Exception {
        assertNull(converter.convert(null));
        assertNull(converter.convert("   "));
        assertNull(converter.convert("-"));
    }

}
