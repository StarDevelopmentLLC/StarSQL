package com.stardevllc.starsql.h2;

import com.stardevllc.starsql.common.SQLProperties;

public class H2Properties extends SQLProperties {
    
    private String type; //The H2 Type like file, mem, ssl, tcp etc...
    private String cipher;
    private String fileLock;
    private boolean ifExists;
    private boolean closeOnExit = true;
    private String initRunScript;
    private int traceLevelFile = -1;
    private boolean ignoreUnknownSettings;
    private String accessMode;
    private boolean autoReconnect;

    public String getType() {
        return type;
    }

    public H2Properties setType(String type) {
        this.type = type;
        return this;
    }

    public String getCipher() {
        return cipher;
    }

    public H2Properties setCipher(String cipher) {
        this.cipher = cipher;
        return this;
    }

    public String getFileLock() {
        return fileLock;
    }

    public H2Properties setFileLock(String fileLock) {
        this.fileLock = fileLock;
        return this;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public H2Properties setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
        return this;
    }

    public boolean isCloseOnExit() {
        return closeOnExit;
    }

    public H2Properties setCloseOnExit(boolean closeOnExit) {
        this.closeOnExit = closeOnExit;
        return this;
    }

    public String getInitRunScript() {
        return initRunScript;
    }

    public H2Properties setInitRunScript(String initRunScript) {
        this.initRunScript = initRunScript;
        return this;
    }

    public int getTraceLevelFile() {
        return traceLevelFile;
    }

    public H2Properties setTraceLevelFile(int traceLevelFile) {
        this.traceLevelFile = traceLevelFile;
        return this;
    }

    public boolean isIgnoreUnknownSettings() {
        return ignoreUnknownSettings;
    }

    public H2Properties setIgnoreUnknownSettings(boolean ignoreUnknownSettings) {
        this.ignoreUnknownSettings = ignoreUnknownSettings;
        return this;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public H2Properties setAccessMode(String accessMode) {
        this.accessMode = accessMode;
        return this;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public H2Properties setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    @Override
    public H2Properties setDatabaseName(String databaseName) {
        return (H2Properties) super.setDatabaseName(databaseName);
    }

    @Override
    public H2Properties setUsername(String username) {
        return (H2Properties) super.setUsername(username);
    }

    @Override
    public H2Properties setPassword(String password) {
        return (H2Properties) super.setPassword(password);
    }
}