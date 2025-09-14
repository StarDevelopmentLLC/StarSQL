package com.stardevllc.starsql.model;

import com.stardevllc.starsql.model.Column.Option;
import com.stardevllc.starsql.model.Column.Type;
import com.stardevllc.starsql.statements.SqlSelect;

import java.util.*;

public class Table {
    protected Database database;
    protected String databaseName;
    protected String name;
    protected Map<String, Column> columns = new HashMap<>();

    public Table(String databaseName, String name, Map<String, Column> columns) {
        this.databaseName = databaseName;
        this.name = name;
        this.columns.putAll(columns);
    }
    
    public Table(Database database, String name, Map<String, Column> columns) {
        this(database.getName(), name, columns);
        this.database = database;
    }
    
    public Table(String databaseName, String name) {
        this.databaseName = databaseName;
        this.name = name;
    }
    
    public Table(Database database, String name) {
        this(database.getName(), name);
        this.database = database;
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
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    public String getFullyQualifiedName() {
        return "`" + databaseName + "`.`" + name + "`";
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
        
        Column column = new Column(this, name, type, position, foreignKey, options);
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