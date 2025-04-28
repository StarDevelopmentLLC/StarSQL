package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.model.Table;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SqlSelect implements SqlStatement {
    private final String tableName;
    private List<SqlColumnKey> columns = new LinkedList<>();
    private WhereClause whereClause = new WhereClause();
    private List<JoinClause> joinClauses = new LinkedList<>();
    private OrderByClause orderByClause = new OrderByClause();

    /**
     * Constructs a select statement using the table name.
     * @param tableName The name of the table that this select statement is performed on. 
     */
    public SqlSelect(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Constructs a select statement based on a table
     * @param table The table of the statement
     */
    public SqlSelect(Table table) {
        this(table, false);
    }

    /**
     * Constructs a statement based on a table, and if you want it to be automatically populated with all the columns of the table.
     * @param table The table
     * @param allColumns True if you want all columns to be selected, false if you want to customize it with the other methods. 
     */
    public SqlSelect(Table table, boolean allColumns) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }
        this.tableName = table.getName();
        if (allColumns) {
            table.getColumns().forEach((name, column) -> columns.add(new SqlColumnKey(this.tableName, column.getName(), null)));
        }
    }
    
    public SqlSelect columns(String... columns) {
        if (columns != null) {
            for (String column : columns) {
                this.columns.add(new SqlColumnKey(this.tableName, column, null));
            }
        }
        return this;
    }
    
    public SqlSelect columns(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                this.columns.add(new SqlColumnKey(column.getTable().getName(), column.getName(), null));
            }
        }
        
        return this;
    }
    
    public SqlSelect columns(SqlColumnKey... columns) {
        if (columns != null) {
            this.columns.addAll(List.of(columns));
        }
        
        return this;
    }

    public SqlSelect addColumn(String column) {
        return addColumn(column, null);
    }
    
    public SqlSelect addColumn(Column column) {
        return addColumn(column.getTable().getName(), column.getName(), null);
    }
    
    public SqlSelect addColumn(String column, String alias) {
        return addColumn(this.tableName, column, alias);
    }
    
    public SqlSelect addColumn(Column column, String alias) {
        return addColumn(column.getTable().getName(), column.getName(), alias);
    }

    public SqlSelect addColumn(String table, String column, String alias) {
        return addColumn(new SqlColumnKey(table, column, alias));
    }
    
    public SqlSelect addColumn(SqlColumnKey column) {
        this.columns.add(column);
        return this;
    }
    
    public SqlSelect where(Consumer<WhereClause> consumer) {
        consumer.accept(this.whereClause);
        return this;
    }
    
    public SqlSelect whereClause(WhereClause clause) {
        if (clause != null) {
            this.whereClause = clause;
        }
        
        return this;
    }

    public SqlSelect addWhereCondition(String column, String operator, Object value) {
        this.whereClause.addCondition(column, operator, value);
        return this;
    }

    public SqlSelect whereColumns(String... whereColumns) {
        this.whereClause.columns(whereColumns);
        return this;
    }

    public SqlSelect whereOperators(String... whereOperators) {
        this.whereClause.conditions(whereOperators);
        return this;
    }

    public SqlSelect whereValues(Object... whereValues) {
        this.whereClause.values(whereValues);
        return this;
    }
    
    public SqlSelect addOrderBy(String column, OrderByClause.Type type) {
        this.orderByClause.add(column, type);
        return this;
    }
    
    public SqlSelect addOrderBy(String column) {
        this.orderByClause.add(column);
        return this;
    }
    
    public SqlSelect orderBy(Consumer<OrderByClause> consumer) {
        consumer.accept(this.orderByClause);
        return this;
    }
    
    public SqlSelect join(JoinType type, Consumer<JoinClause> consumer) {
        JoinClause joinClause = new JoinClause(type);
        consumer.accept(joinClause);
        this.joinClauses.add(joinClause);
        return this;
    }
    
    public SqlSelect joinClause(JoinClause joinClause) {
        if (joinClause != null) {
            this.joinClauses.add(joinClause);
        }
        
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder("SELECT ");
        if (columns.isEmpty()) {
            sb.append("*");
        }

        for (SqlColumnKey column : this.columns) {
            String tableName = column.getTableName() != null ? "`" + column.getTableName() + "`.`" : "";
            sb.append(tableName).append(column.getColumnName()).append("`");
            if (column.getAlias() != null) {
                sb.append(" AS `").append(column.getAlias()).append("`");
            }
            sb.append(", ");
        }
        
        if (!columns.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(" FROM `").append(this.tableName).append("`").append(" ");
        sb.append(this.whereClause.build()).append(" ").append(orderByClause.build()).append(" ");
        
        if (!joinClauses.isEmpty()) {
            for (JoinClause joinClause : joinClauses) {
                sb.append(joinClause.build()).append(" ");
            }
        }
        
        return sb.toString().trim() + ";";
    }
}