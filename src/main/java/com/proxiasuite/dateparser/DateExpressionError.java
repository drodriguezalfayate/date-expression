/**
 * MIT License
 *
 * Copyright (c) 2026  David Rodríguez Alfayate - Divisa iT
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
package com.proxiasuite.dateparser;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Un error de una expresión de fecha, con lo necesario para explicárselo a quien la ha escrito: dónde está, qué texto
 * es, qué se esperaba en ese punto y, si es una palabra mal escrita, la palabra conocida más parecida.
 *
 * @param type       El tipo de error
 * @param position   La posición (desde 0) en la expresión en la que empieza el error
 * @param text       El texto que provoca el error (vacío si falta algo al final)
 * @param suggestion La palabra conocida más parecida a {@code text}, o null
 * @param expected   Lo que podía ir en ese punto
 */
public record DateExpressionError(Type type, int position, String text, String suggestion, Set<Expected> expected) {

    /**
     * El tipo de error
     */
    public enum Type {
        /** Una palabra que la gramática no conoce (y que no es una variable definida con {@code def}) */
        UNKNOWN_WORD,
        /** Un carácter que no puede formar parte de ninguna palabra */
        INVALID_CHARACTER,
        /** Una palabra conocida en un lugar en el que no puede ir */
        UNEXPECTED,
        /** La expresión se termina cuando aún falta algo */
        INCOMPLETE,
        /** Texto al final que no forma parte de la expresión */
        TRAILING_TEXT,
        /** La expresión es correcta pero no da ninguna fecha */
        NO_DATES
    }

    /**
     * Lo que puede ir en un punto de la expresión, agrupado como lo entiende quien la escribe
     */
    public enum Expected {
        /** Un número de día o un ordinal: 1, uno, primer, último... */
        NUMBER("un número o un ordinal (1, uno, primer, último)"),
        /** Un mes */
        MONTH("un mes (enero, febrero...)"),
        /** Un día de la semana, el fin de semana, la semana o la quincena */
        WEEKDAY("un día de la semana (lunes, fin de semana, semana...)"),
        /** Una fecha señalada */
        FIXED_DATE("una fecha señalada (semana santa, carnaval, pascua...)"),
        /** Antes, después o próximo a otra fecha */
        RELATION("antes, después o próximo"),
        /** Hasta otra fecha */
        RANGE("al o hasta"),
        /** Otra fecha más */
        CONJUNCTION("«y» o una coma"),
        /** Una comparación de una condición */
        COMPARISON("«es igual a», «es mayor que» o «es menor que»"),
        /** El resto de una condición */
        CONDITION("«si», «entonces» o «si no»"),
        /** Una variable */
        VARIABLE("una variable definida con def"),
        /** Los dos puntos de una definición */
        ASSIGN("«:»"),
        /** Semanas o meses, para contar */
        PERIOD("semanas o meses");

        private final String description;

        Expected(String description) {
            this.description = description;
        }

        /**
         * La descripción en castellano
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * El error explicado en castellano, para quien no los traduce
     */
    public String getMessage() {
        String where = "posición " + (position + 1);
        String expecting = expected == null || expected.isEmpty() ? ""
                : " Se esperaba " + expected.stream().map(Expected::getDescription).collect(Collectors.joining(", ")) + ".";
        return switch (type) {
            case UNKNOWN_WORD -> "Palabra no reconocida «" + text + "» (" + where + ")."
                                 + (suggestion != null ? " ¿Quería decir «" + suggestion + "»?" : "");
            case INVALID_CHARACTER -> "Carácter no válido «" + text + "» (" + where + ").";
            case UNEXPECTED -> "«" + text + "» no puede ir aquí (" + where + ")." + expecting;
            case INCOMPLETE -> "La expresión está incompleta." + expecting;
            case TRAILING_TEXT -> "Texto no reconocido a partir de «" + text + "» (" + where + ").";
            case NO_DATES -> "La expresión no da ninguna fecha.";
        };
    }
}
