package com.stardevllc.starsql.common.typehandlers.impl;

import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;
import com.stardevllc.starlib.reflection.ReflectionHelper;

import java.lang.reflect.Method;

public class EnumHandler extends SQLTypeHandler {
    public EnumHandler() {
        super(Enum.class, "varchar(1000)", (column, object) -> {
            if (object instanceof Enum<?> e) {
                return e.toString();
            }
            return null;
        }, (column, object) -> {
            if (object instanceof String str) {
                try {
                    Method valueOfMethod = ReflectionHelper.getClassMethod(column.getField().getType(), "valueOf", String.class);
                    valueOfMethod.setAccessible(true);
                    return valueOfMethod.invoke(null, str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return null;
        });
    }
    
    @Override
    public boolean matches(Class<?> clazz) {
        return Enum.class.isAssignableFrom(clazz);
    }
}
