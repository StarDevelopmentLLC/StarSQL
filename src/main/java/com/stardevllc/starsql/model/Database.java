package com.stardevllc.starsql.model;

import com.stardevllc.helper.ReflectionHelper;
import com.stardevllc.starsql.StarSQL;
import com.stardevllc.starsql.interfaces.ObjectConverter;
import com.stardevllc.starsql.statements.JoinType;
import com.stardevllc.starsql.statements.SqlSelect;
import com.stardevllc.starsql.statements.WhereClause;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class Database {
    protected Logger logger;
    protected String url, name, user, password;
    protected Set<Table> tables = new LinkedHashSet<>();
    protected Set<ObjectConverter> objectConverters = new HashSet<>();
    protected DatabaseRegistry registry;

    protected final LinkedList<Object> queue = new LinkedList<>();

    protected boolean setup;

    public Database(Logger logger, DBProperties properties) {
        this.logger = logger;
        this.name = properties.getDatabaseName();
        this.user = properties.getUsername();
        this.password = properties.getPassword();

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

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public void registerClass(Class<?> clazz) {
        try {
            this.tables.add(new Table(this, clazz));
            if (this.isSetup()) {
                try {
                    Table table = new Table(this, clazz);
                    execute(table.generateCreationStatement());
                    this.tables.add(table);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Table> getTables() {
        return new LinkedHashSet<>(tables);
    }

    public Set<Table> getTablesOfSuperType(Class<?> clazz) {
        Set<Table> tables = getTables();
        tables.removeIf(table -> !clazz.isAssignableFrom(table.getModelClass()));
        return tables;
    }

    public Logger getLogger() {
        return logger;
    }

    protected <T> T createClassInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            return null;
        }
    }

    protected <T> T parseObjectFromRow(Class<T> clazz, Row row) throws Exception {
        Table table = getTable(clazz);
        T object = createClassInstance(clazz);

        if (object == null) {
            return null;
        }

        for (String key : row.getData().keySet()) {
            Column column = table.getColumn(key);
            if (column == null) {
                continue;
            }

            try {
                Object data = row.getObject(key);

                if (data == null) {
                    continue;
                }

                if (column.hasForeignKey()) {
                    if (column.getField().getType() == column.getParentForeignKeyTable().getModelClass()) {
                        data = get(column.getParentForeignKeyTable().getModelClass(), column.getParentForeignKeyColumn().getName(), data).get(0);
                    }
                }

                column.getField().setAccessible(true);
                column.getField().set(object, data);
            } catch (Exception e) {
                logger.severe("Error while retrieving data from database: " + e.getMessage());
            }
        }

        List<ForeignKeyStorageInfo> infos = row.getTable().getPrimaryKeyColumn().getForeignKeyStorageInfos();
        for (ForeignKeyStorageInfo info : infos) {
            List<?> foreignKeyObjects = get(info.getChildModelClass(), info.getForeignKeyField(), row.getObject(row.getTable().getPrimaryKeyColumn().getName()));
            if (Collection.class.isAssignableFrom(info.getField().getType())) {
                Collection<Object> collection = (Collection<Object>) info.getField().get(object);
                collection.addAll(foreignKeyObjects);
            } else if (Map.class.isAssignableFrom(info.getField().getType())) {
                Map<Object, Object> map = (Map<Object, Object>) info.getField().get(object);
                Field keyField = ReflectionHelper.getClassField(info.getChildModelClass(), info.getMapKeyField());
                for (Object fko : foreignKeyObjects) {
                    Object key = keyField.get(fko);
                    map.put(key, fko);
                }
            } else {
                info.getField().set(object, foreignKeyObjects.get(0));
            }
        }

        return object;
    }
    
    public <T> List<T> join(Class<T> joinHolderClass, JoinType joinType, Class<?> primary, Class<?> rightSide) throws Exception {
        Table leftSideTable = getTable(primary);
        Table rightSideTable = getTable(rightSide);

        List<T> results = new ArrayList<>();
        
        if (leftSideTable == null || rightSideTable == null) {
            return results;
        }

        Constructor<T> constructor = joinHolderClass.getDeclaredConstructor();
        constructor.newInstance();
        
        String sql = "SELECT `%HOLDER%`.`%HOLDERPRIMARY%` as `%RENAMEDHOLDERPRIMARY%`, %HOLDERCOLUMNS%, `%REFERENCE%`.`%REFERENCEPRIMARY%` as `%RENAMEDREFERENCEPRIMARY%`, %REFERENCECOLUMNS% FROM `%HOLDER%` %JTYPE% JOIN `%REFERENCE%` ON `%HOLDER%`.`%HOLDERKEYCOLUMN%` = `%REFERENCE%`.`%REFERENCEPRIMARY%`;";
        sql = sql.replaceAll("%HOLDER%", leftSideTable.getName());
        sql = sql.replaceAll("%HOLDERPRIMARY%", leftSideTable.getPrimaryKeyColumn().getName());
        sql = sql.replaceAll("%RENAMEDHOLDERPRIMARY%", primary.getSimpleName().toLowerCase() + "id");
        sql = sql.replaceAll("%REFERENCE%", rightSideTable.getName());
        sql = sql.replaceAll("%REFERENCEPRIMARY%", rightSideTable.getPrimaryKeyColumn().getName());
        sql = sql.replaceAll("%RENAMEDREFERENCEPRIMARY%", rightSide.getSimpleName().toLowerCase() + "id");
        sql = sql.replaceAll("%JTYPE%", joinType.name());
        
        StringBuilder holderColumns = new StringBuilder();
        for (Column column : leftSideTable.getColumns()) {
            if (column.isPrimaryKey()) {
                continue;
            }
            
            if (column.hasForeignKey()) {
                Table parentKeyTable = column.getParentForeignKeyTable();
                if (parentKeyTable.equals(rightSideTable)) {
                    sql = sql.replaceAll("%HOLDERKEYCOLUMN%", column.getName());
                }
                continue;
            }

            holderColumns.append("`").append(leftSideTable.getName()).append("`.`").append(column.getName()).append("`").append(",");
        }
        holderColumns.deleteCharAt(holderColumns.length() - 1);
        
        StringBuilder referenceColums = new StringBuilder();
        for (Column column : rightSideTable.getColumns()) {
            if (column.isPrimaryKey()) {
                continue;
            }
            referenceColums.append("`").append(rightSideTable.getName()).append("`.`").append(column.getName()).append("`").append(",");
        }
        referenceColums.deleteCharAt(referenceColums.length() - 1);
        
        sql = sql.replaceAll("%HOLDERCOLUMNS%", holderColumns.toString());
        sql = sql.replaceAll("%REFERENCECOLUMNS%", referenceColums.toString());

        List<Row> rows = executeQuery(sql);
        
        //TODO Add a class for join holders for registration to allow annotation support. 
        Set<Field> classFields = ReflectionHelper.getClassFields(joinHolderClass);
        for (Row row : rows) {
            T holder = constructor.newInstance();
            for (Field field : classFields) {
                Object object = row.getObject(field.getName().toLowerCase());
                if (object == null || object.toString().isEmpty() || object.toString().equals("null")) {
                    continue;
                }
                
                field.setAccessible(true);
                field.set(holder, object);
            }
            
            results.add(holder);
        }

        return results;
    }

    public <T> List<T> get(Class<T> clazz, WhereClause whereClause) throws Exception {
        Table table = getTable(clazz);
        if (table == null) {
            return new ArrayList<>();
        }

        SqlSelect select = new SqlSelect(table, true);

        select.whereClause(whereClause);

        List<Row> rows = executeQuery(select.build());

        List<T> objects = new ArrayList<>();
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }

        return objects;
    }

    @Deprecated
    public <T> List<T> get(Class<T> clazz, String[] columns, Object[] values) throws Exception {
        if (columns == null || values == null) {
            throw new IllegalArgumentException("Columns or Values are null");
        }

        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns has a size of " + columns.length + " and values has a size of " + values.length + ". They must be equal.");
        }

        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }
        
        SqlSelect select = new SqlSelect(table, true);
        
        select.where(whereClause -> {
            int size = columns.length;
            whereClause.columns(columns);
            whereClause.values(values);
            String[] operators = new String[size];
            Arrays.fill(operators, "=");
            whereClause.conditions(operators);
        });

        List<Row> rows = executeQuery(select.build());

        List<T> objects = new ArrayList<>();
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }

        return objects;
    }

    public <T> List<T> get(Class<T> clazz, String columnName, Object value) throws Exception {
        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }
        
        SqlSelect select = new SqlSelect(table, true);

        Column column = table.getColumn(columnName);
        if (column == null) {
            return new ArrayList<>();
        }
        
        select.addColumn(column);
        select.addWhereCondition(column.getName(), "=", value);

        List<Row> rows = executeQuery(select.build());

        List<T> objects = new ArrayList<>();
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }
        return objects;
    }

    public <T> List<T> get(Class<T> clazz) throws Exception {
        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }

        SqlSelect select = new SqlSelect(table, true);
        List<Row> rows = executeQuery(select.build());
        List<T> objects = new ArrayList<>();
        
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }

        return objects;
    }

    protected PushInfo generateObjectPushInfo(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        Column primaryColumn = table.getPrimaryKeyColumn();
        Object primaryKeyValue = null;
        boolean getGeneratedKeys = primaryColumn.isAutoIncrement();
        for (Column column : table.getColumns()) {
            Field field = column.getField();
            try {
                field.setAccessible(true);
                Object value = field.get(object);

                if (column.getCodec() != null) {
                    value = column.getCodec().encode(value);
                }

                if (column.getTypeHandler() != null) {
                    value = column.getTypeHandler().serializeToSQL(column, value);
                }

                if (column.hasForeignKey()) {
                    Class<?> type = column.getField().getType();
                    if (column.getTypeHandler().matches(type)) {
                        value = column.getField().get(object);
                        if (value instanceof Number number) {
                            if (number.longValue() == 0) {
                                value = null;
                            }
                        }
                    } else {
                        Object fieldValue = column.getField().get(object);
                        save(fieldValue);
                        value = column.getParentForeignKeyColumn().getField().get(fieldValue);
                    }
                }

                if (value instanceof String str) {
                    str = str.replace("\\", "\\\\");
                    str = str.replace("'", "\\'");
                    value = str;
                }

                if (column.isAutoIncrement()) {
                    getGeneratedKeys = true;
                }

                if (column.isPrimaryKey()) {
                    primaryKeyValue = value;
                    continue;
                }

                data.put(column.getName(), value);
            } catch (IllegalAccessException e) {
            }
        }

        Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
        StringBuilder insertColumnBuilder = new StringBuilder(), insertValueBuilder = new StringBuilder(), updateBuilder = new StringBuilder();
        if (!primaryColumn.isAutoIncrement()) {
            insertColumnBuilder.append("`").append(primaryColumn.getName()).append("`").append(", ");
            insertValueBuilder.append("'").append(primaryKeyValue).append("'").append(", ");
        } else {
            Number number = (Number) primaryKeyValue;
            if (number.longValue() != 0) {
                insertColumnBuilder.append("`").append(primaryColumn.getName()).append("`").append(", ");
                insertValueBuilder.append("'").append(primaryKeyValue).append("'").append(", ");
            }
        }
        while (iterator.hasNext()) {
            Entry<String, Object> entry = iterator.next();
            insertColumnBuilder.append("`").append(entry.getKey()).append("`");
            updateBuilder.append("`").append(entry.getKey()).append("`").append("=");
            if (entry.getValue() != null) {
                insertValueBuilder.append("'").append(entry.getValue()).append("'");
                updateBuilder.append("'").append(entry.getValue()).append("'");
            } else {
                insertValueBuilder.append("null");
                updateBuilder.append("null");
            }

            if (iterator.hasNext()) {
                insertColumnBuilder.append(", ");
                insertValueBuilder.append(", ");
                updateBuilder.append(", ");
            }
        }

        String sql = "insert into " + table.getName() + " (" + insertColumnBuilder + ") values (" + insertValueBuilder + ") on duplicate key update " + updateBuilder + ";";
        return new PushInfo(sql, getGeneratedKeys, table);
    }

    public void saveSilent(Object object) {
        try {
            save(object);
        } catch (Exception e) {
        }
    }

    public void save(Object object) throws Exception {
        PushInfo pushInfo = generateObjectPushInfo(object);
        boolean getGeneratedKeys = pushInfo.generateKeys();
        String sql = pushInfo.sql();
        Table table = pushInfo.table();

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            if (getGeneratedKeys) {
                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.next();
                for (Column column : table.getColumns()) {
                    if (column.isAutoIncrement()) {
                        updateAutoIncrement(column, object, generatedKeys);
                        break;
                    }
                }
            } else {
                statement.executeUpdate(sql);
            }

            Column primaryColumn = table.getPrimaryKeyColumn();
            Object primaryKeyObject = primaryColumn.getField().get(object);
            for (ForeignKeyStorageInfo info : primaryColumn.getForeignKeyStorageInfos()) {
                if (Collection.class.isAssignableFrom(info.getField().getType())) {
                    Collection<?> collection = (Collection<?>) info.getField().get(object);
                    for (Object storageObject : collection) {
                        updateAndSaveFKStorageObject(info, storageObject, primaryKeyObject);
                    }
                } else if (Map.class.isAssignableFrom(info.getField().getType())) {
                    Map<?, ?> map = (Map<?, ?>) info.getField().get(object);
                    for (Object storageObject : map.values()) {
                        updateAndSaveFKStorageObject(info, storageObject, primaryKeyObject);
                    }
                } else {
                    Object storageObject = info.getField().get(object);
                    updateAndSaveFKStorageObject(info, storageObject, primaryKeyObject);
                }
            }
        } catch (SQLException e) {
            System.out.println(sql);
            throw e;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateAndSaveFKStorageObject(ForeignKeyStorageInfo info, Object storageObject, Object primaryKeyObject) throws Exception {
        Field foreignKeyField = ReflectionHelper.getClassField(info.getChildModelClass(), info.getForeignKeyField());
        foreignKeyField.setAccessible(true);
        foreignKeyField.set(storageObject, primaryKeyObject);
        save(storageObject);
    }

    private void updateAutoIncrement(Column column, Object object, ResultSet generatedKeys) throws SQLException, IllegalAccessException {
        Number number = (Number) column.getField().get(object);
        if (column.getType().equalsIgnoreCase("int")) {
            if (number.intValue() == 0) {
                column.getField().set(object, generatedKeys.getInt(1));
            }
        } else if (column.getType().equalsIgnoreCase("bigint")) {
            if (number.longValue() == 0) {
                column.getField().set(object, generatedKeys.getLong(1));
            }
        }
    }

    public void deleteSilent(Class<?> clazz, Object id) {
        try {
            delete(clazz, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSilent(Object object) {
        try {
            delete(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object object) throws SQLException {
        Table table = getTable(object.getClass());
        if (table == null) {
            throw new IllegalArgumentException("No table registered for class " + object.getClass());
        }

        Column primaryColumn = null;
        for (Column column : table.getColumns()) {
            if (column.isPrimaryKey()) {
                primaryColumn = column;
            }
        }

        try {
            Object id = primaryColumn.getField().get(object);
            delete(object.getClass(), id);
            primaryColumn.getField().set(object, 0);
        } catch (IllegalAccessException e) {
        }
    }

    public void delete(Class<?> clazz, Object id) throws SQLException {
        Table table = getTable(clazz);
        if (table == null) {
            return;
        }

        Column primaryColumn = null;
        for (Column column : table.getColumns()) {
            if (column.isPrimaryKey()) {
                primaryColumn = column;
            }
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("delete from " + table.getName() + " where " + primaryColumn.getName() + "='" + id + "';");
        }
    }

    public Table getTable(String name) {
        for (Table table : new ArrayList<>(this.tables)) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    public Table getTable(Class<?> clazz) {
        for (Table table : new ArrayList<>(this.tables)) {
            if (table.getModelClass().equals(clazz)) {
                return table;
            }
        }

        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            return getTable(clazz.getSuperclass());
        }

        return null;
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    protected Row parseRow(String sql) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return new Row(rs, this);
        }
    }

    protected boolean parsePreparedParmeters(PreparedStatement statement, Object... args) {
        if (args == null || args.length == 0) {
            return false;
        }

        try {
            if (statement.getParameterMetaData().getParameterCount() != args.length) {
                return false;
            }

            for (int i = 0; i < args.length; i++) {
                statement.setObject(i, args[i]); //Hopefully this will work, otherwise more things will be needed to make it work
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected Row parsePreparedRow(String sql, Object... args) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                ResultSet rs = statement.executeQuery();
                return new Row(rs, this);
            }
        }

        return null;
    }

    public void execute(String sql) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public void executePrepared(String sql, Object... args) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                statement.executeUpdate();
            }
        }
    }

    public List<Row> executeQuery(String sql) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                rows.add(new Row(resultSet, this));
            }
            return rows;
        }
    }

    public List<Row> executePreparedQuery(String sql, Object... args) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    rows.add(new Row(resultSet, this));
                }
            }
            return rows;
        }
    }

    public void queue(Object object) {
        this.queue.add(object);
    }

    public void flush() {
        if (!this.queue.isEmpty()) {
            try (Connection connection = getConnection()) {
                for (Object object : this.queue) {
                    PushInfo pushInfo = generateObjectPushInfo(object);
                    Statement statement = connection.createStatement();
                    if (pushInfo.generateKeys()) {
                        statement.executeUpdate(pushInfo.sql(), Statement.RETURN_GENERATED_KEYS);
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        generatedKeys.next();
                        for (Column column : pushInfo.table().getColumns()) {
                            if (column.isAutoIncrement()) {
                                updateAutoIncrement(column, object, generatedKeys);
                            }
                        }
                    } else {
                        statement.executeUpdate(pushInfo.sql());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Database database = (Database) o;
        return Objects.equals(name, database.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Set<ObjectConverter> getObjectConverters() {
        Set<ObjectConverter> typeHandlers = new HashSet<>(this.objectConverters);
        typeHandlers.addAll(StarSQL.DEFAULT_TYPE_HANDLERS);
        if (registry != null) {
            typeHandlers.addAll(registry.getObjectConverters());
        }
        return typeHandlers;
    }

    public void addObjectConverter(ObjectConverter handler) {
        this.objectConverters.add(handler);
    }

    public void setRegistry(DatabaseRegistry registry) {
        this.registry = registry;
    }

    public DatabaseRegistry getRegistry() {
        return registry;
    }

    public void setup() throws Exception {
        if (this.setup) {
            return;
        }
        
        for (Table table : this.tables) {
            table.setupColumns();
            this.tables.add(table);
        }

        for (Table table : getTables()) {
            execute(table.generateCreationStatement());
        }

        this.setup = true;
    }

    public boolean isSetup() {
        return setup;
    }
}
