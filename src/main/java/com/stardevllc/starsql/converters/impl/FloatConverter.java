package com.stardevllc.starsql.converters.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.converters.SQLConverter;

public class FloatConverter extends SQLConverter<Float> {
    public FloatConverter() {
        super(Float.class, "float");
        addAdditionalClass(float.class);
    }
    
    private static Float parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.floatValue();
        } else if (object instanceof String str) {
            return Float.parseFloat(str);
        }
        return 0.0F;
    }

    @Override
    public Object serializeToSQL(Column column, Object input) {
        return parse(column, input);
    }

    @Override
    public Float deserializeFromSQL(Column column, Object input) {
        return parse(column, input);
    }
}
