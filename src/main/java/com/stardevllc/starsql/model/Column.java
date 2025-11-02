package com.stardevllc.starsql.model;

import com.stardevllc.starsql.statements.ColumnKey;

import java.util.*;

public class Column {
    private Database database;
    private Table table;
    private String name;
    
    private Type type;
    
    private int position;
    
    private EnumSet<Option> options = EnumSet.noneOf(Option.class);
    
    private boolean nullable;
    private boolean autoIncrement;
    private boolean primaryKey;
    private boolean unique;
    
    private List<ForeignKey> foreignKeys;
    
    public enum Option {
        NULLABLE, AUTO_INCREMENT, PRIMARY_KEY, UNIQUE
    }
    
    public static class Type {
        private String dataType;
        private int size;
        
        public Type(String dataType, int size) {
            this.dataType = dataType;
            this.size = size;
        }
        
        public Type(String dataType) {
            this.dataType = dataType;
        }
        
        public String getDataType() {
            return dataType;
        }
        
        public int getSize() {
            return size;
        }
    }
    
    public Column(Database database, Table table, String name, Type type, int position, List<ForeignKey> foreignKeys, Option... options) {
        this.database = database;
        this.table = table;
        this.name = name;
        this.type = type;
        this.position = position;
        this.foreignKeys = foreignKeys;
        if (options != null) {
            this.options.addAll(List.of(options));
        }
    }
    
    public Column(Database database, Table table, String name, Type type, int position, Option... options) {
        this(database, table, name, type, position, null, options);
    }

    public ColumnKey toKey() {
        return toKey(null);
    }

    public ColumnKey toKey(String alias) {
        return new ColumnKey(this.database.getName(), this.table.getName(), this.name, alias);
    }
    
    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }
    
    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }
    
    public Type getType() {
        return type;
    }
    
    public int getPosition() {
        return position;
    }
    
    public boolean hasOption(Option option) {
        return this.options.contains(option);
    }
    
    public void addOption(Option option, Option... options) {
        this.options.add(option);
        if (options != null) {
            this.options.addAll(List.of(options));
        }
    }
    
    public void removeOption(Option option, Option... options) {
        this.options.remove(option);
        if (options != null) {
            List.of(options).forEach(this.options::remove);
        }
    }
    
    public boolean isNullable() {
        return hasOption(Option.NULLABLE);
    }
    
    public boolean isPrimaryKey() {
        return hasOption(Option.PRIMARY_KEY);
    }
    
    public boolean isAutoIncrement() {
        return hasOption(Option.AUTO_INCREMENT);
    }
    
    public boolean isUnique() {
        return hasOption(Option.UNIQUE) || hasOption(Option.PRIMARY_KEY);
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