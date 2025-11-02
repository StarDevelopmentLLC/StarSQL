package com.stardevllc.starsql.statements;

import java.util.Objects;

public record ColumnKey(String datasebaseName, String tableName, String columnName, String alias) {
    public ColumnKey(String tableName, String columnName) {
        this(null, tableName, columnName, null);
    }
    
    public ColumnKey(String column) {
        this(null, null, column, null);
    }
    
    public ColumnKey(String table, String column, String alias) {
        this(null, table, column, alias);
    }
    
    public String getName() {
        return alias != null ? alias : this.columnName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        ColumnKey that = (ColumnKey) o;
        
        if (!Objects.equals(tableName, that.tableName)) {
            return false;
        }
        return Objects.equals(columnName, that.columnName);
    }
    
    @Override
    public int hashCode() {
        int result = tableName != null ? tableName.hashCode() : 0;
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }
}