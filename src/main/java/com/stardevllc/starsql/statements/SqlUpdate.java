package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.model.Table;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SqlUpdate implements SqlStatement {
    private final String tableName;
    
    private List<SqlColumnKey> columns = new LinkedList<>();
    private List<Object> values = new LinkedList<>();
    private WhereClause whereClause = new WhereClause();
    
    public SqlUpdate(String tableName) {
        this.tableName = tableName;
    }

    public SqlUpdate(Table table) {
        this(table, false);
    }
    
    public SqlUpdate(Table table, boolean allColumns) {
        this(table.getName());
        if (allColumns) {
            for (Column column : table.getColumns().values()) {
                if (!column.isPrimaryKey()) {
                    this.columns.add(column.toKey());
                }
            }
        }
    }
    
    public SqlUpdate add(SqlColumnKey columnKey, Object value) {
        this.columns.add(columnKey);
        this.values.add(value);
        return this;
    }
    
    public SqlUpdate add(Column column, Object value) {
        return add(column.toKey(), value);
    }

    public SqlUpdate add(String column, Object value) {
        return add(new SqlColumnKey(tableName, column, null), value);
    }
    
    public SqlUpdate columns(String... columns) {
        if (columns != null) {
            for (String column : columns) {
                this.columns.add(new SqlColumnKey(tableName, column, null));
            }
        }
        return this;
    }
    
    public SqlUpdate columns(SqlColumnKey... columnKeys) {
        if (columnKeys != null) {
            this.columns.addAll(List.of(columnKeys));
        }
        return this;
    }
    
    public SqlUpdate columns(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                this.columns.add(column.toKey());
            }
        }
        return this;
    }
    
    public SqlUpdate values(Object... values) {
        if (values != null) {
            this.values.addAll(List.of(values));
        }
        return this;
    }

    public SqlUpdate where(Consumer<WhereClause> consumer) {
        consumer.accept(this.whereClause);
        return this;
    }

    public SqlUpdate addWhereCondition(String column, String operator, Object value) {
        this.whereClause.addCondition(column, operator, value);
        return this;
    }

    public SqlUpdate whereColumns(String... whereColumns) {
        this.whereClause.columns(whereColumns);
        return this;
    }

    public SqlUpdate whereOperators(String... whereOperators) {
        this.whereClause.conditions(whereOperators);
        return this;
    }

    public SqlUpdate whereValues(Object... whereValues) {
        this.whereClause.values(whereValues);
        return this;
    }
    
    public String build() {
        if (columns.size() != values.size()) {
            throw new IllegalArgumentException("Columns and Values must be the same size.");
        }
        
        StringBuilder sb = new StringBuilder("UPDATE `").append(tableName).append("` (");
        for (SqlColumnKey column : columns) {
            String tableName = column.getTableName() != null ? "`" + column.getTableName() + "`." : "";
            String columnName = column.getAlias() != null ? column.getAlias() : column.getColumnName();
            sb.append("`").append(tableName).append("`.`").append(columnName).append("`, ");
        }
        
        sb.delete(sb.length() - 2, sb.length()).append(") SET (");
        
        for (Object value : this.values) {
            sb.append("'").append(value).append("', ");
        }
        sb.delete(sb.length() - 2, sb.length()).append(" ").append(whereClause.build());
        return sb.toString().trim() + ";";
    }
}