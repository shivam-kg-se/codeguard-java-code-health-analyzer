package com.codeguard.parser;

import com.codeguard.model.JavaClass;
import com.codeguard.model.JavaMethod;
import com.codeguard.model.JavaSource;
import com.codeguard.model.JavaType;

import java.util.ArrayList;
import java.util.List;

public class JavaSourceParser {

    private final List<Token> tokens;
    private int current;

    public JavaSourceParser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    // =========================================================
    // MAIN ENTRY POINT
    // =========================================================

    public JavaSource parseSource() {

        String packageName = parsePackage();

        List<String> imports = parseImports();

        JavaClass type = parseType();

        return new JavaSource(
                packageName,
                imports,
                type
        );
    }


    // =========================================================
    // PARENTHESIS MATCHING
    // =========================================================

    private int findMatchingParenthesis(int openingIndex) {

        int depth = 0;

        for (int i = openingIndex; i < tokens.size(); i++) {

            String value = tokens.get(i).value();

            if ("(".equals(value)) {
                depth++;
            }

            if (")".equals(value)) {

                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        throw new IllegalArgumentException(
                "Unmatched parenthesis"
        );
    }


    // =========================================================
    // BRACE MATCHING
    // =========================================================

    private int findMatchingBrace(int openingIndex) {

        int depth = 0;

        for (int i = openingIndex; i < tokens.size(); i++) {

            String value = tokens.get(i).value();

            if ("{".equals(value)) {
                depth++;
            }

            if ("}".equals(value)) {

                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        throw new IllegalArgumentException(
                "Unmatched brace"
        );
    }


    // =========================================================
    // METHOD CANDIDATE
    // =========================================================

    private boolean isMethodCandidate() {

        if (current >= tokens.size()) {
            return false;
        }

        Token token = tokens.get(current);

        // Method name should be an identifier
        if (token.type() != TokenType.IDENTIFIER) {
            return false;
        }

        // We need one token after method name
        if (current + 1 >= tokens.size()) {
            return false;
        }

        // identifier followed by '('
        return "(".equals(
                tokens.get(current + 1).value()
        );
    }


    // =========================================================
    // PARAMETER PARSING
    // =========================================================

    private List<String> parseParameters(
            int openingParenthesis,
            int closingParenthesis) {

        List<String> parameters =
                new ArrayList<>();

        StringBuilder parameter =
                new StringBuilder();

        for (int i = openingParenthesis + 1;
             i < closingParenthesis;
             i++) {

            String value =
                    tokens.get(i).value();

            // Parameter separator
            if (",".equals(value)) {

                if (parameter.length() > 0) {

                    parameters.add(
                            parameter.toString().trim()
                    );
                }

                parameter.setLength(0);

                continue;
            }

            if (parameter.length() > 0) {
                parameter.append(" ");
            }

            parameter.append(value);
        }

        // Last parameter
        if (parameter.length() > 0) {

            parameters.add(
                    parameter.toString().trim()
            );
        }

        return parameters;
    }


    // =========================================================
    // METHOD PARSING
    // =========================================================

    private JavaMethod parseMethod(String className) {

        int methodNameIndex = current;

        String methodName =
                tokens.get(current).value();

        // methodName -> (
        int openingParenthesis =
                current + 1;

        int closingParenthesis =
                findMatchingParenthesis(
                        openingParenthesis
                );

        // ) -> {
        int bodyStart =
                closingParenthesis + 1;


        /*
         * If there is no '{', this is not a normal
         * method with a body.
         *
         * Example:
         *
         * void calculate();
         *
         * This can happen in interfaces/abstract declarations.
         */
        if (bodyStart >= tokens.size()
                || !"{".equals(
                tokens.get(bodyStart).value()
        )) {

            return null;
        }

        int bodyEnd =
                findMatchingBrace(bodyStart);

        List<Token> bodyTokens =
                new ArrayList<>(
                        tokens.subList(
                                bodyStart + 1,
                                bodyEnd
                        )
                );


        // =====================================================
        // CONSTRUCTOR DETECTION
        // =====================================================

        boolean constructor =
                methodName.equals(className);

        String returnType;

        if (constructor) {

            returnType = null;

        } else {

            /*
             * For:
             *
             * public void calculate()
             *
             * methodNameIndex points to calculate.
             *
             * methodNameIndex - 1 points to void.
             */
            returnType =
                    tokens.get(
                            methodNameIndex - 1
                    ).value();
        }


        // =====================================================
        // PARAMETERS
        // =====================================================

        List<String> parameters =
                parseParameters(
                        openingParenthesis,
                        closingParenthesis
                );


        // =====================================================
        // LINE INFORMATION
        // =====================================================

        int startLine =
                tokens.get(
                        methodNameIndex
                ).line();

        int endLine =
                tokens.get(
                        bodyEnd
                ).line();


        // Move parser after method body
        current = bodyEnd + 1;


        return new JavaMethod(
                methodName,
                returnType,
                parameters,
                startLine,
                endLine,
                bodyTokens
        );
    }


    // =========================================================
    // METHOD LIST
    // =========================================================

    private List<JavaMethod> parseMethods(
            int bodyStart,
            String className) {

        List<JavaMethod> methods =
                new ArrayList<>();

        /*
         * Start just after the class '{'
         */
        current = bodyStart + 1;

        while (current < tokens.size()) {

            /*
             * End of class
             */
            if ("}".equals(
                    tokens.get(current).value()
            )) {
                break;
            }


            /*
             * Possible method
             */
            if (isMethodCandidate()) {

                JavaMethod method =
                        parseMethod(className);

                if (method != null) {

                    methods.add(method);

                    continue;
                }
            }

            /*
             * Not a method.
             * Move to next token.
             */
            current++;
        }

        return methods;
    }


    // =========================================================
    // PACKAGE
    // =========================================================

    private String parsePackage() {

        if (current >= tokens.size()) {
            return null;
        }

        Token token =
                tokens.get(current);

        if (!"package".equals(
                token.value()
        )) {
            return null;
        }

        current++;

        StringBuilder packageName =
                new StringBuilder();

        while (current < tokens.size()) {

            Token currentToken =
                    tokens.get(current);

            if (";".equals(
                    currentToken.value()
            )) {

                current++;

                break;
            }

            packageName.append(
                    currentToken.value()
            );

            current++;
        }

        return packageName.toString();
    }


    // =========================================================
    // IMPORTS
    // =========================================================

    private List<String> parseImports() {

        List<String> imports =
                new ArrayList<>();

        while (current < tokens.size()
                && "import".equals(
                tokens.get(current).value()
        )) {

            current++;

            StringBuilder importName =
                    new StringBuilder();

            while (current < tokens.size()) {

                Token token =
                        tokens.get(current);

                if (";".equals(
                        token.value()
                )) {

                    current++;

                    break;
                }

                importName.append(
                        token.value()
                );

                current++;
            }

            imports.add(
                    importName.toString()
            );
        }

        return imports;
    }


    // =========================================================
    // FIND CLASS BODY
    // =========================================================

    private int findOpeningBrace() {

        /*
         * Don't modify 'current'.
         * Only search and return the index.
         */
        for (int i = current;
             i < tokens.size();
             i++) {

            if ("{".equals(
                    tokens.get(i).value()
            )) {

                return i;
            }
        }

        throw new IllegalArgumentException(
                "Type body not found"
        );
    }


    // =========================================================
    // PARSE CLASS / INTERFACE / ENUM
    // =========================================================

    public JavaClass parseType() {

        while (current < tokens.size()) {

            Token token =
                    tokens.get(current);

            if (token.type()
                    == TokenType.KEYWORD) {

                JavaType type =
                        getJavaType(
                                token.value()
                        );

                if (type != null) {

                    /*
                     * Move from:
                     *
                     * class
                     *
                     * to:
                     *
                     * ClassName
                     */
                    current++;

                    Token name =
                            getNextIdentifier();

                    /*
                     * Move after class name.
                     */
                    current++;

                    /*
                     * Find {
                     */
                    int bodyStart =
                            findOpeningBrace();

                    /*
                     * Parse methods inside class.
                     */
                    List<JavaMethod> methods =
                            parseMethods(
                                    bodyStart,
                                    name.value()
                            );

                    return new JavaClass(
                            name.value(),
                            type,
                            methods
                    );
                }
            }

            current++;
        }

        throw new IllegalArgumentException(
                "No class, interface or enum declaration found"
        );
    }


    // =========================================================
    // JAVA TYPE
    // =========================================================

    private JavaType getJavaType(
            String keyword) {

        return switch (keyword) {

            case "class" ->
                    JavaType.CLASS;

            case "interface" ->
                    JavaType.INTERFACE;

            case "enum" ->
                    JavaType.ENUM;

            default ->
                    null;
        };
    }


    // =========================================================
    // IDENTIFIER
    // =========================================================

    private Token getNextIdentifier() {

        if (current >= tokens.size()) {

            throw new IllegalArgumentException(
                    "Expected type name"
            );
        }

        Token token =
                tokens.get(current);

        if (token.type()
                != TokenType.IDENTIFIER) {

            throw new IllegalArgumentException(
                    "Expected identifier but found: "
                            + token.value()
            );
        }

        return token;
    }
}