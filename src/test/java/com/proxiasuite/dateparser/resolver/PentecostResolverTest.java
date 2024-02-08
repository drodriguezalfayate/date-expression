package com.proxiasuite.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PentecostResolverTest {
    @Test
    public void test() {
        PentecostResolver pr = new PentecostResolver();
        LocalDate pentecost = pr.resolvePivotDate(2024, ZoneId.of("Europe/Paris"));
        assertEquals(DayOfWeek.SUNDAY,pentecost.getDayOfWeek());
        assertEquals(19,pentecost.getDayOfMonth());
        assertEquals(5,pentecost.getMonthValue());
        assertEquals(2024,pentecost.getYear());

        assertEquals(2,pr.pivot(pentecost).count());
    }
}
