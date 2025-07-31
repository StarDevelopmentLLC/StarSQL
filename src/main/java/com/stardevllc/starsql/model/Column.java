package com.stardevllc.starsql.model;

import com.stardevllc.starsql.statements.ColumnKey;

import java.util.Objects;

public class Column {
    private Table table;
    private String name;
    
    private String type;
    private int size;
    private int position;
    private boolean nullable;
    private boolean autoIncrement;
    private boolean primaryKey;
    private boolean unique;
    
    private ForeignKey foreignKey;
    
    public Column(Table table, String name, String type, int size, int position, boolean nullable, boolean autoIncrement, boolean primaryKey, boolean unique, ForeignKey foreignKey) {
        this.table = table;
        this.name = name;
        this.type = type;
        this.size = size;
        this.position = position;
        this.nullable = nullable;
        this.autoIncrement = autoIncrement;
        this.primaryKey = primaryKey;
        this.unique = unique;
        this.foreignKey = foreignKey;
    }

    public ColumnKey toKey() {
        return toKey(null);
    }

    public ColumnKey toKey(String alias) {
        return new ColumnKey(this.table.getName(), this.name, alias);
    }
    
    public ForeignKey getForeignKey() {
        return foreignKey;
    }
    
    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getPosition() {
        return position;
    }
    
    public boolean isNullable() {
        return nullable;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public boolean isAutoIncrement() {
        return autoIncrement;
    }
    
    public boolean isUnique() {
        return unique;
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