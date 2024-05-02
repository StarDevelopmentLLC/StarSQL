package com.stardevllc.starsql.converters.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.converters.SQLConverter;
import com.stardevllc.starlib.reflection.ReflectionHelper;

import java.lang.reflect.Method;

public class EnumConverter extends SQLConverter<Enum> {
    public EnumConverter() {
        super(Enum.class, "varchar(1000)");
    }

    @Override
    public Object serializeToSQL(Column column, Object input) {
        if (input instanceof Enum<?> e) {
            return e.toString();
        }
        return null;
    }

    @Override
    public Enum deserializeFromSQL(Column column, Object input) {
        if (input instanceof String str) {
            try {
                Method valueOfMethod = ReflectionHelper.getClassMethod(column.getField().getType(), "valueOf", String.class);
                valueOfMethod.setAccessible(true);
                return (Enum) valueOfMethod.invoke(null, str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return Enum.class.isAssignableFrom(clazz);
    }
}
