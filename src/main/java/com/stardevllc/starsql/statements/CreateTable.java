package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Column;
import com.stardevllc.starsql.model.ForeignKey;

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
        SortedSet<Column> columns = new TreeSet<>((o1, o2) -> {
            if (o1.getPosition() == 0) {
                return -1;
            }
            
            if (o2.getPosition() == 0) {
                return 1;
            }
            
            if (o1.getPosition() == o2.getPosition()) {
                return 1;
            }
            
            return Integer.compare(o1.getPosition(), o2.getPosition());
        });
        
        columns.addAll(this.columns.values());
        
        List<ForeignKey> foreignKeys = new ArrayList<>();
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(this.name).append(" (");
        for (Column column : columns) {
            sb.append("`").append(column.getName()).append("`").append(" ").append(column.getType().getDataType());
            if (column.getType().getSize() > 0) {
                sb.append("(").append(column.getType().getSize()).append(")");
            }
            
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
            
            if (column.getForeignKeys() != null && !column.getForeignKeys().isEmpty()) {
                foreignKeys.addAll(column.getForeignKeys());
            }
        }
        
        if (!foreignKeys.isEmpty()) {
            for (ForeignKey foreignKey : foreignKeys) {
                sb.append("CONSTRAINT ").append(foreignKey.name()).append(" FOREIGN KEY (").append("`").append(foreignKey.referencedTable().columnName()).append("`) REFERENCES `").append(foreignKey.primaryTable().tableName()).append("`(`").append(foreignKey.primaryTable().columnName()).append("`) ").append("ON DELETE ").append(foreignKey.deleteRule().name().replace("_", " ")).append(" ON UPDATE ").append(foreignKey.updateRule().name().replace("_", " ")).append(", ");
            }
        }
        
        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        return sb.toString();
    }
}
