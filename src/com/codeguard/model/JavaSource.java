package com.codeguard.model;

import java.util.List;

public record JavaSource (

    String packageName,
    List<String> imports,
    JavaClass type
) {
}