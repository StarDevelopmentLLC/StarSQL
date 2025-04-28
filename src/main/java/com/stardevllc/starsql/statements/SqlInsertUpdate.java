package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Table;

import java.util.List;

public class SqlInsertUpdate extends SqlInsert {

    private SqlColumnKey primaryKeyColumn;

    public SqlInsertUpdate(String tableName) {
        super(tableName);
    }
    
    public SqlInsertUpdate(Table table) {
        super(table);
    }

    public SqlInsertUpdate(Table table, boolean allColumns) {
        super(table, allColumns);
    }

    public SqlInsertUpdate primaryKeyColumn(String primaryKeyColumn) {
        this.primaryKeyColumn = new SqlColumnKey(this.tableName, primaryKeyColumn, null);
        return this;
    }

    public SqlInsertUpdate primaryKeyColumn(SqlColumnKey columnKey) {
        this.primaryKeyColumn = columnKey;
        return this;
    }

    @Override
    public SqlInsertUpdate columns(String... columns) {
        return (SqlInsertUpdate) super.columns(columns);
    }

    @Override
    public SqlInsertUpdate columns(SqlColumnKey... columnKeys) {
        return (SqlInsertUpdate) super.columns(columnKeys);
    }

    @Override
    public SqlInsertUpdate row(Object... values) {
        return (SqlInsertUpdate) super.row(values);
    }

    public String build() {
        StringBuilder sb = new StringBuilder(super.build());
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON DUPLICATE KEY UPDATE ");

        if (rows.size() != 1) {
            throw new IllegalArgumentException("Insert-Update statements only support a single row.");
        }

        int primaryIndex = -1;
        for (int i = 0; i < this.columns.size(); i++) {
            SqlColumnKey sqlColumnKey = columns.get(i);
            if (sqlColumnKey.equals(this.primaryKeyColumn)) {
                primaryIndex = i;
                break;
            }
        }

        if (primaryIndex == -1) {
            throw new IllegalArgumentException("No primary key column was set for an insert-update statement.");
        }

        this.columns.remove(primaryIndex);
        List<Object> row = this.rows.getFirst();
        row.remove(primaryIndex);

        for (int i = 0; i < this.columns.size(); i++) {
            SqlColumnKey column = this.columns.get(i);
            Object object = row.get(i);
            sb.append("`").append(column.getColumnName()).append("`='").append(object).append("', ");
        }

        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
