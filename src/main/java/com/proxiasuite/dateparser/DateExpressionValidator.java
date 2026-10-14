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

import com.proxiasuite.dateparser.DateExpressionError.Expected;
import com.proxiasuite.dateparser.DateExpressionError.Type;
import com.proxiasuite.dateparser.grammar.DateExpressionGrammarLexer;
import com.proxiasuite.dateparser.grammar.DateExpressionGrammarParser;
import com.proxiasuite.dateparser.parser.DateExpressionVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.text.Normalizer;
import java.time.ZoneId;
import java.util.*;

import static com.proxiasuite.dateparser.grammar.DateExpressionGrammarParser.*;

/**
 * Valida una expresión de fecha de forma estricta: además de los errores de sintaxis (que {@link DateExpression#parse}
 * recupera sin avisar), las palabras que no conoce la gramática, el texto que sobra al final y la expresión que no da
 * ninguna fecha.
 */
final class DateExpressionValidator {

    /** Las palabras de la gramática, para sugerir la más parecida a una mal escrita */
    private static final List<String> KEYWORDS = List.of(
            "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre",
            "noviembre", "diciembre",
            "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo", "fin de semana", "semana",
            "semanas", "quincena", "mes", "meses",
            "uno", "una", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "once", "doce",
            "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte", "veintiuno",
            "veintidós", "veintitrés", "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho",
            "veintinueve", "treinta",
            "primer", "primero", "primera", "segundo", "segunda", "tercer", "tercero", "tercera", "cuarto", "cuarta",
            "quinto", "quinta", "sexto", "sexta", "séptimo", "séptima", "octavo", "octava", "noveno", "novena",
            "décimo", "décima", "último", "última", "penúltimo", "penúltima", "víspera", "ayer",
            "antes", "anterior", "después", "siguiente", "posterior", "próximo", "próxima", "cerca", "al", "hasta",
            "semana santa", "carnaval", "miércoles de ceniza", "corpus", "corpus christi", "ascensión",
            "pentecostés", "pascua", "aprox", "aproximadamente", "si", "entonces", "def", "de", "del", "y");

    private DateExpressionValidator() {
    }

