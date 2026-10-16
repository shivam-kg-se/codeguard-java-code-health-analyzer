package com.codeguard;

import com.codeguard.analyzer.InjectionAnalyzer;
import com.codeguard.model.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ==========================================
        // 1. UserService
        // ==========================================

        JavaClass userService =
                new JavaClass(
                        "UserService",
                        JavaType.CLASS,
                        new ArrayList<>(),   // fields
                        new ArrayList<>()    // methods
                );

        JavaSource userSource =
                new JavaSource(
                        "com.example",
                        List.of(),
                        userService
                );


        // ==========================================
        // 2. Constructor Injection
        // ==========================================

        JavaMethod constructor =
                new JavaMethod(
                        "OrderService",
                        null,
                        List.of("UserService userService"),
                        5,
                        7,
                        new ArrayList<>()
                );


        // ==========================================
        // 3. Setter Injection
        // ==========================================

        JavaMethod setter =
                new JavaMethod(
                        "setUserService",
                        "void",
                        List.of("UserService userService"),
                        10,
                        12,
                        new ArrayList<>()
                );


        // ==========================================
        // 4. Field Injection
        // ==========================================

        JavaField userServiceField =
                new JavaField(
                        "userService",
                        "UserService"
                );



        // ==========================================
        // 5. OrderService
        // ==========================================

        JavaClass orderService =
                new JavaClass(
                        "OrderService",
                        JavaType.CLASS,

                        // Fields
                        List.of(userServiceField),

                        // Methods
                        List.of(
                                constructor,
                                setter
                        )
                );
        JavaField nameField =
                new JavaField(
                        "name",
                        "String"
                );

        JavaSource orderSource =
                new JavaSource(
                        "com.example",
                        List.of("com.example.UserService"),
                        orderService
                );


        // ==========================================
        // 6. All project sources
        // ==========================================

        List<JavaSource> sources =
                List.of(
                        userSource,
                        orderSource
                );


        // ==========================================
        // 7. Run Injection Analyzer
        // ==========================================

        InjectionAnalyzer analyzer =
                new InjectionAnalyzer();

        List<Injection> injections =
                analyzer.analyze(sources);


        // ==========================================
        // 8. Print Result
        // ==========================================

        System.out.println("======================================");
        System.out.println("         INJECTION ANALYSIS");
        System.out.println("======================================");

        for (Injection injection : injections) {

            System.out.println(
                    injection.className()
                            + " -> "
                            + injection.dependency()
                            + " ["
                            + injection.type()
                            + "]"
            );
        }

        System.out.println("======================================");
    }
}