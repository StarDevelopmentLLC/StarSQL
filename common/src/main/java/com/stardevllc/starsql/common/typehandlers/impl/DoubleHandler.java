package com.stardevllc.starsql.common.typehandlers.impl;

import com.stardevllc.starsql.api.interfaces.model.FieldModel;
import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;

public class DoubleHandler extends SQLTypeHandler {
    public DoubleHandler() {
        super(Double.class, "double", DoubleHandler::parse, DoubleHandler::parse);
        addAdditionalClass(double.class);
    }
    
    private static Object parse(FieldModel<SQLDatabase> column, Object object) {
        if (object instanceof Number number) {
            return number.doubleValue();
        } else if (object instanceof String str) {
            return Double.parseDouble(str);
        }
        return 0.0;
    }
}
