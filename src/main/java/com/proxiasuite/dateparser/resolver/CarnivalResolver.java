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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

/**
 * Implementación del calculo de fechas de carnaval, comprende del sábado
 * de carnaval hasta el martes de carnaval, y el computo del sábado se
 * hace en función del miércoles de ceniza que son 40 días hasta del domingo
 * de ramos, 46 días antes del domingo de Pascua. Esto hace que el sábado de carnaval sea 50 días antes.
 *
 * @author David Rodríguez Alfayate 
 */
public class CarnivalResolver implements IEasterBaseResolver {
    @Override
    public LocalDate resolvePivotDate(int year, ZoneId zoneId) {
        return IEasterBaseResolver.computeEasterSunday(year,zoneId).minusDays(50);
    }

    @Override
    public Stream<LocalDate> pivot(LocalDate pivot) {
        return Stream.of(pivot,
                         pivot.plusDays(1),
                         pivot.plusDays(2),
                         pivot.plusDays(3));
    }


}
