package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.typehandlers.SQLTypeHandler;

public class FloatHandler extends SQLTypeHandler {
    public FloatHandler() {
        super(Float.class, "float", FloatHandler::parse, FloatHandler::parse);
        addAdditionalClass(float.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.floatValue();
        } else if (object instanceof String str) {
            return Float.parseFloat(str);
        }
        return 0.0F;
    }
}
