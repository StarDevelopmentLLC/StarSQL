package com.stardevllc.starsql.common.typehandlers.impl;

import com.stardevllc.starsql.api.interfaces.model.FieldModel;
import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;

public class FloatHandler extends SQLTypeHandler {
    public FloatHandler() {
        super(Float.class, "float", FloatHandler::parse, FloatHandler::parse);
        addAdditionalClass(float.class);
    }
    
    private static Object parse(FieldModel<SQLDatabase> column, Object object) {
        if (object instanceof Number number) {
            return number.floatValue();
        } else if (object instanceof String str) {
            return Float.parseFloat(str);
        }
        return 0.0F;
    }
}
