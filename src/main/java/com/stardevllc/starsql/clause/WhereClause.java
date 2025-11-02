package com.stardevllc.starsql.clause;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.statements.ColumnKey;

import java.util.LinkedList;
import java.util.List;

public class WhereClause implements SqlClause {
    private List<ColumnKey> columns = new LinkedList<>();
    private List<String> conditions = new LinkedList<>();
    private List<Object> values = new LinkedList<>();
    private List<WhereOperator> operators = new LinkedList<>();

    public WhereClause addCondition(String column, String condition, Object value) {
        return addCondition(new ColumnKey(column), condition, value);
    }

    public WhereClause addCondition(WhereOperator operator, String column, String condition, Object value) {
        return addCondition(operator, new ColumnKey(column), condition, value);
    }

    public WhereClause addCondition(WhereOperator operator, ColumnKey columnKey, String condition, Object value) {
        this.columns.add(columnKey);
        this.conditions.add(condition);
        this.values.add(value);
        this.operators.add(operator);
        return this;
    }
    
    public WhereClause addCondition(ColumnKey columnKey, String condition, Object value) {
        this.columns.add(columnKey);
        this.conditions.add(condition);
        this.values.add(value);
        this.operators.add(WhereOperator.NONE);
        return this;
    }
    
    public WhereClause addCondition(Column column, String condition, Object value) {
        return addCondition(column.toKey(), condition, value);
    }

    public WhereClause addCondition(WhereOperator operator, Column column, String condition, Object value) {
        return addCondition(operator, column.toKey(), condition, value);
    }

    public WhereClause columns(String... columns) {
        if (columns != null) {
            for (String column : columns) {
                this.columns.add(new ColumnKey(column));
            }
        }
        return this;
    }
    
    public WhereClause columns(ColumnKey... columnKeys) {
        if (columnKeys != null) {
            this.columns.addAll(List.of(columnKeys));
        }
        return this;
    }
    
    public WhereClause columns(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                this.columns.add(new ColumnKey(column.getTable().getName(), column.getName()));
            }
        }
        
        return this;
    }

    public WhereClause conditions(String... operators) {
        if (operators != null) {
            this.conditions.addAll(List.of(operators));
        }
        return this;
    }

    public WhereClause values(Object... values) {
        if (values != null) {
            this.values.addAll(List.of(values));
        }
        return this;
    }
    
    public WhereClause operators(WhereOperator... operators) {
        if (operators != null) {
            this.operators.addAll(List.of(operators));
        }
        return this;
    }
    
    public String build() {
        int columnSize = this.columns.size();
        int conditionsSize = this.conditions.size();
        int valuesSize = this.values.size();
        
        if (columnSize != conditionsSize || columnSize != valuesSize) {
            throw new IllegalArgumentException("Columns, conditions and values do not have the same amount of entries.");
        }
        
        if (columnSize == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder("WHERE ");
        
        for (int i = 0; i < columnSize; i++) {
            WhereOperator operator = this.operators.get(i);
            if (operator != null && operator != WhereOperator.NONE) {
                sb.append(operator.name()).append(" ");
            }
            ColumnKey column = this.columns.get(i);
            if (column.tableName() != null) {
                sb.append("`").append(column.tableName()).append("`.");
            }
            sb.append("`").append(column.columnName()).append("`").append(this.conditions.get(i)).append("'").append(this.values.get(i)).append("' ");
        }
        
        return sb.toString().trim();
    }
}
