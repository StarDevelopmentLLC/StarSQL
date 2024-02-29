package com.stardevllc.starsql.sqlite;

import com.stardevllc.starsql.common.AbstractSQLDatabase;

import java.util.logging.Logger;

public class SQLiteDatabase extends AbstractSQLDatabase {
    public SQLiteDatabase(Logger logger, SQLiteProperties properties) {
        super(logger, properties);

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        this.url = "jdbc:sqlite:";
        if (properties.isMemory()) {
            this.url += ":memory:";
        } else {
            this.url += properties.getDatabaseName();
        }
    }
}
