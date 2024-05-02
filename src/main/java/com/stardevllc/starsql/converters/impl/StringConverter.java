package com.stardevllc.starsql.converters.impl;

import com.stardevllc.starsql.converters.SQLConverter;
import com.stardevllc.starsql.model.Column;

public class StringConverter extends SQLConverter {
    public StringConverter() {
        super(String.class, "varchar(1000)");
        addAdditionalClass(Character.class, char.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof String str) {
            return str;
        } else if (object instanceof Character character) {
            return character;
        } else if (object != null) {
            return object.toString();
        }
        return "";
    }

    @Override
    public Object serializeToSQL(Column column, Object input) {
        return parse(column, input);
    }

    @Override
    public Object deserializeFromSQL(Column column, Object input) {
        return parse(column, input);
    }
}