    static DateExpressionValidation validate(Locale locale, ZoneId zoneId, boolean fullWeeks, int firstYear, int years,
                                             String expr) {
        String text = expr == null ? "" : expr;
        List<DateExpressionError> errors = new ArrayList<>();

        DateExpressionGrammarLexer lexer = new DateExpressionGrammarLexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                    int charPositionInLine, String msg, RecognitionException e) {
                int position = offset(text, line, charPositionInLine);
                String character = position < text.length() ? text.substring(position, position + 1) : "";
                errors.add(new DateExpressionError(Type.INVALID_CHARACTER, position, character, null, Set.of()));
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DateExpressionGrammarParser parser = new DateExpressionGrammarParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                    int charPositionInLine, String msg, RecognitionException e) {
                Token token = (Token) offendingSymbol;
                Set<Expected> expected = expected(((Parser) recognizer).getExpectedTokens());
                if (token == null || token.getType() == Token.EOF) {
                    errors.add(new DateExpressionError(Type.INCOMPLETE, text.length(), "", null, expected));
                } else if (token.getType() == ID) {
                    errors.add(unknownWord(token));
                } else {
                    errors.add(new DateExpressionError(Type.UNEXPECTED, token.getStartIndex(), token.getText(), null,
                                                       expected));
                }
            }
        });
        DateExpressionVisitor visitor = new DateExpressionVisitor(zoneId, locale, firstYear, years, fullWeeks);
        parser.addParseListener(visitor);
        parser.prog();

        // La gramática no exige llegar al final: lo que sobra se ignora
        Token next = tokens.LT(1);
        if (next != null && next.getType() != Token.EOF) {
            errors.add(next.getType() == ID ? unknownWord(next)
                       : new DateExpressionError(Type.TRAILING_TEXT, next.getStartIndex(),
                                                 text.substring(next.getStartIndex()).trim(), null, Set.of()));
        }

        // Las palabras que no conoce la gramática se toman como variables: si no están definidas, son errores
        Set<String> defined = new HashSet<>();
        List<Token> all = tokens.getTokens();
        for (int i = 0; i + 1 < all.size(); i++) {
            if (all.get(i).getType() == Def && all.get(i + 1).getType() == ID) {
                defined.add(all.get(i + 1).getText().toLowerCase(Locale.ROOT));
            }
        }
        for (int i = 0; i < all.size(); i++) {
            Token token = all.get(i);
            boolean definition = i > 0 && all.get(i - 1).getType() == Def;
            if (token.getType() == ID && !definition && !defined.contains(token.getText().toLowerCase(Locale.ROOT))) {
                errors.add(unknownWord(token));
            }
        }

        List<DateExpressionError> result = deduplicate(errors);
        DateExpression expression = result.isEmpty() ? visitor.getDateExpression() : null;
        if (result.isEmpty() && (expression == null || expression.getDates().isEmpty())) {
            result = List.of(new DateExpressionError(Type.NO_DATES, 0, text, null, Set.of()));
            expression = null;
        }
        return new DateExpressionValidation(result, expression);
    }

    private static DateExpressionError unknownWord(Token token) {
        return new DateExpressionError(Type.UNKNOWN_WORD, token.getStartIndex(), token.getText(),
                                       suggestion(token.getText()), Set.of());
    }

    /** Un error por posición, en el orden de la expresión */
    private static List<DateExpressionError> deduplicate(List<DateExpressionError> errors) {
        Map<Integer, DateExpressionError> byPosition = new TreeMap<>();
        for (DateExpressionError error : errors) {
            byPosition.putIfAbsent(error.position(), error);
        }
        return List.copyOf(byPosition.values());
    }

    /** La posición en el texto de una línea y columna del lexer */
    private static int offset(String text, int line, int column) {
        int position = 0;
        for (int l = 1; l < line && position < text.length(); l++) {
            int nl = text.indexOf('\n', position);
            if (nl < 0) break;
            position = nl + 1;
        }
        return Math.min(position + column, text.length());
    }

    /** Lo que se esperaba, por grupos */
    private static Set<Expected> expected(IntervalSet tokens) {
        Set<Expected> result = EnumSet.noneOf(Expected.class);
        if (tokens == null) return result;
        for (int type : tokens.toList()) {
            Expected e = category(type);
            if (e != null) result.add(e);
        }
        return result;
    }

    private static Expected category(int type) {
        if (type == NUMBER || type == Last || type == Prelast || type == Yesterday || (type >= One && type <= ThirtyOne)) {
            return Expected.NUMBER;
        }
        if (type >= January && type <= December) return Expected.MONTH;
        if ((type >= Monday && type <= Sunday) || type == WeekEnd || type == Week || type == Fortnight) {
            return Expected.WEEKDAY;
        }
        if (type >= HolyWeek && type <= Easter && type != WeekEnd) return Expected.FIXED_DATE;
        if (type == Before || type == After || type == Near) return Expected.RELATION;
        if (type == To) return Expected.RANGE;
        if (type == And) return Expected.CONJUNCTION;
        if (type == Equals || type == Greater || type == Lesser) return Expected.COMPARISON;
        if (type == If || type == Then || type == Else) return Expected.CONDITION;
        if (type == ID) return Expected.VARIABLE;
        if (type == Assign) return Expected.ASSIGN;
        if (type == Month) return Expected.PERIOD;
        return null;
    }

    /** La palabra de la gramática más parecida, si se parece lo bastante */
    static String suggestion(String word) {
        String normalized = normalize(word);
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String keyword : KEYWORDS) {
            int distance = distance(normalized, normalize(keyword));
            if (distance < bestDistance) {
                bestDistance = distance;
                best = keyword;
            }
        }
        int allowed = Math.max(1, normalized.length() / 3);
        return bestDistance <= allowed ? best : null;
    }

    private static String normalize(String word) {
        return Normalizer.normalize(word.toLowerCase(Locale.ROOT), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    /** Distancia de edición (Levenshtein) */
    private static int distance(String a, String b) {
        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) previous[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + cost);
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[b.length()];
    }
}
