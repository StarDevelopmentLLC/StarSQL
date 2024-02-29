package com.stardevllc.starsql.common;

import com.stardevllc.starsql.api.interfaces.model.Table;

public record SQLPushInfo(String sql, boolean generateKeys, Table table) {
}
