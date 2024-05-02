package com.stardevllc.starsql.converters.impl;

import com.stardevllc.starsql.converters.SQLConverter;
import com.stardevllc.starsql.model.Column;

public class BooleanConverter extends SQLConverter<Boolean> {
    public BooleanConverter() {
        super(Boolean.class, "varchar(5)");
        addAdditionalClass(boolean.class);
    }

    @Override
    public Object serializeToSQL(Column column, Object input) {
        if (input instanceof Number number) {
            return number.intValue() == 1 ? "true" : "false";
        } else if (input instanceof Boolean bool) {
            return bool ? "true" : "false";
        }
        return false;
    }

    @Override
    public Boolean deserializeFromSQL(Column column, Object input) {
        if (input instanceof Boolean bool) {
            return bool;
        } else if (input instanceof Number number) {
            return number.intValue() == 1;
        } else if (input instanceof String str) {
            return Boolean.parseBoolean(str);
        }
        return false;
    }
}
