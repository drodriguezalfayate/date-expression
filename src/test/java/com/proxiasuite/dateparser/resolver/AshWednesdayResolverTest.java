package com.proxiasuite.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AshWednesdayResolverTest {
    @Test
    public void test2024() {
        AshWednesdayResolver awr = new AshWednesdayResolver();
        LocalDate pivot = awr.resolvePivotDate(2024, ZoneId.of("Europe/Madrid"));
        assertEquals(DayOfWeek.WEDNESDAY,pivot.getDayOfWeek());
        assertEquals(2,pivot.getMonthValue());
        assertEquals(14,pivot.getDayOfMonth());
        assertEquals(2024,pivot.getYear());
        assertEquals(1,awr.pivot(pivot).count());
    }

    @Test
    public void test2025() {
        AshWednesdayResolver awr = new AshWednesdayResolver();
        LocalDate pivot = awr.resolvePivotDate(2025, ZoneId.of("Europe/Madrid"));
        assertEquals(DayOfWeek.WEDNESDAY,pivot.getDayOfWeek());
        assertEquals(3,pivot.getMonthValue());
        assertEquals(5,pivot.getDayOfMonth());
        assertEquals(2025,pivot.getYear());
        assertEquals(1,awr.pivot(pivot).count());
    }
}
