package com.stardevllc.starsql.h2;

import com.stardevllc.starsql.api.interfaces.model.Row;
import com.stardevllc.starsql.common.AbstractSQLDatabase;
import com.stardevllc.starsql.common.SQLRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class H2Database extends AbstractSQLDatabase {
    public H2Database(Logger logger, H2Properties properties) {
        super(logger, properties);

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String url = "jdbc:h2:";
        if (exists(properties.getType())) {
            url += properties.getType() + ":";
        }
        
        url += properties.getDatabaseName() + ";DATABASE_TO_LOWER=TRUE;MODE=MYSQL";
        
        if (exists(properties.getCipher())) {
            url += ";CIPHER=" + properties.getCipher().toUpperCase();
        }
        
        if (exists(properties.getFileLock())) {
            url += ";FILE_LOCK=" + properties.getFileLock().toUpperCase();
        }
        
        if (properties.isIfExists()) {
            url += ";IF_EXISTS=TRUE";
        }
        
        if (!properties.isCloseOnExit()) {
            url += ";DB_CLOSE_ON_EXIT=TRUE";
        }
        
        if (exists(properties.getInitRunScript())) {
            url += ";INT=RUNSCRIPT FROM " + properties.getInitRunScript();
        }
        
        if (properties.getTraceLevelFile() > -1 && properties.getTraceLevelFile() < 4) {
            url += ";TRACE_LEVEL_FILE=" + properties.getTraceLevelFile();
        }
        
        if (properties.isIgnoreUnknownSettings()) {
            url += ";IGNORE_UNKNOWN_SETTINGS=TRUE";
        }
        
        if (exists(properties.getAccessMode())) {
            url += ";ACCESS_MODE_DATA=" + properties.getAccessMode().toUpperCase();
        }
        
        if (properties.isAutoReconnect()) {
            url += ";AUTO_RECONNECT=TRUE";
        }
        
        this.url = url;
    }

    @Override
    public void execute(String sql) throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @Override
    public void executePrepared(String sql, Object... args) throws SQLException {
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public List<Row> executeQuery(String sql) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                rows.add(new SQLRow(resultSet, this));
            }
            return rows;
        }
    }

    @Override
    public List<Row> executePreparedQuery(String sql, Object... args) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    rows.add(new SQLRow(resultSet, this));
                }
            }
            return rows;
        }
    }

    private boolean exists(String value) {
        return value != null && !value.isEmpty();
    }
}
