package com.codeguard.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tokenizer {
    private int index;
    private int line;
    public List<Token> tokenize(String sourceCode) {

        index = 0;
        line = 1;
        List<Token> tokens = new ArrayList<>();
        while (index < sourceCode.length()) {
            char current = sourceCode.charAt(index);
            if (Character.isWhitespace(current)) {
                handleWhitespace(sourceCode);
                continue;
            }
            if (current == '"') {
                tokens.add(readString(sourceCode));
                continue;
            }
            if (current == '\'') {
                tokens.add(readCharacter(sourceCode));
                continue;
            }
            if (isCommentStart(sourceCode)) {
                skipComment(sourceCode);
                continue;
            }
            if (Character.isJavaIdentifierStart(current)) {
                tokens.add(readIdentifier(sourceCode));
                continue;
            }
            if (Character.isDigit(current)) {
                tokens.add(readNumber(sourceCode));
                continue;
            }
            if (SYMBOLS.contains(current)) {
                tokens.add(readSymbol(current));
                continue;
            }
            String operator = readOperator(sourceCode);
            if (operator != null) {
                tokens.add(new Token(
                        TokenType.OPERATOR,
                        operator,
                        line
                ));
                index += operator.length();
                continue;
            }
            index++;
        }
        return tokens;
    }
    private String readOperator(String sourceCode) {

        if (index + 1 < sourceCode.length()) {

            String twoCharacters =
                    sourceCode.substring(index, index + 2);

            if (OPERATORS.contains(twoCharacters)) {
                return twoCharacters;
            }
        }

        String oneCharacter =
                String.valueOf(sourceCode.charAt(index));

        if (OPERATORS.contains(oneCharacter)) {
            return oneCharacter;
        }

        return null;
    }
    private Token readIdentifier(String sourceCode) {

        int start = index;

        index++;

        while (index < sourceCode.length()
                && Character.isJavaIdentifierPart(
                sourceCode.charAt(index))) {

            index++;
        }

        String value = sourceCode.substring(start, index);

        TokenType type ;
        if (KEYWORDS.contains(value)) {
            type = TokenType.KEYWORD;
        } else if (LITERALS.contains(value)) {
            type = TokenType.LITERAL;
        } else {
            type = TokenType.IDENTIFIER;
        }

        return new Token(type, value, line);
    }
    private void handleWhitespace(String sourceCode) {

        if (sourceCode.charAt(index) == '\n') {
            line++;
        }

        index++;
    }
    private Token readNumber(String sourceCode) {

        int start = index;

        while (index < sourceCode.length()
                && Character.isDigit(sourceCode.charAt(index))) {

            index++;
        }
        if (index < sourceCode.length()
                && sourceCode.charAt(index) == '.') {

            index++;

            while (index < sourceCode.length()
                    && Character.isDigit(sourceCode.charAt(index))) {

                index++;
            }
        }

        String value = sourceCode.substring(start, index);

        return new Token(
                TokenType.NUMBER,
                value,
                line
        );
    }
    private Token readString(String sourceCode) {

        int start = index;

        index++; // skip opening "

        while (index < sourceCode.length()) {

            char current = sourceCode.charAt(index);

            // Handle escaped characters such as \", \\ and \n
            if (current == '\\') {
                index += 2;
                continue;
            }

            // Closing quote
            if (current == '"') {
                index++;
                break;
            }

            // Normal character
            index++;
        }

        if (index > sourceCode.length()
                || sourceCode.charAt(index - 1) != '"') {

            throw new IllegalArgumentException(
                    "Unterminated string at line " + line
            );
        }

        String value = sourceCode.substring(start, index);

        return new Token(
                TokenType.STRING,
                value,
                line
        );
    }
    private Token readCharacter(String sourceCode) {

        int start = index;

        index++;

        while (index < sourceCode.length()
                && sourceCode.charAt(index) != '\'') {

            index++;
        }

        if (index >= sourceCode.length()) {
            throw new IllegalArgumentException(
                    "Unterminated character literal at line " + line
            );
        }

        index++;

        String value = sourceCode.substring(start, index);

        return new Token(
                TokenType.CHARACTER,
                value,
                line
        );
    }
    private Token readSymbol(char current) {

        index++;

        return new Token(
                TokenType.SYMBOL,
                String.valueOf(current),
                line
        );
    }
    private void skipSingleLineComment(String sourceCode) {

        index += 2;

        while (index < sourceCode.length()
                && sourceCode.charAt(index) != '\n') {

            index++;
        }
    }
    private void skipMultiLineComment(String sourceCode) {

        index += 2;

        while (index + 1 < sourceCode.length()
                && !(sourceCode.charAt(index) == '*'
                && sourceCode.charAt(index + 1) == '/')) {

            if (sourceCode.charAt(index) == '\n') {
                line++;
            }

            index++;
        }

        if (index + 1 >= sourceCode.length()) {
            throw new IllegalArgumentException(
                    "Unterminated comment at line " + line
            );
        }

        index += 2;
    }
    private boolean isCommentStart(String sourceCode) {

        if (index + 1 >= sourceCode.length()) {
            return false;
        }

        char current = sourceCode.charAt(index);
        char next = sourceCode.charAt(index + 1);

        return current == '/'
                && (next == '/' || next == '*');
    }
    private void skipComment(String sourceCode) {

        char next = sourceCode.charAt(index + 1);

        if (next == '/') {
            skipSingleLineComment(sourceCode);
        } else {
            skipMultiLineComment(sourceCode);
        }
    }

    private static final Set<String> KEYWORDS = Set.of(
            "class",
            "interface",
            "enum",
            "public",
            "private",
            "protected",
            "static",
            "final",
            "void",
            "int",
            "long",
            "double",
            "float",
            "boolean",
            "char",
            "new",
            "if",
            "else",
            "for",
            "while",
            "do",
            "switch",
            "case",
            "default",
            "break",
            "continue",
            "return",
            "try",
            "catch",
            "finally",
            "throw",
            "throws",
            "extends",
            "implements",
            "this",
            "super",
            "package",
            "import"
    );
    private static final Set<Character> SYMBOLS = Set.of(
            '(',
            ')',
            '{',
            '}',
            '[',
            ']',
            ';',
            ',',
            '.',
            ':'
    );
    private static final Set<String> OPERATORS = Set.of(
            "+",
            "-",
            "*",
            "/",
            "%",
            "=",
            "==",
            "!=",
            ">",
            "<",
            ">=",
            "<=",
            "&&",
            "||",
            "!",
            "++",
            "--",
            "+=",
            "-=",
            "*=",
            "/=",
            "->"
    );
    private static final Set<String> LITERALS = Set.of(
            "true",
            "false",
            "null"
    );
}