package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.typehandlers.SQLTypeHandler;
import com.stardevllc.starlib.Value;

import java.lang.reflect.Method;

public class ValueHandler extends SQLTypeHandler {
    public ValueHandler() {
        super(Value.class, "varchar(1000)", (column, object) -> {
            Value value = (Value) object;
            if (value == null) {
                return "null";
            }
    
            String encoded = value.getType().name() + ":";
            if (value.get() == null) {
                return encoded + "null";
            }
    
            if (value.getType() == Value.Type.ENUM) {
                Enum<?> enumObject = (Enum<?>) value.get();
                return encoded + enumObject.getClass().getName() + ":" + enumObject.name();
            }
    
            return encoded + value.get();
        }, (column, object) -> {
            String encoded = (String) object;
            if (encoded == null || encoded.isEmpty() || encoded.equalsIgnoreCase("null")) {
                return null;
            }
            String[] split = encoded.split(":");
            if (split.length == 1) {
                return new Value(Value.Type.valueOf(split[0]), null);
            }
    
            Value.Type type = Value.Type.valueOf(split[0].toUpperCase());
    
            String rawValue = split[1];
            if (rawValue.isEmpty() || rawValue.equalsIgnoreCase("null")) {
                return new Value(type, null);
            }
    
            Object obj = switch (type) {
                case INTEGER -> Integer.parseInt(rawValue);
                case STRING -> rawValue;
                case LONG -> Long.parseLong(rawValue);
                case BOOLEAN -> Boolean.parseBoolean(rawValue);
                case DOUBLE -> Double.parseDouble(rawValue);
                case ENUM -> {
                    try {
                        Class<?> enumClazz = Class.forName(split[1]);
                        Method method = enumClazz.getDeclaredMethod("valueOf", String.class);
                        yield  method.invoke(null, split[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        yield null;
                    }
                }
            };
    
            return new Value(type, obj);
        });
    }
}
