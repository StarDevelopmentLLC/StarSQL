package com.stardevllc.stardata.sql;

import com.stardevllc.stardata.api.annotations.*;
import com.stardevllc.stardata.api.interfaces.model.ClassModel;
import com.stardevllc.stardata.api.interfaces.model.FieldModel;
import com.stardevllc.stardata.sql.interfaces.Column;
import com.stardevllc.stardata.sql.interfaces.SQLDatabase;
import com.stardevllc.stardata.sql.interfaces.Table;
import com.stardevllc.stardata.sql.annotations.ForeignKey;
import com.stardevllc.stardata.sql.annotations.ForeignKeyStorage;
import com.stardevllc.starlib.observable.ObservableValue;
import com.stardevllc.starlib.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;

public class SQLTable implements Table {
    private String name;
    private final Class<?> modelClass;
    private final Set<Column> columns = new TreeSet<>();
    private SQLDatabase database;
    private Column primaryKeyColumn;
    private int columnOrderIndex = 1000;

    private Set<Class<?>> requiredClasses = new HashSet<>();
    private List<Field> columnFields = new ArrayList<>();
    
    private List<Field> foreignKeyStorageFields = new ArrayList<>();

    /**
     * Constructs a new table based on a Java Class
     *
     * @param database   The Database that this table is registered to.
     * @param modelClass The class to use
     */
    public SQLTable(SQLDatabase database, Class<?> modelClass) throws Exception {
        this.database = database;
        this.modelClass = modelClass;

        try {
            modelClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new Exception("Could not find default constructor for class " + modelClass.getName());
        }

        name = determineTableName(modelClass);

        if (name == null) {
            name = modelClass.getSimpleName().toLowerCase();
        }

        for (Field field : ReflectionHelper.getClassFields(modelClass)) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Ignored.class)) {
                continue;
            }

            if (field.isAnnotationPresent(ForeignKeyStorage.class)) {
                this.foreignKeyStorageFields.add(field);
                continue;
            }
            
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            if (foreignKey != null) {
                this.requiredClasses.add(foreignKey.value());
            }

            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            Class<?> type = field.getType();
            if (ObservableValue.class.isAssignableFrom(type) && !field.isAnnotationPresent(Codec.class)) {
                continue;
            }

            if (Collection.class.isAssignableFrom(type) && !field.isAnnotationPresent(Codec.class)) {
                continue;
            }

            if (Map.class.isAssignableFrom(type) && !field.isAnnotationPresent(Codec.class)) {
                continue;
            }
            
            this.columnFields.add(field);
        }
    }

    @Override
    public FieldModel<SQLDatabase> getPrimaryField() {
        return primaryKeyColumn;
    }

    public Set<Class<?>> getRequiredClasses() {
        return requiredClasses;
    }

    public void setupColumns() throws Exception {
        for (Field field : columnFields) {
            Column column = new SQLColumn(this, field);

            if (column.getType() == null || column.getType().isEmpty()) {
                continue;
            }

            if (column.isPrimaryKey()) {
                if (this.primaryKeyColumn != null) {
                    throw new Exception("Multiple Primary key Columns exist for table " + this.name);
                }
                this.primaryKeyColumn = column;
            }

            this.columns.add(column);
        }

        if (this.foreignKeyStorageFields.isEmpty()) {
            return;
        }

        for (Field field : this.foreignKeyStorageFields) {
            ForeignKeyStorage annotation = field.getAnnotation(ForeignKeyStorage.class);
            ForeignKeyStorageInfo info = new ForeignKeyStorageInfo(field, annotation.clazz(), annotation.field(), annotation.mapKeyField());
            this.primaryKeyColumn.getForeignKeyStorageInfos().add(info);
        }
    }

    @Override
    public Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    @Override
    public int getColumnOrderIndex() {
        return columnOrderIndex;
    }

    @Override
    public void setColumnOrderIndex(int columnOrderIndex) {
        this.columnOrderIndex = columnOrderIndex;
    }

    private static String determineTableName(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }

        Name tableName = clazz.getAnnotation(Name.class);
        if (tableName == null || tableName.value().isEmpty()) {
            return determineTableName(clazz.getSuperclass());
        }

        return tableName.value();
    }

    @Override
    public SQLDatabase getDatabase() {
        return database;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getModelClass() {
        return modelClass;
    }

    @Override
    public Set<FieldModel<SQLDatabase>> getFieldModels() {
        return new HashSet<>(this.columns);
    }

    @Override
    public void setup(SQLDatabase database) {

    }

    @Override
    public Set<Column> getColumns() {
        return new TreeSet<>(columns);
    }

    @Override
    public void addColumn(Column column) {
        this.columns.add(column);
    }

    @Override
    public String generateCreationStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(getName()).append("(");
        getColumns().forEach(column -> {
            sb.append("`").append(column.getName()).append("`").append(" ").append(column.getType());
            if (column.isPrimaryKey()) {
                sb.append(" primary key");
            }

            if (column.isAutoIncrement()) {
                sb.append(" auto_increment");
            }

            if (column.isNotNull()) {
                sb.append(" not null");
            }

            if (column.isUnique()) {
                sb.append(" unique");
            }

            if (column.hasForeignKey()) {
                Table reference = column.getParentForeignKeyTable();
                Column fkColumn = column.getParentForeignKeyColumn();
                FKAction onDelete = column.getForeignKeyOnDeleteAction();
                FKAction onUpdate = column.getForeignKeyOnUpdateAction();
                sb.append(", foreign key (`").append(column.getName()).append("`) references `").append(reference.getName()).append("`(`").append(fkColumn.getName()).append("`)").append(" on delete ").append(onDelete.name().toLowerCase().replace("_", " ")).append(" on update ").append(onUpdate.name().toLowerCase().replace("_", " "));
            }

            sb.append(", ");
        });

        sb.delete(sb.length() - 2, sb.length());
        sb.append(");");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SQLTable table = (SQLTable) o;
        return name.equalsIgnoreCase(table.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(ClassModel<SQLDatabase> o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public Column getColumn(String columnName) {
        for (Column column : this.columns) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        return null;
    }

    @Override
    public Logger getLogger() {
        return database.getLogger();
    }

    @Override
    public Column getColumnByOrder(int order) {
        for (Column column : getColumns()) {
            if (column.getOrder() == order) {
                return column;
            }
        }
        return null;
    }
}