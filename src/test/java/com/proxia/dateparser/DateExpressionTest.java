package com.proxia.dateparser;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class DateExpressionTest {
    @Test
    public void testMonthAt() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.AUGUST.getValue()).withYear(2024);

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(),selectedMonth.getYear()+2).forEach(year->{
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i->current.with(ChronoField.DAY_OF_MONTH,i)).collect(()->dates,(a, b)->a.add(b),(a, b)->a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), true, dates);

        DateExpression at = dt.at(1,3,15);
        assertEquals(6,at.getDates().stream().count());

        assertEquals(at.getDates().get(0).getYear(),2024);
        assertEquals(at.getDates().get(0).getMonthValue(),8);
        assertEquals(at.getDates().get(0).getDayOfMonth(),1);

        assertEquals(at.getDates().get(1).getYear(),2024);
        assertEquals(at.getDates().get(1).getMonthValue(),8);
        assertEquals(at.getDates().get(1).getDayOfMonth(),3);

        assertEquals(at.getDates().get(2).getYear(),2024);
        assertEquals(at.getDates().get(2).getMonthValue(),8);
        assertEquals(at.getDates().get(2).getDayOfMonth(),15);


        assertEquals(at.getDates().get(3).getYear(),2025);
        assertEquals(at.getDates().get(3).getMonthValue(),8);
        assertEquals(at.getDates().get(3).getDayOfMonth(),1);

        assertEquals(at.getDates().get(4).getYear(),2025);
        assertEquals(at.getDates().get(4).getMonthValue(),8);
        assertEquals(at.getDates().get(4).getDayOfMonth(),3);

        assertEquals(at.getDates().get(5).getYear(),2025);
        assertEquals(at.getDates().get(5).getMonthValue(),8);
        assertEquals(at.getDates().get(5).getDayOfMonth(),15);

        /*
        at = dt.atWeek(2);
        assertEquals(14,at.getDates().stream().count());
        assertEquals(at.getDates().get(0).getYear(),2024);
        assertEquals(at.getDates().get(0).getMonthValue(),8);
        assertEquals(at.getDates().get(0).getDayOfMonth(),5);

         */

    }

    @Test
    public void testMonthAtNegativeIndex() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(),selectedMonth.getYear()+2).forEach(year->{
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i->current.with(ChronoField.DAY_OF_MONTH,i)).collect(()->dates,(a, b)->a.add(b),(a, b)->a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), true, dates);

        DateExpression at = dt.at(1,-1,-2);
        assertEquals(6,at.getDates().stream().count());

        assertEquals(at.getDates().get(0).getYear(),2024);
        assertEquals(at.getDates().get(0).getMonthValue(),2);
        assertEquals(at.getDates().get(0).getDayOfMonth(),1);


        assertEquals(at.getDates().get(1).getYear(),2024);
        assertEquals(at.getDates().get(1).getMonthValue(),2);
        assertEquals(at.getDates().get(1).getDayOfMonth(),28);

        assertEquals(at.getDates().get(2).getYear(),2024);
        assertEquals(at.getDates().get(2).getMonthValue(),2);
        assertEquals(at.getDates().get(2).getDayOfMonth(),29);




        assertEquals(at.getDates().get(3).getYear(),2025);
        assertEquals(at.getDates().get(3).getMonthValue(),2);
        assertEquals(at.getDates().get(3).getDayOfMonth(),1);

        assertEquals(at.getDates().get(4).getYear(),2025);
        assertEquals(at.getDates().get(4).getMonthValue(),2);
        assertEquals(at.getDates().get(4).getDayOfMonth(),27);

        assertEquals(at.getDates().get(5).getYear(),2025);
        assertEquals(at.getDates().get(5).getMonthValue(),2);
        assertEquals(at.getDates().get(5).getDayOfMonth(),28);



    }

    @Test
    public void testMonthWeekDay() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), true, dates);
        DateExpression at = dt.at(new DayOfWeek[]{DayOfWeek.SATURDAY},1,-1);

        // Tenemso que tener dos sabados por año.... => 4 entradas.
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(1).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(2).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(3).getDayOfWeek());

        assertEquals(2024,at.getDates().get(0).getYear());
        assertEquals(2024,at.getDates().get(1).getYear());
        assertEquals(2025,at.getDates().get(2).getYear());
        assertEquals(2025,at.getDates().get(3).getYear());


        assertEquals(2,at.getDates().get(0).getMonthValue());
        assertEquals(2,at.getDates().get(1).getMonthValue());
        assertEquals(2,at.getDates().get(2).getMonthValue());
        assertEquals(2,at.getDates().get(3).getMonthValue());

        assertEquals(3,at.getDates().get(0).getDayOfMonth());
        assertEquals(24,at.getDates().get(1).getDayOfMonth());
        assertEquals(1,at.getDates().get(2).getDayOfMonth());
        assertEquals(22,at.getDates().get(3).getDayOfMonth());

    }

    @Test
    public void testMonthWeekDay2Days() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), true, dates);
        DateExpression at = dt.at(new DayOfWeek[]{DayOfWeek.THURSDAY,DayOfWeek.SATURDAY},1,-1);

        // Tenemso que tener dos sabados por año y dos miercoles, por año,
        //
        // 2024. Jueves, Sabado, sabado, Jueves
        assertEquals(DayOfWeek.THURSDAY,at.getDates().get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(1).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(2).getDayOfWeek());
        assertEquals(DayOfWeek.THURSDAY,at.getDates().get(3).getDayOfWeek());

        // En 2025, las entradas son SABADO, JUEVES, SABADO JUEVES
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(4).getDayOfWeek());
        assertEquals(DayOfWeek.THURSDAY,at.getDates().get(5).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(6).getDayOfWeek());
        assertEquals(DayOfWeek.THURSDAY,at.getDates().get(7).getDayOfWeek());

        for(int i=0;i<at.getDates().size();i++) {
            assertEquals(2024+i/4,at.getDates().get(i).getYear());
            assertEquals(2,at.getDates().get(i).getMonthValue());
        }

        // Y ahora los días.
        assertEquals(1,at.getDates().get(0).getDayOfMonth());
        assertEquals(3,at.getDates().get(1).getDayOfMonth());
        assertEquals(24,at.getDates().get(2).getDayOfMonth());
        assertEquals(29,at.getDates().get(3).getDayOfMonth());

        // En 2025, las entradas son SABADO, JUEVES, SABADO JUEVES
        assertEquals(1,at.getDates().get(4).getDayOfMonth());
        assertEquals(6,at.getDates().get(5).getDayOfMonth());
        assertEquals(22,at.getDates().get(6).getDayOfMonth());
        assertEquals(27,at.getDates().get(7).getDayOfMonth());

    }

    @Test
    public void testWeekAtFullWeeks() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), true, dates);
        DateExpression at = dt.atWeek(1,-1); // Primera y última semana
        assertEquals(28,at.getDates().size());

        assertEquals(DayOfWeek.MONDAY,at.getDates().get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(6).getDayOfWeek());
        assertEquals(DayOfWeek.MONDAY,at.getDates().get(7).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(13).getDayOfWeek());

        assertEquals(DayOfWeek.MONDAY,at.getDates().get(14).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(20).getDayOfWeek());
        assertEquals(DayOfWeek.MONDAY,at.getDates().get(21).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(27).getDayOfWeek());

    }


    @Test
    public void testWeekAtFullWeeksWeekStartOnSunday() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("en","US"), true, dates);
        DateExpression at = dt.atWeek(1,-1); // Primera y última semana
        assertEquals(28,at.getDates().size());

        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(6).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(7).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(13).getDayOfWeek());

        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(14).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(20).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(21).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(27).getDayOfWeek());

    }

    @Test
    public void testWeekAtPartialWeeks() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), false, dates);
        DateExpression at = dt.atWeek(1,-1); // Primera y última semana
        assertEquals(8+7,at.getDates().size()); // En febrero 2024, tnemos 4 días la priemra semana y 4 la última y en 2025 tenmos 2 días la primera semana y 5 la última

        assertEquals(DayOfWeek.THURSDAY,at.getDates().get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(3).getDayOfWeek());
        assertEquals(DayOfWeek.MONDAY,at.getDates().get(4).getDayOfWeek());
        assertEquals(DayOfWeek.THURSDAY,at.getDates().get(7).getDayOfWeek());

        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(8).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(9).getDayOfWeek());
        assertEquals(DayOfWeek.MONDAY,at.getDates().get(10).getDayOfWeek());
        assertEquals(DayOfWeek.FRIDAY,at.getDates().get(14).getDayOfWeek());

    }

    @Test
    public void testWeekends() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es","ES"), false, dates);
        DateExpression at = dt.atWeekend(1,-1); // Primera y última fin de semana
        assertEquals(8,at.getDates().size());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(1).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(2).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(3).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(4).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(5).getDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY,at.getDates().get(6).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY,at.getDates().get(7).getDayOfWeek());


        for(int i=0;i<at.getDates().size();i++) {
            assertEquals(2024+i/4,at.getDates().get(i).getYear());
            assertEquals(2,at.getDates().get(i).getMonthValue());
        }

        assertEquals(3,at.getDates().get(0).getDayOfMonth());
        assertEquals(4,at.getDates().get(1).getDayOfMonth());
        assertEquals(24,at.getDates().get(2).getDayOfMonth());
        assertEquals(25,at.getDates().get(3).getDayOfMonth());

        // En 2025, las entradas son SABADO, JUEVES, SABADO JUEVES
        assertEquals(1,at.getDates().get(4).getDayOfMonth());
        assertEquals(2,at.getDates().get(5).getDayOfMonth());
        assertEquals(22,at.getDates().get(6).getDayOfMonth());
        assertEquals(23,at.getDates().get(7).getDayOfMonth());
    }


    @Test
    public void testLastFortnights() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es", "ES"), false, dates);
        DateExpression at = dt.atFortnight(-1); // Primer

        // Lad dos últimas quincenas de frebero son
        // 2024 -> 14 días
        // 2025 -> 13 días
        assertEquals(27,at.getDates().size());
        for(int i=0;i<at.getDates().size();i++) {
            assertEquals(2024+i/14,at.getDates().get(i).getYear());
            assertEquals(2,at.getDates().get(i).getMonthValue());
        }

        // Esperamos que el día 0 sea el 16 en elos dos casos, y el día último el 29 o el 28 (depende del año)
        assertEquals(16,at.getDates().get(0).getDayOfMonth());
        assertEquals(29,at.getDates().get(13).getDayOfMonth());

        assertEquals(16,at.getDates().get(14).getDayOfMonth());
        assertEquals(28,at.getDates().get(26).getDayOfMonth());
    }


    @Test
    public void testFirstFortnights() {
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        LocalDate now = LocalDate.now(zoneId);
        LocalDate atMonth = now.withMonth(Month.FEBRUARY.getValue()).withYear(2024); // 2024 es bisiesto.

        final LocalDate selectedMonth = atMonth;

        // Generamos los días completos (2 años)
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(), selectedMonth.getYear() + 2).forEach(year -> {
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                    .mapToObj(i -> current.with(ChronoField.DAY_OF_MONTH, i)).collect(() -> dates, (a, b) -> a.add(b), (a, b) -> a.addAll(b));
        });

        // GEneramos la expersión.
        DateExpression dt = new DateExpression(new Locale("es", "ES"), false, dates);
        DateExpression at = dt.atFortnight(1); // Primer

        // Lad dos últimas quincenas de frebero son
        // 2024 -> 15 días
        // 2025 -> 15 días
        assertEquals(30,at.getDates().size());
        for(int i=0;i<at.getDates().size();i++) {
            assertEquals(2024+i/15,at.getDates().get(i).getYear());
            assertEquals(2,at.getDates().get(i).getMonthValue());
        }

        // Esperamos que el día 0 sea el 16 en elos dos casos, y el día último el 29 o el 28 (depende del año)
        assertEquals(1,at.getDates().get(0).getDayOfMonth());
        assertEquals(15,at.getDates().get(14).getDayOfMonth());

        assertEquals(1,at.getDates().get(15).getDayOfMonth());
        assertEquals(15,at.getDates().get(29).getDayOfMonth());
    }

    @Test
    public void testAndNoDuplicates() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,3,15),LocalDate.of(2024,4,15)));
        DateExpression d2 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,15),LocalDate.of(2024,6,15)));

        DateExpression d3 = d1.and(d2);
        assertEquals(4,d3.getDates().size());
        assertEquals(LocalDate.of(2024,2,15),d3.getDates().get(0));
        assertEquals(LocalDate.of(2024,3,15),d3.getDates().get(1));
        assertEquals(LocalDate.of(2024,4,15),d3.getDates().get(2));
        assertEquals(LocalDate.of(2024,6,15),d3.getDates().get(3));

    }

    @Test
    public void testAndDuplicates() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,3,15),LocalDate.of(2024,4,15)));
        DateExpression d2 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,3,15),LocalDate.of(2024,6,15)));

        DateExpression d3 = d1.and(d2);
        assertEquals(3,d3.getDates().size());
        assertEquals(LocalDate.of(2024,3,15),d3.getDates().get(0));
        assertEquals(LocalDate.of(2024,4,15),d3.getDates().get(1));
        assertEquals(LocalDate.of(2024,6,15),d3.getDates().get(2));

    }

    @Test
    public void testUntil() {
        // Tenemos en cuenta que
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,12,24),LocalDate.of(2025,12,24)));
        DateExpression d2 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,1,1),LocalDate.of(2025,1,1)));

        DateExpression to = d1.to(d2);
        assertFalse(to.getDates().isEmpty());

        assertEquals(LocalDate.of(2024,12,24),to.getDates().get(0));
        assertEquals(LocalDate.of(2024,12,25),to.getDates().get(1));
        assertEquals(LocalDate.of(2024,12,26),to.getDates().get(2));
        assertEquals(LocalDate.of(2024,12,27),to.getDates().get(3));
        assertEquals(LocalDate.of(2024,12,28),to.getDates().get(4));
        assertEquals(LocalDate.of(2024,12,29),to.getDates().get(5));
        assertEquals(LocalDate.of(2024,12,30),to.getDates().get(6));
        assertEquals(LocalDate.of(2024,12,31),to.getDates().get(7));
        assertEquals(LocalDate.of(2025, 1, 1),to.getDates().get(8));


    }

    @Test
    public void testBefore() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,12,24),LocalDate.of(2025,12,24)));

        DateExpression d2 = d1.before(3, ChronoUnit.DAYS);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,12,21),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,12,21),d2.getDates().get(1));
    }

    @Test
    public void testBeforeDay() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,6),LocalDate.of(2025,2,6)));

        DateExpression d2 = d1.before(DayOfWeek.MONDAY,1);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,5),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,2,3),d2.getDates().get(1));

        d2 = d1.before(DayOfWeek.MONDAY,2);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,1,29),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,1,27),d2.getDates().get(1));

        d2 = d1.before(DayOfWeek.TUESDAY,1);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,1,30),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,2,4),d2.getDates().get(1));
    }

    @Test
    public void testBeforeWeekend() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,6),LocalDate.of(2025,2,6)));
        DateExpression d2 = d1.beforeWeekend(1);

        assertEquals(4,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,3),d2.getDates().get(0));
        assertEquals(LocalDate.of(2024,2,4),d2.getDates().get(1));

        assertEquals(LocalDate.of(2025,2,1),d2.getDates().get(2));
        assertEquals(LocalDate.of(2025,2,2),d2.getDates().get(3));


    }

    @Test
    public void testAfter() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,12,24),LocalDate.of(2025,12,24)));

        DateExpression d2 = d1.after(6, ChronoUnit.DAYS);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,12,30),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,12,30),d2.getDates().get(1));
    }

    @Test
    public void testAfterDay() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,6),LocalDate.of(2025,2,6)));

        DateExpression d2 = d1.after(DayOfWeek.MONDAY,1);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,12),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,2,10),d2.getDates().get(1));

        d2 = d1.after(DayOfWeek.MONDAY,2);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,19),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,2,17),d2.getDates().get(1));

        d2 = d1.after(DayOfWeek.TUESDAY,1);
        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,13),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,2,11),d2.getDates().get(1));
    }

    @Test
    public void testAfterWeekend() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,6),LocalDate.of(2025,2,6)));
        DateExpression d2 = d1.afterWeekend(1);

        assertEquals(4,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,10),d2.getDates().get(0));
        assertEquals(LocalDate.of(2024,2,11),d2.getDates().get(1));

        assertEquals(LocalDate.of(2025,2,8),d2.getDates().get(2));
        assertEquals(LocalDate.of(2025,2,9),d2.getDates().get(3));


    }

    @Test
    public void testNear() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,7),LocalDate.of(2025,2,7)));
        DateExpression d2 = d1.near(DayOfWeek.MONDAY);

        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,5),d2.getDates().get(0));
        assertEquals(LocalDate.of(2025,2,10),d2.getDates().get(1));



    }

    @Test
    public void testNearWeekend() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,7),LocalDate.of(2025,2,7)));
        DateExpression d2 = d1.nearWeekend();

        assertEquals(4,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,3),d2.getDates().get(0));
        assertEquals(LocalDate.of(2024,2,4),d2.getDates().get(1));

        assertEquals(LocalDate.of(2025,2,8),d2.getDates().get(2));
        assertEquals(LocalDate.of(2025,2,9),d2.getDates().get(3));


    }

    @Test
    public void testNearWeekend2() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,11)));
        DateExpression d2 = d1.nearWeekend();

        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,10),d2.getDates().get(0));
        assertEquals(LocalDate.of(2024,2,11),d2.getDates().get(1));



    }

    @Test
    public void testNearWeekend3() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,10)));
        DateExpression d2 = d1.nearWeekend();

        assertEquals(2,d2.getDates().size());
        assertEquals(LocalDate.of(2024,2,10),d2.getDates().get(0));
        assertEquals(LocalDate.of(2024,2,11),d2.getDates().get(1));
    }

    @Test
    public void testCompare() {
        DateExpression d1 = new DateExpression(new Locale("es","ES"), false,Arrays.asList(LocalDate.of(2024,2,10)));

        DateExpression d2 = d1.nearWeekend();
        assertTrue(Objects.deepEquals(d2.getDates(),d1.compare(d2, DateExpression.Opcode.EQUALS,d1,d2).getDates()));

        d2 = d1.after(1,ChronoUnit.WEEKS);

        assertTrue(Objects.deepEquals(d2.getDates(),d1.compare(d2, DateExpression.Opcode.EQUALS,d1,d2).getDates()));
        d2 = d1.after(DayOfWeek.SUNDAY,3);
        assertTrue(Objects.deepEquals(d1.getDates(),d1.compare(d2, DateExpression.Opcode.LESSER,d1,d2).getDates()));
        assertTrue(Objects.deepEquals(d1.getDates(),d2.compare(d1, DateExpression.Opcode.GREATER,d1,d2).getDates()));
    }




}
