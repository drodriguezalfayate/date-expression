package com.proxiasuite.dateparser;

import com.proxiasuite.dateparser.DateExpressionError.Expected;
import com.proxiasuite.dateparser.DateExpressionError.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * La validación estricta de las expresiones: cada error con su posición y lo necesario para explicarlo
 */
public class DateExpressionValidationTest {

    private static DateExpressionValidation validate(String expr) {
        return DateExpression.validate(new Locale("es", "ES"), ZoneId.of("Europe/Madrid"), true, 2024, 2, expr);
    }

    private static DateExpressionError onlyError(String expr) {
        DateExpressionValidation v = validate(expr);
        assertFalse(v.isValid(), expr);
        assertNull(v.expression());
        return v.errors().get(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"uno de enero", "25 de diciembre", "lunes de julio", "1 de agosto al 15 de agosto",
            "segundo domingo de enero", "tres dias antes del primer sabado de febrero", "aprox 15 de mayo",
            "aproximadamente 15 de mayo", "dieciséis de julio", "veintidós de julio", "veintitrés de julio",
            "veintiséis de julio", "dieciseis de julio"})
    public void validExpressionsAreResolved(String expr) {
        DateExpressionValidation v = validate(expr);
        assertTrue(v.isValid(), expr + ": " + v.errors());
        assertFalse(v.expression().getDates().isEmpty());
    }

    @Test
    public void aproximadamenteMarksTheExpressionAsApproximated() {
        assertTrue(validate("aproximadamente 15 de mayo").expression().isApproximate());
    }

    @Test
    public void aMisspelledWordIsReportedWithASuggestion() {
        DateExpressionError e = onlyError("uno de enro");
        assertEquals(Type.UNKNOWN_WORD, e.type());
        assertEquals("enro", e.text());
        assertEquals(7, e.position());
        assertEquals("enero", e.suggestion());
    }

    @Test
    public void aMisspelledWordThatTheGrammarSkipsIsAlsoReported() {
        // parse se queda con «1 de enero» y no avisa
        DateExpressionError e = onlyError("1 de enero y 2 de febreo");
        assertEquals(Type.UNKNOWN_WORD, e.type());
        assertEquals("febrero", e.suggestion());
    }

    @Test
    public void trailingTextIsReported() {
        DateExpressionError e = onlyError("enero 15");
        assertEquals(Type.TRAILING_TEXT, e.type());
        assertEquals("15", e.text());
        assertEquals(6, e.position());
    }

    @Test
    public void anIncompleteExpressionSaysWhatWasExpected() {
        DateExpressionError e = onlyError("1 de agosto hasta");
        assertEquals(Type.INCOMPLETE, e.type());
        assertTrue(e.expected().contains(Expected.MONTH), e.expected().toString());
    }

    @Test
    public void anInvalidCharacterIsReported() {
        DateExpressionError e = onlyError("1 de enero #");
        assertEquals(Type.INVALID_CHARACTER, e.type());
        assertEquals("#", e.text());
    }

    @Test
    public void definedVariablesAreNotUnknownWords() {
        DateExpressionValidation v = validate("def navidad: 25 de diciembre\nnavidad");
        assertTrue(v.isValid(), v.errors().toString());
        assertEquals(LocalDate.of(2024, 12, 25), v.expression().getDates().get(0));
    }

    @Test
    public void anUndefinedVariableIsAnUnknownWord() {
        assertEquals(Type.UNKNOWN_WORD, onlyError("navidad").type());
    }

    @Test
    public void everyErrorHasASpanishMessage() {
        assertEquals("Palabra no reconocida «enro» (posición 8). ¿Quería decir «enero»?",
                     onlyError("uno de enro").getMessage());
    }
}
