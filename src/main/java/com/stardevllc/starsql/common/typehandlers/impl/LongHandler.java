package com.stardevllc.starsql.common.typehandlers.impl;

import com.stardevllc.starsql.api.interfaces.model.FieldModel;
import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;

public class LongHandler extends SQLTypeHandler {
    public LongHandler() {
        super(Long.class, "bigint", LongHandler::parse, LongHandler::parse);
        addAdditionalClass(long.class);
    }
    
    private static Object parse(FieldModel<SQLDatabase> column, Object object) {
        if (object instanceof Number number) {
            return number.longValue();
        } else if (object instanceof String str) {
            return Long.parseLong(str);
        }
        return 0;
    }
}
