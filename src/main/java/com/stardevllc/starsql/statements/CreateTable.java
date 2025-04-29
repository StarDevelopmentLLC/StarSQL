package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Column;

import java.util.*;

public class CreateTable implements SqlStatement {
    
    private String name;
    private Map<String, Column> columns = new HashMap<>();
    
    public CreateTable(String name, Set<Column> columns) {
        this.name = name;
        for (Column column : columns) {
            this.columns.put(column.getName().toLowerCase(), column);
        }
    }
    
    public CreateTable(String name) {
        this.name = name;
    }
    
    public CreateTable addColumn(Column column) {
        this.columns.put(column.getName().toLowerCase(), column);
        return this;
    }
    
    public CreateTable columns(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                addColumn(column);
            }
        }
        
        return this;
    }
    
    @Override
    public String build() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(this.name);
        for (Column column : this.columns.values()) {
            sb.append("`").append(column.getName()).append("`").append(" ").append(column.getType());
            if (column.isPrimaryKey()) {
                sb.append(" PRIMARY KEY");
            }
            
            if (column.isAutoIncrement()) {
                sb.append(" AUTO_INCREMENT");
            }
            
            if (!column.isNullable()) {
                sb.append(" NOT NULL");
            }
            
            if (column.isUnique() && !column.isPrimaryKey()) {
                sb.append(" UNIQUE");
            }
            
            sb.append(", ");
        }
        
        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        return sb.toString();
    }
}
