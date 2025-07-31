package com.stardevllc.starsql.model;

public class ForeignKey {
    private String table, column;
    private String referencedTable, referencedColumn;
    
    public ForeignKey(String table, String column, String referencedTable, String referencedColumn) {
        this.table = table;
        this.column = column;
        this.referencedTable = referencedTable;
        this.referencedColumn = referencedColumn;
    }
    
    public ForeignKey(Column column, Column referencedColumn) {
        this(column.getTable().getName(), column.getName(), referencedColumn.getTable().getName(), referencedColumn.getName());
    }
    
    public String getTable() {
        return table;
    }
    
    public String getColumn() {
        return column;
    }
    
    public String getReferencedTable() {
        return referencedTable;
    }
    
    public String getReferencedColumn() {
        return referencedColumn;
    }
}
