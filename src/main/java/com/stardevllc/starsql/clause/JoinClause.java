package com.stardevllc.starsql.clause;

import com.stardevllc.starsql.statements.ColumnKey;

public class JoinClause implements SqlClause {
    private JoinType joinType;
    private String table1Name, table2Name;
    private ColumnKey table1Column, table2Column;

    public JoinClause(JoinType joinType, String table1Name, String table2Name, ColumnKey table1Column, ColumnKey table2Column) {
        this.joinType = joinType;
        this.table1Name = table1Name;
        this.table2Name = table2Name;
        this.table1Column = table1Column;
        this.table2Column = table2Column;
    }

    public JoinClause(JoinType joinType, ColumnKey table1Column, ColumnKey table2Column) {
        this(joinType, table1Column.getTableName(), table2Column.getTableName(), table1Column, table2Column);
    }

    public JoinClause(JoinType joinType) {
        this.joinType = joinType;
    }
    
    public JoinClause() {
        
    }
    
    public JoinClause type(JoinType type) {
        this.joinType = type;
        return this;
    }
    
    public JoinClause setTableOne(String table1Name, ColumnKey table1Column) {
        this.table1Name = table1Name;
        this.table1Column = table1Column;
        return this;
    }

    public JoinClause setTableOne(String table1Name, String table1Column) {
        return setTableOne(table1Name, new ColumnKey(table1Name, table1Column, null));
    }

    public JoinClause setTableTwo(String table2Name, ColumnKey table2Column) {
        this.table2Name = table2Name;
        this.table2Column = table2Column;
        return this;
    }

    public JoinClause setTableTwo(String table2Name, String table2Column) {
        return setTableTwo(table2Name, new ColumnKey(table2Name, table2Column, null));
    }
    
    public String build() {
        if (joinType == null) {
            return "";
        }
        
        if (isEmpty(table1Name) || isEmpty(table2Name)) {
            throw new IllegalArgumentException("One or both of the table names is empty.");
        }
        
        if (table1Column == null || table2Column == null) {
            throw new IllegalArgumentException("One or both of the colums are not specified.");
        }

        String table1ColumnName = table1Column.getAlias() != null ? table1Column.getAlias() : table1Column.getColumnName();
        String table2ColumnName = table2Column.getAlias() != null ? table2Column.getAlias() : table2Column.getColumnName();
        return joinType.name() + " JOIN `" + table2Name + "` ON `" + table1Name + "`.`" + table1ColumnName + "`=`" + table2Name + "`.`" + table2ColumnName + "`";
    }
    
    private boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }
}