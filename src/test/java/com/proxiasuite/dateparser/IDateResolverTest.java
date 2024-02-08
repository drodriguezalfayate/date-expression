package com.proxiasuite.dateparser;

import com.proxiasuite.dateparser.resolver.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class IDateResolverTest {
    @Test
    public void testLeapYear() {
        IDateResolver dateResolver = IDateResolver.getInstance(DateType.February);
        assertNotNull(dateResolver);

        // pedimos 5 años, y vemos cuantos años bisiestos tenemos...
        int numOfLeapYears = 0;
        LocalDate ld = LocalDate.now();
        // Si no estamos en febrero
        if(ld.getMonthValue() <= 2) {
            if(ld.isLeapYear()) numOfLeapYears=2;
        } else {
            numOfLeapYears = 1;
        }

        // Siempre resolvemos un periodo de 5 años, nos tenemos que asegurar que al menos
        // nos caiga un bisiesto... febrero tiene 28 días, con lo cual tenemos 5*28 + 1 días.
        List<LocalDate> dates = dateResolver.resolve(5, true,new Locale("es","ES"), ZoneId.of("Europe/Madrid")).getDates();
        assertNotNull(dates);
        assertEquals(28*5+numOfLeapYears,dates.size());


        // Tenemos que comprobar que todos los días son febrero.
        assertTrue(dates.stream().allMatch(l->l.getMonth() == Month.FEBRUARY));
    }

    @Test
    public void testYears() {
        LocalDate ld = LocalDate.now();

        if(ld.getMonthValue()!=12) {
            Month month = Month.of(ld.getMonth()==Month.JANUARY?3:ld.getMonthValue()+1);
            MonthResolver mr = new MonthResolver(month);
            List<LocalDate> dates = mr.resolve(2,true,new Locale("es","ES"), ZoneId.of("Europe/Madrid")).getDates();
            int daysOfMonth = dates.get(0).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();

            assertEquals(daysOfMonth*2,dates.size());
            assertTrue(IntStream.range(0,daysOfMonth).anyMatch(i->dates.get(i).getYear() == ld.getYear()));
            assertTrue(IntStream.range(daysOfMonth,2*daysOfMonth).anyMatch(i->dates.get(i).getYear() == ld.getYear()+1));

        } else {
            MonthResolver mr = new MonthResolver(Month.JANUARY);
            List<LocalDate> dates = mr.resolve(2,true,new Locale("es","ES"), ZoneId.of("Europe/Madrid")).getDates();
            int daysOfMonth = dates.get(0).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();

            assertEquals(daysOfMonth*2,dates.size());
            assertTrue(IntStream.range(0,daysOfMonth).anyMatch(i->dates.get(i).getYear() == ld.getYear()+1));
            assertTrue(IntStream.range(daysOfMonth,2*daysOfMonth).anyMatch(i->dates.get(i).getYear() == ld.getYear()+2));

        }
    }

    @Test
    public void testResolvers() {
        assertEquals(AscensionResolver.class,IDateResolver.getInstance(DateType.Ascension).getClass());
        assertEquals(AshWednesdayResolver.class,IDateResolver.getInstance(DateType.AshWednesday).getClass());
        assertEquals(CarnivalResolver.class,IDateResolver.getInstance(DateType.Carnival).getClass());
        assertEquals(CorpusChristiResolver.class,IDateResolver.getInstance(DateType.CorpusChristi).getClass());
        assertEquals(EasterResolver.class,IDateResolver.getInstance(DateType.Easter).getClass());
        assertEquals(HolyWeekResolver.class,IDateResolver.getInstance(DateType.HolyWeek).getClass());
        assertEquals(PentecostResolver.class,IDateResolver.getInstance(DateType.Pentecost).getClass());
        for(DateType dt: DateType.values()) {
            assertNotNull(IDateResolver.getInstance(dt));
        }
    }
}
