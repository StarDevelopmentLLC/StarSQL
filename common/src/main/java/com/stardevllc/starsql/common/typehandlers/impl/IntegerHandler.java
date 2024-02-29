package com.stardevllc.starsql.common.typehandlers.impl;

import com.stardevllc.starsql.api.interfaces.model.FieldModel;
import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;

public class IntegerHandler extends SQLTypeHandler {
    public IntegerHandler() {
        super(Integer.class, "int", IntegerHandler::parse, IntegerHandler::parse);
        addAdditionalClass(int.class);
    }
    
    private static Object parse(FieldModel<SQLDatabase> column, Object object) {
        if (object instanceof Number number) {
            return number.intValue();
        } else if (object instanceof String str) {
            return Integer.parseInt(str);
        }
        return 0;
    }
}
