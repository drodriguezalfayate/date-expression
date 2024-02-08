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
package com.proxiasuite.dateparser.parser;

import com.proxiasuite.dateparser.grammar.DateExpressionGrammarParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.DayOfWeek;

/**
 * Enumeración con los tipos de días que tenemos en el sistema
 *
 * @author David Rodríguez Alfayate 
 */
public enum DayQualifierType {
    Monday(DayOfWeek.MONDAY),
    Tuesday(DayOfWeek.TUESDAY),
    Wednesday(DayOfWeek.WEDNESDAY),
    Thursday(DayOfWeek.THURSDAY),
    Friday(DayOfWeek.FRIDAY),
    Saturday(DayOfWeek.SATURDAY),
    Sunday(DayOfWeek.SUNDAY),
    Weekend,
    Week,
    Fortnight;

    DayOfWeek dayOfWeek;

    DayQualifierType() {

    }

    DayQualifierType(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    static DayQualifierType decode(TerminalNode tn) {
        switch(tn.getSymbol().getType()) {
            case DateExpressionGrammarParser.Monday:
                return Monday;
            case DateExpressionGrammarParser.Tuesday:
                return Tuesday;
            case DateExpressionGrammarParser.Wednesday:
                return Wednesday;
            case DateExpressionGrammarParser.Thursday:
                return Thursday;
            case DateExpressionGrammarParser.Friday:
                return Friday;
            case DateExpressionGrammarParser.Saturday:
                return Saturday;
            case DateExpressionGrammarParser.Sunday:
                return Sunday;
            case DateExpressionGrammarParser.WeekEnd:
                return Weekend;
            case DateExpressionGrammarParser.Week:
                return Week;
            default:
                return Fortnight;
        }
    }
}
