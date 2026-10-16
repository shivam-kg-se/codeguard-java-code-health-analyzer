package com.codeguard.model;

public record Injection(
        String className,
        String dependency,
        InjectionType type

) {
}
