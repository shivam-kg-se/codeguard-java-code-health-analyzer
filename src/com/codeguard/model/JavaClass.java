package com.codeguard.model;

import java.util.List;

public record JavaClass (

    String name,
    JavaType type,
    List<JavaField> fields,
    List<JavaMethod> methods
) {
}