package com.stardevllc.starsql.statements;

import com.stardevllc.starsql.model.Table;

import java.util.function.Consumer;

public class SqlDelete implements SqlStatement {
    
    private String tableName;
    private WhereClause whereClause = new WhereClause();
    
    public SqlDelete(String tableName) {
        this.tableName = tableName;
    }
    
    public SqlDelete(Table table) {
        this(table.getName());
    }
    
    public SqlDelete where(Consumer<WhereClause> consumer) {
        consumer.accept(this.whereClause);
        return this;
    }
    
    public SqlDelete whereClause(WhereClause whereClause) { 
        this.whereClause = whereClause;
        return this;
    }
    
    @Override
    public String build() {
        return "DELETE FROM `" + tableName + "` WHERE " + whereClause.build() + ";";
    }
}