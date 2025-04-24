package com.stardevllc.starsql.statements;

import java.util.HashMap;
import java.util.Map;

public class OrderByClause {
    public enum Type {
        ASC, DESC
    }
    
    private Map<String, Type> columns = new HashMap<>();
    
    public OrderByClause add(String column, Type type) {
        this.columns.put(column, type);
        return this;
    }
    
    public OrderByClause add(String column) {
        return add(column, Type.ASC);
    }
    
    public String build() {
        if (columns.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder("ORDER BY ");
        columns.forEach((name, type) -> sb.append("`").append(name).append("` ").append(type.name()).append(", "));
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}