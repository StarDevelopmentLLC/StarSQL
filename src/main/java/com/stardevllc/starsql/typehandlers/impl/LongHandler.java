package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.typehandlers.SQLTypeHandler;

public class LongHandler extends SQLTypeHandler {
    public LongHandler() {
        super(Long.class, "bigint", LongHandler::parse, LongHandler::parse);
        addAdditionalClass(long.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.longValue();
        } else if (object instanceof String str) {
            return Long.parseLong(str);
        }
        return 0;
    }
}
