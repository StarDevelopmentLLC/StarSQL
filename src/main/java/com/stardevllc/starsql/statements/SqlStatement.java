package com.stardevllc.starsql.statements;

@FunctionalInterface
public interface SqlStatement {
    String build();
}