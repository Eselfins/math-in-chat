package io.github.eselfins.mathinchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MathExpressionTest {

    @Test
    void multiplicationWithXAlias() {
        assertEquals(25.0, MathExpression.tryEvaluate("5x5"));
        assertEquals(25.0, MathExpression.tryEvaluate("5X5"));
        assertEquals(25.0, MathExpression.tryEvaluate("5×5"));
    }

    @Test
    void basicOperators() {
        assertEquals(25.0, MathExpression.tryEvaluate("5*5"));
        assertEquals(14.0, MathExpression.tryEvaluate("2*(3+4)"));
        assertEquals(-2.0, MathExpression.tryEvaluate("-3+1"));
        assertEquals(2.5, MathExpression.tryEvaluate("10/4"));
        assertEquals(1024.0, MathExpression.tryEvaluate("2^10"));
        assertEquals(1.0, MathExpression.tryEvaluate("10%3"));
    }

    @Test
    void divisionAlias() {
        assertEquals(2.5, MathExpression.tryEvaluate("10÷4"));
    }

    @Test
    void precedenceAndAssociativity() {
        assertEquals(14.0, MathExpression.tryEvaluate("2+3*4"));
        assertEquals(-4.0, MathExpression.tryEvaluate("-2^2"));
        assertEquals(512.0, MathExpression.tryEvaluate("2^3^2"));
        assertEquals(0.25, MathExpression.tryEvaluate("2^-2"));
    }

    @Test
    void whitespaceAndDecimals() {
        assertEquals(7.0, MathExpression.tryEvaluate(" 3 + 4 "));
        assertEquals(1.5, MathExpression.tryEvaluate("0.5+1"));
        assertEquals(3.0, MathExpression.tryEvaluate("1.5*2"));
    }

    @Test
    void nonMathInputReturnsNull() {
        assertNull(MathExpression.tryEvaluate("hello"));
        assertNull(MathExpression.tryEvaluate("/time set day"));
        assertNull(MathExpression.tryEvaluate(""));
        assertNull(MathExpression.tryEvaluate("   "));
        assertNull(MathExpression.tryEvaluate(null));
    }

    @Test
    void incompleteExpressionReturnsNull() {
        assertNull(MathExpression.tryEvaluate("5+"));
        assertNull(MathExpression.tryEvaluate("(3+4"));
        assertNull(MathExpression.tryEvaluate("5 5"));
    }

    @Test
    void bareNumberReturnsNull() {
        assertNull(MathExpression.tryEvaluate("25"));
        assertNull(MathExpression.tryEvaluate("3.14"));
    }

    @Test
    void divisionByZeroReturnsNull() {
        assertNull(MathExpression.tryEvaluate("1/0"));
        assertNull(MathExpression.tryEvaluate("1%0"));
    }

    @Test
    void formatIntegralValues() {
        assertEquals("25", MathExpression.formatResult(25.0));
        assertEquals("-4", MathExpression.formatResult(-4.0));
        assertEquals("0", MathExpression.formatResult(0.0));
    }

    @Test
    void formatDecimalValues() {
        assertEquals("2.5", MathExpression.formatResult(2.5));
        assertEquals("3.3333", MathExpression.formatResult(10.0 / 3.0));
    }

    @Test
    void magnitudeSuffixes() {
        assertEquals(1500000.0, MathExpression.tryEvaluate("1m+500k"));
        assertEquals(6000.0, MathExpression.tryEvaluate("2k*3"));
        assertEquals(1500000.0, MathExpression.tryEvaluate("1M+500K"));
        assertEquals(250000000.0, MathExpression.tryEvaluate("1b/4"));
    }

    @Test
    void commaDecimalSeparator() {
        assertEquals(2.5, MathExpression.tryEvaluate("1,5+1"));
        assertEquals(3000.0, MathExpression.tryEvaluate("1,5k*2"));
    }

    @Test
    void misplacedSuffixReturnsNull() {
        assertNull(MathExpression.tryEvaluate("1 k"));
        assertNull(MathExpression.tryEvaluate("(1+2)k"));
    }

    @Test
    void bareSuffixedNumberReturnsNull() {
        assertNull(MathExpression.tryEvaluate("1m"));
        assertNull(MathExpression.tryEvaluate("500k"));
    }

    @Test
    void commaIsNeverAThousandsSeparator() {
        assertNull(MathExpression.tryEvaluate("1,000,000"));
    }

    @Test
    void ghostFormatCompactWithSuffixInput() {
        assertEquals("1.5m (1500000)", MathExpression.formatGhost("1m+500k", 1500000.0));
        assertEquals("1k (1000)", MathExpression.formatGhost("1k+0", 1000.0));
        assertEquals("250m (250000000)", MathExpression.formatGhost("1b/4", 250000000.0));
        assertEquals("-500k (-500000)", MathExpression.formatGhost("-1m+500k", -500000.0));
    }

    @Test
    void ghostFormatPlainWhenNotExactOrTooSmall() {
        assertEquals("333.3333", MathExpression.formatGhost("1k/3", 1000.0 / 3.0));
        assertEquals("500", MathExpression.formatGhost("1k/2", 500.0));
    }

    @Test
    void ghostFormatPlainWithoutSuffixInput() {
        assertEquals("1500", MathExpression.formatGhost("500*3", 1500.0));
        assertEquals("1024", MathExpression.formatGhost("2^10", 1024.0));
    }
}
