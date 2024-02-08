package com.proxia.dateparser.resolver;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorpusResolverTest {
    @Test
    public void test() {
        CorpusChristiResolver pr = new CorpusChristiResolver();
        LocalDate corpus = pr.resolvePivotDate(2024, ZoneId.of("Europe/Paris"));
        assertEquals(DayOfWeek.THURSDAY,corpus.getDayOfWeek());
        assertEquals(30,corpus.getDayOfMonth());
        assertEquals(5,corpus.getMonthValue());
        assertEquals(2024,corpus.getYear());

        assertEquals(1,pr.pivot(corpus).count());
    }
}
