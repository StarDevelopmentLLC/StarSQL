package com.stardevllc.starsql.sqlite;

import com.stardevllc.starsql.common.SQLProperties;

public class SQLiteProperties extends SQLProperties {
    
    private boolean memory;

    public boolean isMemory() {
        return memory;
    }

    public SQLiteProperties setMemory(boolean memory) {
        this.memory = memory;
        return this;
    }

    @Override
    public SQLiteProperties setDatabaseName(String databaseName) {
        return (SQLiteProperties) super.setDatabaseName(databaseName);
    }

    @Override
    public SQLiteProperties setUsername(String username) {
        return this;
    }

    @Override
    public SQLiteProperties setPassword(String password) {
        return this;
    }
}
