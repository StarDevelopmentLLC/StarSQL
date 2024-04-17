package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.typehandlers.SQLTypeHandler;

public class BooleanHandler extends SQLTypeHandler {
    public BooleanHandler() {
        super(Boolean.class, "varchar(5)", (column, object) -> {
            if (object instanceof Number number) {
                return number.intValue() == 1 ? "true" : "false";
            } else if (object instanceof Boolean bool) {
                return bool ? "true" : "false";
            }
            return false;
        }, (column, object) -> {
            if (object instanceof Boolean bool) {
                return bool;
            } else if (object instanceof Number number) {
                return number.intValue() == 1;
            } else if (object instanceof String str) {
                return Boolean.parseBoolean(str);
            }
            return false;
        });
        addAdditionalClass(boolean.class);
    }
}
