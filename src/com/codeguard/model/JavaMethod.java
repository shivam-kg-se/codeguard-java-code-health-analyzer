package com.codeguard.model;

import com.codeguard.parser.Token;
import com.codeguard.parser.Tokenizer;

import java.util.List;

public record JavaMethod (

    String name,
    String returnType,
    List<String> parameters,
    int startLine,
    int endLine,
    List<Token> bodyTokens
) {
    }
