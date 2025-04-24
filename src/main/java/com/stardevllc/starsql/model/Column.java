package com.stardevllc.starsql.model;

import com.stardevllc.starsql.statements.SqlColumnKey;

import java.util.Objects;

public class Column{
    private final Table table;

    private final String name;
    private String type;
    private boolean primaryKey, autoIncrement, notNull, unique;
    
    public Column(Table table, String name) {
        this.table = table;
        this.name = name;
    }

    public SqlColumnKey toKey() {
        return toKey(null);
    }

    public SqlColumnKey toKey(String alias) {
        return new SqlColumnKey(this.table.getName(), this.name, alias);
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public Table getParent() {
        return null;
    }

    public String getType() {
        return type;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isNotNull() {
        return notNull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Column column = (Column) o;
        return Objects.equals(name, column.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}