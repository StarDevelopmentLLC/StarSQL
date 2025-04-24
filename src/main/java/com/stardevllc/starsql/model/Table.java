package com.stardevllc.starsql.model;

import java.util.*;

public class Table {
    private String name;
    private final Set<Column> columns = new TreeSet<>();
    private Column primaryKeyColumn;
    private int columnOrderIndex = 1000;

    public Table(String name) {
        this.name = name;
    }

    public Column getPrimaryField() {
        return primaryKeyColumn;
    }

    public Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public int getColumnOrderIndex() {
        return columnOrderIndex;
    }

    public void setColumnOrderIndex(int columnOrderIndex) {
        this.columnOrderIndex = columnOrderIndex;
    }

    public String getName() {
        return name;
    }

    public Set<Column> getColumns() {
        return new TreeSet<>(columns);
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
        for (Column column : this.columns) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        return null;
    }
}