package com.codeguard.scanner;

import java.nio.file.Path;
import java.util.List;

public class JavaFileScanner {
    public List<Path> findJavaFiles(List<Path> files){
        return files.stream()
                .filter(file -> file.toString().toLowerCase().endsWith(".java"))
                .toList();
    }
}
