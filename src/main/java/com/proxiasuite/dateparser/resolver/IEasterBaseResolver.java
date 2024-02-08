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
package com.proxiasuite.dateparser.resolver;

import com.proxiasuite.dateparser.DateExpression;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Interfaz genérico para resolver todas las fechas que se basan en el cómputo de la
 * pascua cristiana.
 *
 * @author David Rodríguez Alfayate 
 */
public interface IEasterBaseResolver extends IDateResolver {
    @Override
    default DateExpression resolve(int startAtYear, int years, boolean fullWeeks, Locale locale, ZoneId zoneId) {
        // Siempre devolvemos de año no pasado...
        LocalDate now = LocalDate.now(zoneId).withYear(startAtYear);

        IntStream range = IntStream.range(now.getYear(),now.getYear()+years);

        // Vamos a calcular ahora para todos los años las fechas de los domingos de ramos.
        return new DateExpression(locale,fullWeeks,range.mapToObj(year->resolvePivotDate(year,zoneId))
                                                       .flatMap(this::pivot)
                                                       .collect(Collectors.toList()));

    }

    /**
     * Resuelve la fecha de referencia a partir de la que vamos a calcular
     * todas las fechas derivadas
     *
     * @param year      El año
     * @param zoneId    La zona horaria
     * @return  La fecha de pivot para todas las fechas que tenemos.
     */
    LocalDate resolvePivotDate(int year, ZoneId zoneId);

    /**
     * Devuelve un flujo de fechas a partir de la fecha pivote, el computo dependerá
     * de cada implementación exacta
     *
     * @param pivot La fecha a partir de la que pivotamos los cálculos
     * @return Un stream sobre el que se pivota a partir de esa fecha
     */
    Stream<LocalDate> pivot(LocalDate pivot);

    /**
     * Método que computa el domingo de resurrección (domingo de pascua), utilizando el
     * algoritmo de Butcher, el domingo de pascua es siempre un domingo de pascua,
     * no se tiene en cuenta la zona horaria de referencia. Así pues "empieza antes
     * o después en función de la zona"
     *
     * @param year      El año para el que lo queremos calcular.
     * @param zoneId    Zona horaria.
     *
     * @return La fecha del domingo de pascua
     */
    static LocalDate computeEasterSunday(int year, ZoneId zoneId) {
        int A = year % 19;
        int B = year / 100;
        int C = year % 100;
        int D = B / 4;
        int E = B % 4;
        int F = (B+8)/25;
        int G = (B - F +1)/3;
        int H = (19*A + B - D - G + 15)%30;
        int I = C / 4;
        int K = C % 4;
        int L = (32 + 2*E + 2*I - H - K)%7;
        int M = (A + 11*H + 22*L)/451;
        int N = H + L - 7*M + 114;

        int month = N / 31;
        int day   = 1 + (N % 31);
        LocalDate ld = LocalDate.now(zoneId);
        return ld.withYear(year).withMonth(month).withDayOfMonth(day);
    }
}
