package com.stardevllc.starsql.mysql;

import com.stardevllc.starsql.common.SQLProperties;

public class MySQLProperties extends SQLProperties {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public MySQLProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public MySQLProperties setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public MySQLProperties setDatabaseName(String databaseName) {
        return (MySQLProperties) super.setDatabaseName(databaseName);
    }

    @Override
    public MySQLProperties setUsername(String username) {
        return (MySQLProperties) super.setUsername(username);
    }

    @Override
    public MySQLProperties setPassword(String password) {
        return (MySQLProperties) super.setPassword(password);
    }
}
