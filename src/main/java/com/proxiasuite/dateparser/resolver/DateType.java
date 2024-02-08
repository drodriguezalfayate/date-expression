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


import java.time.Month;

/**
 * Enumeración con los tipos de fechas que tenemos soportadas
 *
 * @author David Rodríguez Alfayate
 */
public enum DateType {
    /**
     * Semana Santa
     */
    HolyWeek,

    /**
     * Carnaval
     */
    Carnival,

    /**
     * Miércoles de ceniza
     */
    AshWednesday,

    /**
     * Corpus Christi
     */
    CorpusChristi,

    /**
     * Fiesta de la ascensión
     */
    Ascension,

    /**
     * Pentecostes
     */
    Pentecost,

    /**
     * Pascua
     */
    Easter,

    /**
     * Enero
     */
    January(Month.JANUARY),

    /**
     * Febrero
     */
    February(Month.FEBRUARY),

    /**
     * Marzo
     */
    March(Month.MARCH),

    /**
     * Abril
     */
    April(Month.APRIL),

    /**
     * Mayo
     */
    May(Month.MAY),

    /**
     * Junio
     */
    Jun(Month.JUNE),

    /**
     * Julio
     */
    July(Month.JULY),

    /**
     * Agosto
     */
    August(Month.AUGUST),

    /**
     * Septiembre
     */
    September(Month.SEPTEMBER),

    /**
     * Octubre
     */
    October(Month.OCTOBER),

    /**
     * Noviembre
     */
    November(Month.NOVEMBER),

    /**
     * Diciembre
     */
    December(Month.DECEMBER);

    /**
     * Mes concreto en el que nos encontramos
     */
    Month month = null;

    DateType(Month month) {
        this.month = month;
    }

    DateType() {

    }

    public Month asMonth() {
        return month;
    }

}
