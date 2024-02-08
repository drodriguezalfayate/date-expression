package com.proxia.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EasterResolverTest {
    @Test
    public void test2024() {
        EasterResolver pr = new EasterResolver();
        LocalDate ld = pr.resolvePivotDate(2024, ZoneId.of("Europe/Paris"));

        assertEquals(2024,ld.getYear());
        assertEquals(3,ld.getMonthValue());
        assertEquals(31,ld.getDayOfMonth());

        assertEquals(7,pr.pivot(ld).count());
    }

}
