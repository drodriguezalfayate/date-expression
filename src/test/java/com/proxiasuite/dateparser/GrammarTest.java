package com.proxiasuite.dateparser;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class GrammarTest {
    @Test
    public void reallySimple() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"uno de enero");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,1,1),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,1,1),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,1,1),de.getDates().get(2));
    }

    @Test
    public void testWeekend() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                                                 ZoneId.of("Europe/Paris"),
                                        true,2024,3,
                                            "tercer fin de semana de agosto");
        assertNotNull(de);
        assertEquals(6,de.getDates().size());
        assertEquals(LocalDate.of(2024,8,17),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,8,18),de.getDates().get(1));

        assertEquals(LocalDate.of(2025,8,16),de.getDates().get(2));
        assertEquals(LocalDate.of(2025,8,17),de.getDates().get(3));

        assertEquals(LocalDate.of(2026,8,15),de.getDates().get(4));
        assertEquals(LocalDate.of(2026,8,16),de.getDates().get(5));

    }


    @Test
    public void testLunesAguas() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "lunes despues del lunes de pascua");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,4,8),de.getDates().get(0));

        assertEquals(LocalDate.of(2025,4,28),de.getDates().get(1));

        assertEquals(LocalDate.of(2026,4,13),de.getDates().get(2));


    }

    @Test
    public void testLunesAguas2() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "lunes despu\u00e9s del lunes de pascua");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,4,8),de.getDates().get(0));

        assertEquals(LocalDate.of(2025,4,28),de.getDates().get(1));

        assertEquals(LocalDate.of(2026,4,13),de.getDates().get(2));


    }

    @Test
    public void testTwoDays() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "3 y cuarto domingo de junio");
        assertNotNull(de);
        assertEquals(6,de.getDates().size());
        assertEquals(LocalDate.of(2024,6,16),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,6,23),de.getDates().get(1));

        assertEquals(LocalDate.of(2025,6,15),de.getDates().get(2));
        assertEquals(LocalDate.of(2025,6,22),de.getDates().get(3));

        assertEquals(LocalDate.of(2026,6,21),de.getDates().get(4));
        assertEquals(LocalDate.of(2026,6,28),de.getDates().get(5));
    }


    @Test
    public void testThreeDays() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "3 y cuarto domingo de junio y 1 de abril");
        assertNotNull(de);
        assertEquals(9,de.getDates().size());
        assertEquals(LocalDate.of(2024,4,1),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,6,16),de.getDates().get(1));
        assertEquals(LocalDate.of(2024,6,23),de.getDates().get(2));

        assertEquals(LocalDate.of(2025,4,1),de.getDates().get(3));
        assertEquals(LocalDate.of(2025,6,15),de.getDates().get(4));
        assertEquals(LocalDate.of(2025,6,22),de.getDates().get(5));

        assertEquals(LocalDate.of(2026,4,1),de.getDates().get(6));
        assertEquals(LocalDate.of(2026,6,21),de.getDates().get(7));
        assertEquals(LocalDate.of(2026,6,28),de.getDates().get(8));
    }

    @Test
    public void testDefinitioin() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "def navidad: 25 de diciembre\r\nnavidad");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,12,25),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,12,25),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,12,25),de.getDates().get(2));
    }

    @Test
    public void testDefinitionAndTo() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "def navidad: 31 de diciembre\r\n"+
                     "def annonuevo: 1 de enero\r\n"+
                     "de navidad a annonuevo");
        assertNotNull(de);
        assertEquals(4,de.getDates().size());
        assertEquals(LocalDate.of(2024,12,31),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,1,1),de.getDates().get(1));
        assertEquals(LocalDate.of(2025,12,31),de.getDates().get(2));
        assertEquals(LocalDate.of(2026,1,1),de.getDates().get(3));
    }

    @Test
    public void testBefore() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "def resurreccion: domingo antes del lunes de pascua\r\n"+
                     "si resurreccion es igual a segundo domingo de semana santa \r\n"+
                     "entonces 25 de diciembre\r\n"+
                     "si no 31 de enero\r\n");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,12,25),de.getDates().get(0));

        assertEquals(LocalDate.of(2025,12,25),de.getDates().get(1));

        assertEquals(LocalDate.of(2026,12,25),de.getDates().get(2));
    }

    @Test
    public void testBefore2() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "def resurreccion: lunes antes del lunes de pascua\r\n"+
                        "si resurreccion es igual a segundo domingo de semana santa \r\n"+
                        "entonces 25 de diciembre\r\n"+
                        "si no 31 de enero\r\n");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,1,31),de.getDates().get(0));

        assertEquals(LocalDate.of(2025,1,31),de.getDates().get(1));

        assertEquals(LocalDate.of(2026,1,31),de.getDates().get(2));
    }

    @Test
    public void testWeekenNear() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "fin de semana proximo al 14 de julio");


        assertNotNull(de);
        assertEquals(6,de.getDates().size());

        assertEquals(LocalDate.of(2024,7,13),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,7,14),de.getDates().get(1));

        assertEquals(LocalDate.of(2025,7,12),de.getDates().get(2));
        assertEquals(LocalDate.of(2025,7,13),de.getDates().get(3));

        assertEquals(LocalDate.of(2026,7,11),de.getDates().get(4));
        assertEquals(LocalDate.of(2026,7,12),de.getDates().get(5));

    }

    @Test
    public void testDayNear() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "jueves proximo al 14 de julio");


        assertNotNull(de);
        assertEquals(3,de.getDates().size());

        assertEquals(LocalDate.of(2024,7,11),de.getDates().get(0));

        assertEquals(LocalDate.of(2025,7,17),de.getDates().get(1));

        assertEquals(LocalDate.of(2026,7,16),de.getDates().get(2));
    }

    @Test
    public void testWeekAfter() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "2 semanas despues del miercoles de ceniza");


        assertNotNull(de);
        assertEquals(3,de.getDates().size());

        assertEquals(LocalDate.of(2024,2,28),de.getDates().get(0));

        assertEquals(LocalDate.of(2025,3,19),de.getDates().get(1));

        assertEquals(LocalDate.of(2026,3,4),de.getDates().get(2));
    }

    @Test
    public void testPentecost() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "1 mes despues del domingo de pentecostes");


        assertNotNull(de);
        assertEquals(3,de.getDates().size());

        assertEquals(LocalDate.of(2024,6,19),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,7,8),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,6,24),de.getDates().get(2));
    }

    @Test
    public void testCarnivalCompare() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"),
                ZoneId.of("Europe/Paris"),
                true,2024,3,
                "si sabado de carnaval es igual a segundo sabado de febrero\r\n"+
                     "entonces primer fin de semana de marzo \r\n"+
                     "si no ultimo fin de semana de abril\r\n");
        assertNotNull(de);
        assertEquals(6,de.getDates().size());
        assertEquals(LocalDate.of(2024,3,2),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,3,3),de.getDates().get(1));

        assertEquals(LocalDate.of(2025,4,26),de.getDates().get(2));
        assertEquals(LocalDate.of(2025,4,27),de.getDates().get(3));

    }

    @Test
    public void testNoYear() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"uno de enero");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.now().getYear(),de.getDates().get(0).getYear());
        assertEquals(LocalDate.now().getYear()+1,de.getDates().get(1).getYear());
        assertEquals(LocalDate.now().getYear()+2,de.getDates().get(2).getYear());
    }

    @Test
    public void testMonths() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"enero");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.JANUARY)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"febrero");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.FEBRUARY)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"marzo");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.MARCH)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"abril");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.APRIL)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"mayo");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.MAY)));


        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"junio");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.JUNE)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"julio");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.JULY)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"agosto");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.AUGUST)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"septiembre");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.SEPTEMBER)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"octubre");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.OCTOBER)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"noviembre");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.NOVEMBER)));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,3,"diciembre");
        assertNotNull(de);
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.DECEMBER)));




    }

    @Test
    public void testPreLast() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"penultimo miercoles de marzo");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,3,20),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,3,19),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,3,18),de.getDates().get(2));

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"penultimo martes de marzo");
        assertNotNull(de);
        assertEquals(LocalDate.of(2026,3,24),de.getDates().get(2));

    }

    @Test
    public void testCorpus() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"viernes despues del corpus");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,5,31),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,6,20),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,6,5),de.getDates().get(2));
    }

    @Test
    public void testWeek() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"ultima semana de febrero");
        assertNotNull(de);
        assertEquals(21,de.getDates().size());
        assertTrue(de.getDates().stream().allMatch(l->l.getMonth().equals(Month.FEBRUARY)));
        assertEquals(LocalDate.of(2024,2,19),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,2,25),de.getDates().get(6));

        assertEquals(LocalDate.of(2025,2,17),de.getDates().get(7));
        assertEquals(LocalDate.of(2025,2,23),de.getDates().get(13));

        assertEquals(LocalDate.of(2026,2,16),de.getDates().get(14));
        assertEquals(LocalDate.of(2026,2,22),de.getDates().get(20));

    }

    @Test
    public void testWeekendAfterAscension() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"fin de semana despues de la ascension");
        assertNotNull(de);
        assertEquals(6,de.getDates().size());

        assertEquals(LocalDate.of(2024,5,11),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,5,12),de.getDates().get(1));

        assertEquals(LocalDate.of(2025,5,31),de.getDates().get(2));
        assertEquals(LocalDate.of(2025,6,1),de.getDates().get(3));

        assertEquals(LocalDate.of(2026,5,16),de.getDates().get(4));
        assertEquals(LocalDate.of(2026,5,17),de.getDates().get(5));
    }


    @Test
    public void testFortnight() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"primera quincena de febrero");
        assertNotNull(de);
        assertFalse(de.isApproximate());
        assertEquals(45,de.getDates().size());

    }

    @Test
    public void testApproximateFortnight() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"aprox primera quincena de febrero");
        assertNotNull(de);
        assertTrue(de.isApproximate());
        assertEquals(45,de.getDates().size());

    }

    @Test
    public void testBeforeAt() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"tres dias antes del primer sabado de febrero");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,1,31),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,1,29),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,2,4),de.getDates().get(2));

    }

    @Test
    public void testBeforeWeekend() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"segundo fin de semana antes del primer sabado de febrero");
        assertNotNull(de);
        assertEquals(6,de.getDates().size());
        assertEquals(LocalDate.of(2024,1,20),de.getDates().get(0));
        assertEquals(LocalDate.of(2024,1,21),de.getDates().get(1));

        assertEquals(LocalDate.of(2025,1,18),de.getDates().get(2));
        assertEquals(LocalDate.of(2025,1,19),de.getDates().get(3));

    }

    @Test
    public void testRecursiveAt() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"primer lunes del primer y segundo lunes de julio");
        assertNotNull(de);
        assertEquals(3,de.getDates().size());
        assertEquals(LocalDate.of(2024,7,1),de.getDates().get(0));
        assertEquals(LocalDate.of(2025,7,7),de.getDates().get(1));
        assertEquals(LocalDate.of(2026,7,6),de.getDates().get(2));

    }

    @Test
    public void testInvalidGrammar() {
        DateExpression de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"navidad");
        assertNull(de);

        de = DateExpression.parse(new Locale("es","ES"), ZoneId.of("Europe/Paris"),true,2024,3,"del 3 al 4 de julio");
        assertNull(de);

    }
    }
}
