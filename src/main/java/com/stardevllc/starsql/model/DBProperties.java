package com.stardevllc.starsql.model;

public class DBProperties {
    protected String databaseName, username, password, host = "localhost";
    protected int port = 3306;
    
    static {
        DBProperties properties = new DBProperties().setDatabaseName("test").setHost("localhost").setPort(3306).setUsername("user").setPassword("password");
    }
    
    public DBProperties setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public String getHost() {
        return host;
    }

    public DBProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public DBProperties setPort(int port) {
        this.port = port;
        return this;
    }
    
    public DBProperties setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public DBProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
