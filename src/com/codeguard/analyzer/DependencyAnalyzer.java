package com.codeguard.analyzer;

import com.codeguard.model.JavaSource;

import java.util.*;

public class DependencyAnalyzer {

    public Map<String, Set<String>> analyze(
            List<JavaSource> sources) {

        Map<String, Set<String>> graph =
                new HashMap<>();

        // Step 1: collect project classes
        Set<String> projectClasses =
                new HashSet<>();

        for (JavaSource source : sources) {

            projectClasses.add(
                    source.type().name()
            );
        }

        // Step 2: analyze every class
        for (JavaSource source : sources) {

            String className =
                    source.type().name();

            Set<String> dependencies =
                    new HashSet<>();

            // Step 3: check imports
            for (String importName : source.imports()) {

                String[] parts =
                        importName.split("\\.");

                String importedClass =
                        parts[parts.length - 1];

                // Ignore wildcard imports
                if ("*".equals(importedClass)) {
                    continue;
                }

                // Step 4: project dependency
                if (projectClasses.contains(importedClass)) {

                    dependencies.add(
                            importedClass
                    );
                }
            }

            graph.put(
                    className,
                    dependencies
            );
        }

        return graph;
    }
}