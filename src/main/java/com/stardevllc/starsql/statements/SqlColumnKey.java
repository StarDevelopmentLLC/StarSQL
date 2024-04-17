package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Column;

import java.util.Objects;

public class SqlColumnKey {
    private final String tableName, columnName, alias;

    public SqlColumnKey(String tableName, String columnName, String alias) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.alias = alias;
    }

    public SqlColumnKey(String columnName, String alias) {
        this(null, columnName, alias);
    }

    public SqlColumnKey(String columnName) {
        this(columnName, null);
    }
    
    public SqlColumnKey(Column column, String alias) {
        this(column.getTable().getName(), column.getName(), alias);
    }
    
    public SqlColumnKey(Column column) {
        this(column, null);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getAlias() {
        return alias;
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

        SqlColumnKey that = (SqlColumnKey) o;

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