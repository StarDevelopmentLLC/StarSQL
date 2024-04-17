package com.stardevllc.starsql.model;

public record PushInfo(String sql, boolean generateKeys, Table table) {
}
