package com.codeguard.analyzer;

import com.codeguard.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InjectionAnalyzer {

    public List<Injection> analyze(List<JavaSource> sources) {

        List<Injection> injections = new ArrayList<>();

        Set<String> projectClasses = sources.stream()
                .map(source -> source.type().name())
                .collect(java.util.stream.Collectors.toSet());

        for (JavaSource source : sources) {

            String className = source.type().name();
            for (JavaField field : source.type().fields()) {

                String dependency = field.type();

                if (projectClasses.contains(dependency)) {

                    injections.add(
                            new Injection(
                                    className,
                                    dependency,
                                    InjectionType.FIELD
                            )
                    );
                }
            }

            for (JavaMethod method : source.type().methods()) {

                // Constructor
                if (method.returnType() == null) {

                    for (String parameter : method.parameters()) {

                        String dependency = getParameterType(parameter);

                        if (projectClasses.contains(dependency)) {

                            injections.add(
                                    new Injection(
                                            className,
                                            dependency,
                                            InjectionType.CONSTRUCTOR
                                    )
                            );
                        }
                    }
                }
                if (method.name().startsWith("set")
                        && method.parameters().size() == 1) {

                    String parameter = method.parameters().get(0);

                    String dependency = getParameterType(parameter);

                    if (projectClasses.contains(dependency)) {

                        injections.add(
                                new Injection(
                                        className,
                                        dependency,
                                        InjectionType.SETTER
                                )
                        );
                    }
                }
            }
        }

        return injections;
    }

    private String getParameterType(String parameter) {

        String[] parts = parameter.trim().split("\\s+");

        return parts[0];
    }
}