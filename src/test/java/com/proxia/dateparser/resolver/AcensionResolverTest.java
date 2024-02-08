package com.proxia.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcensionResolverTest {
    @Test
    public void test2024() {
        AscensionResolver pr = new AscensionResolver();
        LocalDate ascension = pr.resolvePivotDate(2024, ZoneId.of("Europe/Paris"));

        assertEquals(9,ascension.getDayOfMonth());
        assertEquals(5,ascension.getMonthValue());
        assertEquals(2024,ascension.getYear());

        assertEquals(1,pr.pivot(ascension).count());
    }

    @Test
    public void test2025() {
        AscensionResolver pr = new AscensionResolver();
        LocalDate ascension = pr.resolvePivotDate(2025, ZoneId.of("Europe/Paris"));

        assertEquals(29,ascension.getDayOfMonth());
        assertEquals(5,ascension.getMonthValue());
        assertEquals(2025,ascension.getYear());

        assertEquals(1,pr.pivot(ascension).count());
    }
}
