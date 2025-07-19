package com.stardevllc.starsql.model;

import java.util.*;

public class Table {
    private String database;
    private String name;
    private Map<String, Column> columns = new HashMap<>();

    public Table(String database, String name, Map<String, Column> columns) {
        this.database = database;
        this.name = name;
        this.columns.putAll(columns);
    }
    
    public Table(Database database, String name, Map<String, Column> columns) {
        this(database.getName(), name, columns);
    }
    
    public Table(String database, String name) {
        this.database = database;
        this.name = name;
    }
    
    public Table(Database database, String name) {
        this(database.getName(), name);
    }

    public String getName() {
        return name;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public String getFullyQualifiedName() {
        return "`" + database + "`.`" + name + "`";
    }
    
    public Map<String, Column> getColumns() {
        return new HashMap<>(columns);
    }
    
    public void addColumn(Column column) {
        this.columns.put(column.getName().toLowerCase(), column);
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