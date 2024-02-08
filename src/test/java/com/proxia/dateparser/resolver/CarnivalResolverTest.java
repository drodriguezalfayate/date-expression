package com.proxia.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CarnivalResolverTest {
    @Test
    public void test2024() {
        CarnivalResolver awr = new CarnivalResolver();
        LocalDate pivot = awr.resolvePivotDate(2024, ZoneId.of("Europe/Madrid"));
        assertEquals(DayOfWeek.SATURDAY,pivot.getDayOfWeek());
        assertEquals(2,pivot.getMonthValue());
        assertEquals(10,pivot.getDayOfMonth());
        assertEquals(2024,pivot.getYear());
        assertEquals(4,awr.pivot(pivot).count());
    }

}
