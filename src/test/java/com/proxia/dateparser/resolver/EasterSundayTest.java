package com.proxia.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EasterSundayTest {
    @Test
    public void test2024() {
        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2024, ZoneId.of("Europe/Madrid"));
        assertEquals(2024,ld.getYear());
        assertEquals(3,ld.getMonthValue());
        assertEquals(31,ld.getDayOfMonth());
    }

    @Test
    public void test2025() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2025, ZoneId.of("Europe/Madrid"));
        assertEquals(2025,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(20,ld.getDayOfMonth());
    }

    @Test
    public void test2026() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2026, ZoneId.of("Europe/Madrid"));
        assertEquals(2026,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(5,ld.getDayOfMonth());
    }


    @Test
    public void test2027() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2027, ZoneId.of("Europe/Madrid"));
        assertEquals(2027,ld.getYear());
        assertEquals(3,ld.getMonthValue());
        assertEquals(28,ld.getDayOfMonth());
    }


    @Test
    public void test2028() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2028, ZoneId.of("Europe/Madrid"));
        assertEquals(2028,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(16,ld.getDayOfMonth());
    }

    @Test
    public void test2029() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2029, ZoneId.of("Europe/Madrid"));
        assertEquals(2029,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(1,ld.getDayOfMonth());
    }

    @Test
    public void test2030() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2030, ZoneId.of("Europe/Madrid"));
        assertEquals(2030,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(21,ld.getDayOfMonth());
    }



    @Test
    public void test2007() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2007, ZoneId.of("Europe/Madrid"));
        assertEquals(2007,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(8,ld.getDayOfMonth());
    }

    @Test
    public void test2000() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(2000, ZoneId.of("Europe/Madrid"));
        assertEquals(2000,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(23,ld.getDayOfMonth());
    }

    @Test
    public void test1900() {

        LocalDate ld = IEasterBaseResolver.computeEasterSunday(1900, ZoneId.of("Europe/Madrid"));
        assertEquals(1900,ld.getYear());
        assertEquals(4,ld.getMonthValue());
        assertEquals(15,ld.getDayOfMonth());
    }
}
