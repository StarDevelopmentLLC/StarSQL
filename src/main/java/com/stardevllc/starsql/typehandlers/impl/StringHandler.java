package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.typehandlers.SQLTypeHandler;

public class StringHandler extends SQLTypeHandler {
    public StringHandler() {
        super(String.class, "varchar(1000)", StringHandler::parse, StringHandler::parse);
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
}
