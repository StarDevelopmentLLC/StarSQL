package com.stardevllc.starsql.model;

import com.stardevllc.starsql.statements.ColumnKey;

import java.sql.DatabaseMetaData;

public record ForeignKey(String name, ColumnKey primaryTable, ColumnKey referencedTable, Rule updateRule, Rule deleteRule) {
    
    public enum Rule {
        NO_ACTION(DatabaseMetaData.importedKeyNoAction),
        CASCADE(DatabaseMetaData.importedKeyCascade),
        SET_NULL(DatabaseMetaData.importedKeySetNull),
        SET_DEFAULT(DatabaseMetaData.importedKeySetDefault),
        RESTRICT(DatabaseMetaData.importedKeyRestrict);
        
        private final int metadataValue;
        
        Rule(int metadataValue) {
            this.metadataValue = metadataValue;
        }
        
        public static Rule parseRule(int value) {
            for (Rule rule : values()) {
                if (rule.metadataValue == value) {
                    return rule;
                }
            }
            
            return RESTRICT;
        }
    }
}
