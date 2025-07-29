package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.model.Table;

import java.util.LinkedList;
import java.util.List;

public class SqlInsert implements SqlStatement {

    protected final String tableName;
    protected List<ColumnKey> columns = new LinkedList<>();
    protected List<List<Object>> rows = new LinkedList<>();

    public SqlInsert(String tableName) {
        this.tableName = tableName;
    }
    
    public SqlInsert(Table table) {
        this.tableName = table.getName();
    }

    public SqlInsert(Table table, boolean allColumns) {
        this(table.getName());
        if (allColumns) {
            for (Column column : table.getColumns().values()) {
                columns.add(new ColumnKey(this.tableName, column.getName(), null));
            }
        }
    }

    public SqlInsert columns(String... columns) {
        if (columns != null) {
            for (String column : columns) {
                this.columns.add(new ColumnKey(this.tableName, column, null));
            }
        }

        return this;
    }

    public SqlInsert columns(ColumnKey... columnKeys) {
        if (columnKeys != null) {
            this.columns.addAll(List.of(columnKeys));
        }

        return this;
    }

    public SqlInsert row(Object... values) {
        if (values != null) {
            rows.add(new LinkedList<>(List.of(values)));
        }

        return this;
    }
    
    public SqlInsert add(ColumnKey columnKey, Object... values) {
        this.columns.add(columnKey);
        if (values != null) {
            rows.add(new LinkedList<>(List.of(values)));
        }
        
        return this;
    }

    public String build() {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("You cannot have empty columns in an insert statement.");
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("You cannot have empty rows in an insert statement.");
        }

        StringBuilder sb = new StringBuilder("INSERT INTO ").append("`").append(tableName).append("` (");
        for (ColumnKey column : columns) {
            String tableName = column.getTableName() != null ? "`" + column.getTableName() + "`." : "";
            String columnName = column.getAlias() != null ? column.getAlias() : column.getColumnName();

            sb.append(tableName).append("`").append(columnName).append("`").append(", ");
        }

        sb.delete(sb.length() - 2, sb.length()).append(") VALUES ");

        for (List<Object> row : rows) {
            sb.append("(");
            for (Object object : row) {
                sb.append("'").append(object).append("', ");
            }

            sb.delete(sb.length() - 2, sb.length()).append("), ");
        }

        sb.delete(sb.length() - 2, sb.length()).append(";");
        return sb.toString();
    }
}
