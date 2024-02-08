package com.proxiasuite.dateparser;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpcodeTest {
    @Test
    public void testEquals() {
        assertTrue(DateExpression.Opcode.EQUALS.compare(LocalDate.of(2024,2,21),LocalDate.of(2024,2,21)));
        assertFalse(DateExpression.Opcode.EQUALS.compare(LocalDate.of(2024,2,21),LocalDate.of(2024,2,22)));
    }

    @Test
    public void testLessThan() {
        assertFalse(DateExpression.Opcode.LESSER.compare(LocalDate.of(2024,2,21),LocalDate.of(2024,2,21)));
        assertTrue(DateExpression.Opcode.LESSER.compare(LocalDate.of(2024,2,23),LocalDate.of(2024,2,24)));
        assertFalse(DateExpression.Opcode.LESSER.compare(LocalDate.of(2024,2,25),LocalDate.of(2024,2,24)));
    }


    @Test
    public void testGreaterThan() {
        assertFalse(DateExpression.Opcode.GREATER.compare(LocalDate.of(2024,2,21),LocalDate.of(2024,2,21)));
        assertFalse(DateExpression.Opcode.GREATER.compare(LocalDate.of(2024,2,23),LocalDate.of(2024,2,24)));
        assertTrue(DateExpression.Opcode.GREATER.compare(LocalDate.of(2024,2,25),LocalDate.of(2024,2,24)));
    }
}
