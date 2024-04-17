package com.stardevllc.starsql.typehandlers.impl;

import com.stardevllc.starsql.typehandlers.SQLTypeHandler;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDHander extends SQLTypeHandler {
   private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    
    public UUIDHander() {
        super(UUID.class, "varchar(36)", (column, object) -> {
            if (object instanceof UUID uuid) {
                return uuid.toString();
            } else if (object instanceof String str) {
                if (UUID_PATTERN.matcher(str).matches()) {
                    return str;
                }
            }
            return "";
        }, (column, object) -> {
            if (object instanceof String str) {
                if (UUID_PATTERN.matcher(str).matches()) {
                    return UUID.fromString(str);
                }
            }
            
            return null;
        });
    }
}
