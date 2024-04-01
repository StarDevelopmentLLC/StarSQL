package com.stardevllc.starsql.api.statements;

@FunctionalInterface
public interface SqlStatement {
    String build();
}