package com.stardevllc.starsql.model;

import com.stardevllc.starlib.objects.builder.IBuilder;
import com.stardevllc.starsql.statements.ColumnKey;

import java.util.*;

public class Column {
    private final Database database;
    private final Table table;
    private final String name;
    
    private final Type type;
    
    private final int position;
    
    private final EnumSet<Option> options = EnumSet.noneOf(Option.class);
    
    private final ForeignKey foreignKey;
    
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
    
    public Column(Database database, Table table, String name, Type type, int position, ForeignKey foreignKey, Option... options) {
        this.database = database;
        this.table = table;
        this.name = name;
        this.type = type;
        this.position = position;
        this.foreignKey = foreignKey;
        if (options != null) {
            this.options.addAll(List.of(options));
        }
    }
    
    public Column(Database database, Table table, String name, Type type, int position, Option... options) {
        this(database, table, name, type, position, null, options);
    }
    
    private Column(Builder builder) {
        this.database = builder.database;
        this.table = builder.table;
        this.name = builder.name;
        this.type = builder.type;
        this.position = builder.position;
        this.options.addAll(builder.options);
        if (builder.referenced != null) {
            this.foreignKey = new ForeignKey(builder.foreignKeyName, new ColumnKey(table.getName(), this.name), builder.referenced, builder.onUpdateRule, builder.onDeleteRule);
        } else {
            this.foreignKey = null;
        }
    }
    
    public ColumnKey toKey() {
        return toKey(null);
    }
    
    public ColumnKey toKey(String alias) {
        return new ColumnKey(this.database.getName(), this.table.getName(), this.name, alias);
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
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder implements IBuilder<Column, Builder> {
        
        private Database database;
        private Table table;
        private String name;
        
        private Type type;
        
        private int position;
        
        private final EnumSet<Option> options = EnumSet.noneOf(Option.class);
        
        private String foreignKeyName;
        private ColumnKey referenced;
        private ForeignKey.Rule onUpdateRule, onDeleteRule;
        
        private Builder() {
        }
        
        private Builder(Builder builder) {
            this.database = builder.database;
            this.table = builder.table;
            this.name = builder.name;
            this.type = builder.type;
            this.position = builder.position;
            this.options.addAll(builder.options);
            this.foreignKeyName = builder.foreignKeyName;
            this.referenced = builder.referenced;
            this.onUpdateRule = builder.onUpdateRule;
            this.onDeleteRule = builder.onDeleteRule;
        }
        
        public Builder database(Database database) {
            this.database = database;
            return self();
        }
        
        public Builder table(Table table) {
            this.table = table;
            return self();
        }
        
        public Builder name(String name) {
            this.name = name;
            return self();
        }
        
        public Builder type(Type type) {
            this.type = type;
            return self();
        }
        
        public Builder type(String dataType, int size) {
            this.type = new Type(dataType, size);
            return self();
        }
        
        public Builder type(String dataType) {
            this.type = new Type(dataType);
            return self();
        }
        
        public Builder position(int position) {
            this.position = position;
            return self();
        }
        
        public Builder option(Option option, Option... options) {
            this.options.add(option);
            if (options != null) {
                this.options.addAll(List.of(options));
            }
            
            return self();
        }
        
        public Builder nullable() {
            return option(Option.NULLABLE);
        }
        
        public Builder autoIncrement() {
            return option(Option.AUTO_INCREMENT);
        }
        
        public Builder primaryKey() {
            return option(Option.PRIMARY_KEY);
        }
        
        public Builder unique() {
            return option(Option.UNIQUE);
        }
        
        public Builder foreignKey(String name, ColumnKey referenced, ForeignKey.Rule updateRule, ForeignKey.Rule deleteRule) {
            this.foreignKeyName = name;
            this.referenced = referenced;
            this.onUpdateRule = updateRule;
            this.onDeleteRule = deleteRule;
            return self();
        }
        
        public Builder foreignKey(String name, String referencedTable, String referencedColumn, ForeignKey.Rule updateRule, ForeignKey.Rule deleteRule) {
            this.foreignKeyName = name;
            this.referenced = new ColumnKey(referencedTable, referencedColumn);
            this.onUpdateRule = updateRule;
            this.onDeleteRule = deleteRule;
            return self();
        }
        
        @Override
        public Column build() {
            return new Column(this);
        }
        
        @Override
        public Builder clone() {
            return new Builder(this);
        }
    }
}