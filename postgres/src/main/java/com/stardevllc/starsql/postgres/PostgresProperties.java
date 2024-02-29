package com.stardevllc.starsql.postgres;

import com.stardevllc.starsql.common.SQLProperties;

public class PostgresProperties extends SQLProperties {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public PostgresProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public PostgresProperties setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public PostgresProperties setDatabaseName(String databaseName) {
        return (PostgresProperties) super.setDatabaseName(databaseName);
    }

    @Override
    public PostgresProperties setUsername(String username) {
        return (PostgresProperties) super.setUsername(username);
    }

    @Override
    public PostgresProperties setPassword(String password) {
        return (PostgresProperties) super.setPassword(password);
    }
}
