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
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implementación del calculo de la semana correspondiente a la octava de pascua, es un periodo
 * de ocho días, aunque por simplicidad consideramos sólo 7 días desde el domingo de pascua
 * hasta el sábado de la octava de pascua. Porque no consdierar 8 días, por evitar que los cálculos
 * de domingo de pascua sean confusos, para evitar que nos devuelva dos domingos en vez de uno...
 * Así si nos dicen "domingo de pascua" es sólo uno; porque en otro caso habría dos.
 *
 * @author David Rodríguez Alfayate 
 */
public class EasterResolver implements IEasterBaseResolver {
    @Override
    public LocalDate resolvePivotDate(int year, ZoneId zoneId) {
        return IEasterBaseResolver.computeEasterSunday(year,zoneId);
    }

    @Override
    public Stream<LocalDate> pivot(LocalDate pivot) {
        return IntStream.rangeClosed(0,6).mapToObj(pivot::plusDays);
    }


}
