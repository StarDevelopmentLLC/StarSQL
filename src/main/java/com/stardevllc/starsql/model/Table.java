package com.stardevllc.starsql.model;

import com.stardevllc.starsql.model.Column.Option;
import com.stardevllc.starsql.model.Column.Type;
import com.stardevllc.starsql.statements.SqlSelect;

import java.util.*;

public class Table {
    protected Database database;
    protected String name;
    protected Map<String, Column> columns = new HashMap<>();
    
    public Table(Database database, String name, Map<String, Column> columns) {
        this.database = database;
        this.name = name;
        if (columns != null) {
            this.columns.putAll(columns);
        }
    }
    
    public Table(Database database, String name) {
        this(database, name, null);
    }
    
    public SqlSelect select() {
        return new SqlSelect(this, true);
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public void setDatabase(Database database) {
        this.database = database;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFullyQualifiedName() {
        return "`" + this.database.getName() + "`.`" + name + "`";
    }
    
    public Map<String, Column> getColumns() {
        return new HashMap<>(columns);
    }
    
    public void addColumn(Column column) {
        this.columns.put(column.getName().toLowerCase(), column);
    }
    
    public Column getOrAddColumn(Column column) {
        if (!this.columns.containsKey(column.getName().toLowerCase())) {
            addColumn(column);
        }
        
        return this.getColumn(column.getName());
    }
    
    public Column getOrCreateColumn(String name, Type type, int position, ForeignKey foreignKey, Option... options) {
        if (this.columns.containsKey(name.toLowerCase())) {
            return this.getColumn(name);
        }
        
        Column column = new Column(this.database, this, name, type, position, List.of(), options);
        addColumn(column);
        return column;
    }
    
    public Column getOrCreateColumn(String name, Type type, int position, Option... options) {
        return getOrCreateColumn(name, type, position, null, options);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Table table = (Table) o;
        return name.equalsIgnoreCase(table.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    public int compareTo(Column o) {
        return this.name.compareTo(o.getName());
    }
    
    public Column getColumn(String columnName) {
        return this.columns.get(columnName.toLowerCase());
    }
}