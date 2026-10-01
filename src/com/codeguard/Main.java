package com.codeguard;

import com.codeguard.model.JavaClass;
import com.codeguard.model.JavaMethod;
import com.codeguard.model.JavaSource;
import com.codeguard.parser.JavaSourceParser;
import com.codeguard.parser.Token;
import com.codeguard.parser.Tokenizer;
import com.codeguard.scanner.ProjectScanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =====================================================
        // 1. CHECK COMMAND-LINE ARGUMENT
        // =====================================================

        if (args.length == 0) {

            System.out.println(
                    "Usage: java com.codeguard.Main <project-path>"
            );

            return;
        }

        // First argument = project directory
        Path projectPath = Path.of(args[0]);


        // =====================================================
        // 2. SCAN PROJECT
        // =====================================================

        ProjectScanner scanner =
                new ProjectScanner();

        List<Path> javaFiles =
                scanner.scanProject(projectPath);


        System.out.println();
        System.out.println("========================================");
        System.out.println("          CODEGUARD DAY 2 TEST");
        System.out.println("========================================");

        System.out.println(
                "Java files found: "
                        + javaFiles.size()
        );


        // =====================================================
        // 3. PROCESS EACH JAVA FILE
        // =====================================================

        for (Path javaFile : javaFiles) {

            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println(
                    "FILE: " + javaFile
            );
            System.out.println("----------------------------------------");

            try {

                // =============================================
                // 4. READ SOURCE CODE
                // =============================================

                String sourceCode =
                        Files.readString(javaFile);


                // =============================================
                // 5. TOKENIZE
                // =============================================

                Tokenizer tokenizer =
                        new Tokenizer();

                List<Token> tokens =
                        tokenizer.tokenize(sourceCode);


                System.out.println(
                        "Total tokens: "
                                + tokens.size()
                );


                // =============================================
                // 6. PARSE
                // =============================================

                JavaSourceParser parser =
                        new JavaSourceParser(tokens);

                JavaSource source =
                        parser.parseSource();


                // =============================================
                // 7. PRINT PACKAGE
                // =============================================

                System.out.println(
                        "Package: "
                                + source.packageName()
                );


                // =============================================
                // 8. PRINT IMPORTS
                // =============================================

                System.out.println(
                        "Imports: "
                                + source.imports()
                );


                // =============================================
                // 9. PRINT CLASS
                // =============================================

                JavaClass javaClass =
                        source.type();

                System.out.println(
                        "Type: "
                                + javaClass.type()
                );

                System.out.println(
                        "Class: "
                                + javaClass.name()
                );


                // =============================================
                // 10. PRINT METHODS
                // =============================================

                List<JavaMethod> methods =
                        javaClass.methods();

                System.out.println(
                        "Methods: "
                                + methods.size()
                );


                for (JavaMethod method : methods) {

                    System.out.println();

                    System.out.println(
                            "  Method: "
                                    + method.name()
                    );

                    System.out.println(
                            "  Return Type: "
                                    + method.returnType()
                    );

                    System.out.println(
                            "  Parameters: "
                                    + method.parameters()
                    );

                    System.out.println(
                            "  Lines: "
                                    + method.startLine()
                                    + " - "
                                    + method.endLine()
                    );


                    // =========================================
                    // 11. PRINT METHOD BODY TOKENS
                    // =========================================

                    System.out.println(
                            "  Body:"
                    );

                    for (Token token :
                            method.bodyTokens()) {

                        System.out.print(
                                "    "
                                        + token.type()
                                        + " : "
                                        + token.value()
                                        + " (line "
                                        + token.line()
                                        + ")"
                        );

                        System.out.println();
                    }
                }

            } catch (Exception e) {

                System.out.println(
                        "ERROR while parsing: "
                                + javaFile
                );

                System.out.println(
                        e.getMessage()
                );
            }
        }


        // =====================================================
        // 12. END
        // =====================================================

        System.out.println();
        System.out.println("========================================");
        System.out.println("        DAY 2 TEST COMPLETED");
        System.out.println("========================================");
    }
}