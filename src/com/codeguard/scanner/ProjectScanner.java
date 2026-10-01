package com.codeguard.scanner;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectScanner {
    public List<Path> scanProject(Path projectDirectory) {
        // Implement the logic to scan the project directory and return a list of Java file paths
        if(projectDirectory==null){
            throw new IllegalArgumentException("project directory can not be null");

        }
        if(!Files.exists(projectDirectory)){
            throw new IllegalArgumentException(
                    "project directory does not exits" + projectDirectory
            );
        }
        if(!Files.isDirectory(projectDirectory)){
            throw new IllegalArgumentException(
                    "provided path is not directory"
            );
        }
        List<Path> files = new ArrayList<>();
        try{
             Files.walkFileTree(
                     projectDirectory,
                     new SimpleFileVisitor<>(){
                 @Override
                         public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes){
                     if(!directory.equals(projectDirectory) && isIgnoredDirectory(directory)){
                         return FileVisitResult.SKIP_SUBTREE;
                     }
                     return FileVisitResult.CONTINUE;
                 }
                 @Override
                     public FileVisitResult visitFile(Path file, BasicFileAttributes attributes){
                     files.add(file);
                     return FileVisitResult.CONTINUE;
                 }
            }
             );
             return files;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to scan project :"+projectDirectory,e);
        }
    }
    private boolean isIgnoredDirectory(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return false;
        }
        return IGNORED_DIRECTORIES.contains(
                fileName.toString()
        );
    }
    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            ".git",
            ".idea",
            "target",
            "build",
            "out"
    );
}
