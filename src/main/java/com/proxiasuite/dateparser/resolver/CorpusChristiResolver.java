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
 * Implementación del calculo de fechas del corpus christi, es sólo un día
 * y es siempre 60 días despues del domingo de pascua.
 *
 * @author David Rodríguez Alfayate 
 */
public class CorpusChristiResolver implements IEasterBaseResolver {
    @Override
    public LocalDate resolvePivotDate(int year, ZoneId zoneId) {
        return IEasterBaseResolver.computeEasterSunday(year,zoneId).plusDays(60);
    }

    @Override
    public Stream<LocalDate> pivot(LocalDate pivot) {
        return Stream.of(pivot);
    }


}
