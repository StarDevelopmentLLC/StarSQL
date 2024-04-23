package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.typehandlers.SQLConverter;

public class DoubleConverter extends SQLConverter {
    public DoubleConverter() {
        super(Double.class, "double");
        addAdditionalClass(double.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.doubleValue();
        } else if (object instanceof String str) {
            return Double.parseDouble(str);
        }
        return 0.0;
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
