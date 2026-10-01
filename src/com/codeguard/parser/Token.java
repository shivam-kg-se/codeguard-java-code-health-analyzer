package com.codeguard.parser;

public record Token(TokenType type,String value,int line) {
}
