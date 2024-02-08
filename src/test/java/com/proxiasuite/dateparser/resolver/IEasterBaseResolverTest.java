package com.proxiasuite.dateparser.resolver;

import com.proxiasuite.dateparser.DateExpression;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IEasterBaseResolverTest {
    class Sample implements IEasterBaseResolver {

        @Override
        public LocalDate resolvePivotDate(int year, ZoneId zoneId) {
            LocalDate ld = LocalDate.now(zoneId);
            ld = ld.withYear(year);
            return ld;
        }

        @Override
        public Stream<LocalDate> pivot(LocalDate pivot) {
            return Stream.of(pivot,pivot.plusDays(1),pivot.plusDays(2));
        }
    }

    @Test
    public void test() {
        Sample s = new Sample();
        DateExpression expr = s.resolve(3,false, new Locale("es","ES"), ZoneId.of("Europe/Madrid"));
        assertNotNull(expr);
        assertNotNull(expr.getDates());
        assertEquals(9,expr.getDates().stream().count());
        LocalDate now = LocalDate.now(ZoneId.of("Europe/Madrid"));
        assertEquals(now,expr.getDates().get(0));
        assertEquals(now.getYear()+1,expr.getDates().get(3).getYear());
        assertEquals(now.getDayOfMonth(),expr.getDates().get(3).getDayOfMonth());
        assertEquals(now.getMonthValue(),expr.getDates().get(3).getMonthValue());

        assertEquals(now.getYear()+2,expr.getDates().get(6).getYear());
        assertEquals(now.getDayOfMonth(),expr.getDates().get(6).getDayOfMonth());
        assertEquals(now.getMonthValue(),expr.getDates().get(6).getMonthValue());

    }
}
