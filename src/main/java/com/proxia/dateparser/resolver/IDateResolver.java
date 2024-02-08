/**
 * MIT License
 *
 * Copyright (c) 2024  David Rodríguez Alfayate - Divisa iT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.proxia.dateparser.resolver;

import com.proxia.dateparser.DateExpression;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Interfaz que permite obtener un conjunto de fechas a partir del tipo de fecha {@link DateType} que tenemos
 * identificada en el sistema
 *
 * @author David Rodríguez Alfayate (drodriguez@divisait.com)
 */
public interface IDateResolver {
    /**
     * Método que permite resolver las fechas que tenemos vinculadas a este resolutor de fechas, siempre devuelve
     * fechas en el futuro a partir del momento actual.
     *
     * @param years     El número de años para el que vamos a resolver fechas
     * @param fullWeeks Si computamos la semana como semana completa.
     * @param locale    El idioma del usuario
     * @param zoneId    La zona horaria
     *
     * @return  Un conjunto de fechas que se corresponden con la resolución de la expresión actual;
     */
    default DateExpression resolve(int years, boolean fullWeeks, Locale locale, ZoneId zoneId) {
        LocalDate ld = LocalDate.now(zoneId);
        return resolve(ld.getYear(),years,fullWeeks,locale,zoneId);
    }

    /**
     * Método que permite resolver las fechas que tenemos vinculadas a este resolutor de fechas, siempre devuelve
     * fechas en el futuro a partir del momento actual.
     *
     * @param years     El número de años para el que vamos a resolver fechas
     * @param fullWeeks Si computamos la semana como semana completa.
     * @param locale    El idioma del usuario
     * @param zoneId    La zona horaria
     *
     * @return  Un conjunto de fechas que se corresponden con la resolución de la expresión actual;
     */
    DateExpression resolve(int startAtYear, int years, boolean fullWeeks, Locale locale, ZoneId zoneId);

    /**
     * Método estático que permite obtener la implementación del sistema de resolución de fechas para
     * un determinado tipo de fecha concreto
     */
    static IDateResolver getInstance(DateType dateType) {
        switch(dateType) {
            case HolyWeek:
                return new HolyWeekResolver();
            case Carnival:
                return new CarnivalResolver();
            case AshWednesday:
                return new AshWednesdayResolver();
            case CorpusChristi:
                return new CorpusChristiResolver();
            case Ascension:
                return new AscensionResolver();
            case Pentecost:
                return new PentecostResolver();
            case Easter:
                return new EasterResolver();
            default:
                return new MonthResolver(dateType.asMonth());
        }
    }
}
