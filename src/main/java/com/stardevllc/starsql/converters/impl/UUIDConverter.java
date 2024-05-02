package com.stardevllc.starsql.converters.impl;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.converters.SQLConverter;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDConverter extends SQLConverter<UUID> {
   private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    
    public UUIDConverter() {
        super(UUID.class, "varchar(36)");
    }

    @Override
    public Object serializeToSQL(Column column, Object input) {
        if (input instanceof UUID uuid) {
            return uuid.toString();
        } else if (input instanceof String str) {
            if (UUID_PATTERN.matcher(str).matches()) {
                return str;
            }
        }
        return "";
    }

    @Override
    public UUID deserializeFromSQL(Column column, Object input) {
        if (input instanceof String str) {
            if (UUID_PATTERN.matcher(str).matches()) {
                return UUID.fromString(str);
            }
        }

        return null;
    }
}
