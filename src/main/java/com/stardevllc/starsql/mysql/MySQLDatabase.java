package com.stardevllc.starsql.mysql;

import com.stardevllc.starsql.common.AbstractSQLDatabase;

import java.util.logging.Logger;

public class MySQLDatabase extends AbstractSQLDatabase {
    public MySQLDatabase(Logger logger, MySQLProperties properties) {
        super(logger, properties);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        this.url = "jdbc:mysql://" + properties.getHost();
        
        if (properties.getPort() != 0) {
            this.url += ":" + properties.getPort();
        }
        
        this.url += "/" + properties.getDatabaseName();
    }
}