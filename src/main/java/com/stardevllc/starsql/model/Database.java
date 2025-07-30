package com.stardevllc.starsql.model;

import com.stardevllc.starsql.statements.ColumnKey;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public class Database {
    
    private static final Map<Integer, String> TYPES_MAP = new HashMap<>();
    
    static {
        for (Field field : Types.class.getDeclaredFields()) {
            try {
                int value = (int) field.get(null);
                TYPES_MAP.put(value, field.getName().toLowerCase());
            } catch (Exception e) {
            }
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {}
    }
    
    private String name;
    private String url;
    private String username;
    private String password;
    
    private int majorVersion, minorVersion;
    private String productName, productVersion;
    
    private int maxColumnNameLength, maxColumnsInTable;
    private int maxRowSizeInBytes;
    private int maxTableNameLength;
    
    private Map<String, Table> tables = new HashMap<>();
    
    public Database(String name, String url, String username, String password) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public void retrieveDatabaseInformation() {
        try (Connection connection = connect()) {
            DatabaseMetaData databaseMeta = connection.getMetaData();
            this.majorVersion = databaseMeta.getDatabaseMajorVersion();
            this.minorVersion = databaseMeta.getDatabaseMinorVersion();
            this.productName = databaseMeta.getDatabaseProductName();
            this.productVersion = databaseMeta.getDatabaseProductVersion();
            this.maxColumnNameLength = databaseMeta.getMaxColumnNameLength();
            this.maxColumnsInTable = databaseMeta.getMaxColumnsInTable();
            this.maxRowSizeInBytes = databaseMeta.getMaxRowSize();
            this.maxTableNameLength = databaseMeta.getMaxTableNameLength();
            
            Set<String> tableNames = new HashSet<>();
            
            try (ResultSet tableResults = databaseMeta.getTables(this.name, null, null, new String[]{"TABLE"})) {
                while (tableResults.next()) {
                    tableNames.add(tableResults.getString("TABLE_NAME"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            Map<String, Set<String>> uniqueKeys = new HashMap<>();
            
            try (PreparedStatement statement = connection.prepareStatement("SELECT `TABLE_NAME`, `CONSTRAINT_NAME`, `CONSTRAINT_TYPE` FROM `information_schema`.`TABLE_CONSTRAINTS` WHERE `TABLE_SCHEMA`=? AND `CONSTRAINT_TYPE`='UNIQUE';")) {
                statement.setString(1, this.name);
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String table = resultSet.getString("TABLE_NAME");
                        String constraintName = resultSet.getString("CONSTRAINT_NAME"); //This is the name of the column if it is a unique constraint
                        
                        if (uniqueKeys.containsKey(table)) {
                            uniqueKeys.get(table).add(constraintName);
                        } else {
                            uniqueKeys.put(table, new HashSet<>(Set.of(constraintName)));
                        }
                    }
                }
            }
            
            Map<ColumnKey, ColumnKey> foreignKeys = new HashMap<>();
            
            try (PreparedStatement statement = connection.prepareStatement("SELECT `TABLE_NAME`, `COLUMN_NAME`, `REFERENCED_TABLE_NAME`, `REFERENCED_COLUMN_NAME` FROM `information_schema`.`KEY_COLUMN_USAGE` WHERE KEY_COLUMN_USAGE.`CONSTRAINT_SCHEMA`=? AND KEY_COLUMN_USAGE.`REFERENCED_COLUMN_NAME` IS NOT NULL;")) {
                statement.setString(1, this.name);
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String tableName = resultSet.getString("TABLE_NAME");
                        String columName = resultSet.getString("COLUMN_NAME");
                        
                        String referencedTableName = resultSet.getString("REFERENCED_TABLE_NAME");
                        String referencedColumnName = resultSet.getString("REFERENCED_COLUMN_NAME");
                        
                        foreignKeys.put(new ColumnKey(tableName, columName, null), new ColumnKey(referencedTableName, referencedColumnName, null));
                    }
                }
            }
            
            for (String tableName : tableNames) {
                Table table = new Table(this, tableName.toLowerCase());
                
                String primaryKeyColumn = "";
                
                try (ResultSet pkResults = databaseMeta.getPrimaryKeys(null, null, tableName)) {
                    while (pkResults.next()) {
                        primaryKeyColumn = pkResults.getString("COLUMN_NAME");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
                Set<String> uniqueColumns = uniqueKeys.getOrDefault(tableName, new HashSet<>());
                
                try (ResultSet columnResults = databaseMeta.getColumns(null, null, tableName, null)) {
                    while (columnResults.next()) {
                        String name = columnResults.getString("COLUMN_NAME");
                        String type = TYPES_MAP.get(columnResults.getInt("DATA_TYPE"));
                        int size = columnResults.getInt("COLUMN_SIZE");
                        int position = columnResults.getInt("ORDINAL_POSITION");
                        String isNullable = columnResults.getString("IS_NULLABLE");
                        boolean nullable = Objects.equals(isNullable, "YES");
                        String isAutoIncrement = columnResults.getString("IS_AUTOINCREMENT");
                        boolean autoIncrement = Objects.equals(isAutoIncrement, "YES");
                        boolean primaryKey = Objects.equals(primaryKeyColumn, name);
                        boolean unique = primaryKey || uniqueColumns.contains(name);
                        ColumnKey foreignKey = foreignKeys.get(new ColumnKey(this.name, name, null));
                        
                        Column column = new Column(table, name, type, size, position, nullable, autoIncrement, primaryKey, unique, foreignKey);
                        table.addColumn(column);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
                this.tables.put(table.getName().toLowerCase(), table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean execute(String sql) {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            return statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error with statement: " + sql);
        }
        
        return false;
    }
    
    public void executeQuery(String sql, Consumer<ResultSet> consumer) {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            consumer.accept(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error with statement: " + sql);
        }
    }
    
    public int executeUpdate(String sql) {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error with statement: " + sql);
        }
        
        return Integer.MIN_VALUE;
    }
    
    public String getName() {
        return name;
    }
    
    public Table getTable(String name) {
        return this.tables.get(name.toLowerCase());
    }
    
    public Map<String, Table> getTables() {
        return new HashMap<>(tables);
    }
    
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }
    
    public void connect(Consumer<Connection> connectionConsumer) {
        try (Connection connection = connect()) {
            connectionConsumer.accept(connection);
        } catch (SQLException e) {
            System.err.println("Problem with getting connection");
            e.printStackTrace();
        }
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public int getMajorVersion() {
        return majorVersion;
    }
    
    public int getMinorVersion() {
        return minorVersion;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public String getProductVersion() {
        return productVersion;
    }
    
    public int getMaxColumnNameLength() {
        return maxColumnNameLength;
    }
    
    public int getMaxColumnsInTable() {
        return maxColumnsInTable;
    }
    
    public int getMaxRowSizeInBytes() {
        return maxRowSizeInBytes;
    }
    
    public int getMaxTableNameLength() {
        return maxTableNameLength;
    }
}
