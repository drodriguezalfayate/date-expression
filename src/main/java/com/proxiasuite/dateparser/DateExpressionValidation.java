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

import java.util.List;

/**
 * El resultado de validar una expresión de fecha ({@link DateExpression#validate}): los errores, en el orden en que
 * aparecen en la expresión, y, si no hay ninguno, la expresión resuelta.
 *
 * @param errors     Los errores; vacío si la expresión es válida
 * @param expression La expresión resuelta, o null si hay errores
 */
public record DateExpressionValidation(List<DateExpressionError> errors, DateExpression expression) {

    /**
     * Si la expresión es válida: no hay errores y da alguna fecha
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
}
