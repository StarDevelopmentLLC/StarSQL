package com.stardevllc.starsql.common;

import com.stardevllc.starsql.api.interfaces.ObjectCodec;
import com.stardevllc.starsql.api.interfaces.model.ClassModel;
import com.stardevllc.starsql.api.interfaces.model.Database;
import com.stardevllc.starsql.api.interfaces.model.Column;
import com.stardevllc.starsql.api.interfaces.model.Row;
import com.stardevllc.starsql.api.interfaces.model.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLRow implements Row {
    private final Map<String, Object> data = new HashMap<>();
    private Table table;
    
    public SQLRow(ResultSet rs, AbstractSQLDatabase database) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                table = database.getTable(metaData.getTableName(i));
                String columnName = metaData.getColumnName(i).toLowerCase();
                Column column = table.getColumn(columnName);
                if (column == null) {
                    continue;
                }
                
                String columnLabel = metaData.getColumnLabel(i);
                if (rs.getObject(i) == null) {
                    this.data.put(columnLabel, null);
                } else if (column.getCodec() != null) {
                    data.put(columnLabel, column.getCodec().decode(rs.getString(i)));
                } else if (column.getTypeHandler() != null) {
                    Object object = column.getTypeHandler().getDeserializer().deserialize(column, rs.getObject(i));
                    this.data.put(columnLabel, object);
                } else {
                    this.data.put(columnLabel, rs.getObject(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Database getDatabase() {
        return table.getDatabase();
    }

    @Override
    public ClassModel<?> getClassModel() {
        return table;
    }

    @Override
    public Object getObject(String key) {
        return data.get(key.toLowerCase());
    }
    
    @Override
    public String getString(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof String str) {
            return str;
        }
        if (value == null) {
            return "null";
        } else {
            return value.toString();
        }
    }
    
    @Override
    public int getInt(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Integer) {
            return (int) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return 0;
    }
    
    @Override
    public long getLong(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Long) {
            return (long) value;
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return 0;
    }
    
    @Override
    public double getDouble(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        return 0;
    }
    
    @Override
    public float getFloat(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Float) {
            return (float) value;
        } else if (value instanceof String) {
            return Float.parseFloat((String) value);
        }
        return 0;
    }
    
    @Override
    public boolean getBoolean(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Boolean) {
            return (boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Integer) {
            return (int) value == 1;
        } else if (value instanceof Long) {
            return (long) value == 1;
        }
        return false;
    }
    
    @Override
    public UUID getUuid(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof UUID uuid) {
            return uuid;
        } else if (value instanceof String str) {
            return UUID.fromString(str);
        }
        return null;
    }
    
    @Override
    public <T> T get(String key, ObjectCodec<T> codec) {
        Object value = data.get(key.toLowerCase());
        return codec.decode((String) value);
    }
    
    @Override
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }
}