package io.github.eselfins.mathinchat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathExpression {

    private MathExpression() {
    }

    public static Double tryEvaluate(String input) {
        if (input == null) {
            return null;
        }
        String expr = input.trim()
                .replace('x', '*')
                .replace('X', '*')
                .replace('×', '*')
                .replace('÷', '/')
                .replace(',', '.');
        if (expr.isEmpty() || !isMathAlphabet(expr) || !hasOperator(expr)) {
            return null;
        }
        try {
            Parser parser = new Parser(expr);
            double result = parser.parseExpression();
            parser.expectEnd();
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return null;
            }
            return result;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatResult(double value) {
        if (value == 0.0) {
            return "0";
        }
        if (value == Math.rint(value)) {
            return String.format(java.util.Locale.ROOT, "%.0f", value);
        }
        return BigDecimal.valueOf(value)
                .setScale(4, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static String formatGhost(String rawInput, double value) {
        String full = formatResult(value);
        if (rawInput == null || !hasMagnitudeSuffix(rawInput)) {
            return full;
        }
        String compact = tryCompact(value, 1_000_000_000.0, "b");
        if (compact == null) {
            compact = tryCompact(value, 1_000_000.0, "m");
        }
        if (compact == null) {
            compact = tryCompact(value, 1_000.0, "k");
        }
        return compact != null ? compact + " (" + full + ")" : full;
    }

    private static String tryCompact(double value, double factor, String suffix) {
        if (Math.abs(value) < factor) {
            return null;
        }
        double scaled = value / factor;
        try {
            BigDecimal.valueOf(scaled).setScale(4, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            return null;
        }
        return formatResult(scaled) + suffix;
    }

    private static boolean hasMagnitudeSuffix(String input) {
        for (int i = 1; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == 'k' || c == 'K' || c == 'm' || c == 'M' || c == 'b' || c == 'B')
                    && isDigitOrDot(input.charAt(i - 1))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDigitOrDot(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == ',';
    }

    private static boolean isMathAlphabet(String expr) {
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (!isMathChar(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isMathChar(char c) {
        return (c >= '0' && c <= '9')
                || c == '.' || c == ','
                || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^'
                || c == '(' || c == ')'
                || c == ' '
                || c == 'k' || c == 'K' || c == 'm' || c == 'M' || c == 'b' || c == 'B';
    }

    private static boolean hasOperator(String expr) {
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^') {
                return true;
            }
        }
        return false;
    }

    private static final class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ParseException(String message) {
            super(message);
        }
    }

    private static final class Parser {
        private final String input;
        private int pos;

        Parser(String input) {
            this.input = input;
        }

        double parseExpression() {
            double value = parseTerm();
            while (true) {
                if (peek('+')) {
                    value += parseTerm();
                } else if (peek('-')) {
                    value -= parseTerm();
                } else {
                    return value;
                }
            }
        }

        private double parseTerm() {
            double value = parseUnary();
            while (true) {
                if (peek('*')) {
                    value *= parseUnary();
                } else if (peek('/')) {
                    double divisor = parseUnary();
                    if (divisor == 0.0) {
                        throw new ParseException("division by zero");
                    }
                    value /= divisor;
                } else if (peek('%')) {
                    double divisor = parseUnary();
                    if (divisor == 0.0) {
                        throw new ParseException("modulo by zero");
                    }
                    value %= divisor;
                } else {
                    return value;
                }
            }
        }

        private double parseUnary() {
            if (peek('-')) {
                return -parseUnary();
            }
            if (peek('+')) {
                return parseUnary();
            }
            return parsePower();
        }

        private double parsePower() {
            double base = parsePrimary();
            if (peek('^')) {
                double exponent = parseUnary();
                return Math.pow(base, exponent);
            }
            return base;
        }

        private double parsePrimary() {
            skipSpaces();
            if (peek('(')) {
                double value = parseExpression();
                if (!peek(')')) {
                    throw new ParseException("missing closing parenthesis");
                }
                return value;
            }
            return parseNumber();
        }

        private double parseNumber() {
            skipSpaces();
            int start = pos;
            boolean sawDigit = false;
            boolean sawDot = false;
            while (pos < input.length()) {
                char c = input.charAt(pos);
                if (c >= '0' && c <= '9') {
                    sawDigit = true;
                    pos++;
                } else if (c == '.' && !sawDot) {
                    sawDot = true;
                    pos++;
                } else {
                    break;
                }
            }
            if (!sawDigit) {
                throw new ParseException("expected number at position " + start);
            }
            final double value;
            try {
                value = Double.parseDouble(input.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new ParseException("invalid number at position " + start);
            }
            if (pos < input.length()) {
                switch (input.charAt(pos)) {
                    case 'k', 'K' -> {
                        pos++;
                        return value * 1_000.0;
                    }
                    case 'm', 'M' -> {
                        pos++;
                        return value * 1_000_000.0;
                    }
                    case 'b', 'B' -> {
                        pos++;
                        return value * 1_000_000_000.0;
                    }
                    default -> {
                    }
                }
            }
            return value;
        }

        private boolean peek(char expected) {
            skipSpaces();
            if (pos < input.length() && input.charAt(pos) == expected) {
                pos++;
                return true;
            }
            return false;
        }

        private void expectEnd() {
            skipSpaces();
            if (pos != input.length()) {
                throw new ParseException("trailing characters at position " + pos);
            }
        }

        private void skipSpaces() {
            while (pos < input.length() && input.charAt(pos) == ' ') {
                pos++;
            }
        }
    }
}
