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
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.IntStream;

public class MonthResolver implements IDateResolver {
    /**
     * El mes en el que estamos resolviendo las fechas
     */
    final Month month;

    public MonthResolver(Month month) {
        this.month = month;
    }


    @Override
    public DateExpression resolve(int startAtYear, int years, boolean fullWeeks, Locale locale, ZoneId zoneId) {
        LocalDate selectedMonth = LocalDate.now(zoneId).withYear(startAtYear).withMonth(month.getValue());

        // Generamos los días completos.
        ArrayList<LocalDate> dates = new ArrayList<>();
        IntStream.range(selectedMonth.getYear(),selectedMonth.getYear()+years).forEach(year->{
            LocalDate current = selectedMonth.withYear(year);
            IntStream.rangeClosed(1, current.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth())
                     .mapToObj(i->current.with(ChronoField.DAY_OF_MONTH,i)).collect(()->dates,(a,b)->a.add(b),(a,b)->a.addAll(b));
        });

        return new DateExpression(locale,fullWeeks,dates);
    }
}
