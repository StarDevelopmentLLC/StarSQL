package com.stardevllc.starsql.model;

import com.stardevllc.starsql.annotations.*;
import com.stardevllc.starsql.interfaces.ObjectCodec;
import com.stardevllc.starsql.interfaces.ObjectConverter;
import com.stardevllc.starsql.statements.SqlColumnKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class Column implements Comparable<Column> {
    private final Table table;
    private final Field field;
    private final Class<?> fieldClass;

    private final String name;
    private String type;
    private final boolean primaryKey, autoIncrement, notNull, unique;
    private final int order;
    private ObjectCodec<?> codec;
    private ObjectConverter typeHandler;
    
    private List<ForeignKeyStorageInfo> foreignKeyStorageInfos = new ArrayList<>();
    
    private Table parentForeignKeyTable; //This is the table that this column references
    private Column parentForeignKeyColumn; 
    
    private FKAction fkOnDelete, fkOnUpdate;
    
    /**
     * Constructs a column based on a class and the Field
     *
     * @param table The class
     * @param field The field
     */
    public Column(Table table, Field field) {
        this.table = table;
        this.field = field;
        this.fieldClass = table.getModelClass();

        Class<?> fieldType = field.getType();
        Database database = table.getDatabase();
        if (field.isAnnotationPresent(Codec.class)) {
            try {
                codec = field.getAnnotation(Codec.class).value().getConstructor().newInstance();
                type = "varchar(1000)";
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
        if (field.isAnnotationPresent(Type.class)) {
            String typeValue = field.getAnnotation(Type.class).value();
            if (this.codec != null) {
                if (!typeValue.toLowerCase().startsWith("varchar")) {
                    throw new IllegalArgumentException("Field " + field.getName() + " in class " + table.getModelClass().getName() + " is annotated with the ColumnType annotation and specifies a non-varchar value. This is not allowed by this library.");
                }
            }

            type = typeValue;
        }
        
        if (typeHandler == null && codec == null) {
            for (ObjectConverter typeHandler : this.table.getDatabase().getTypeHandlers()) {
                if (typeHandler.matches(this.field.getType())) {
                    this.typeHandler = typeHandler;
                    break;
                }
            }
            
            ForeignKey foreignKeyData = field.getAnnotation(ForeignKey.class);
            if (foreignKeyData != null) {
                this.parentForeignKeyTable = database.getTable(foreignKeyData.value());
                if (this.parentForeignKeyTable != null) {
                    this.parentForeignKeyColumn = this.parentForeignKeyTable.getPrimaryKeyColumn();
                }
                
                if (this.typeHandler == null) {
                    this.typeHandler = this.parentForeignKeyColumn.getTypeHandler();
                }

                FKOnDelete onDeleteAnnotation = field.getAnnotation(FKOnDelete.class);
                if (onDeleteAnnotation != null) {
                    this.fkOnDelete = onDeleteAnnotation.value();
                } else {
                    this.fkOnDelete = foreignKeyData.onDelete();
                }

                FKOnUpdate onUpdateAnnotation = field.getAnnotation(FKOnUpdate.class);
                if (onUpdateAnnotation != null) {
                    this.fkOnUpdate = onUpdateAnnotation.value();
                } else {
                    this.fkOnUpdate = foreignKeyData.onUpdate();
                }
            } 

            if (this.typeHandler != null) {
                if (this.type == null || this.type.isEmpty()) {
                    type = this.typeHandler.getDataType();
                }
            } else {
                throw new IllegalArgumentException("Could not determine a handler or a codec for the field type " + field.getType().getName() + " in class " + table.getModelClass().getName());
            }
        }

        if (field.isAnnotationPresent(Name.class)) {
            name = field.getAnnotation(Name.class).value();
        } else {
            name = field.getName().toLowerCase();
        }

        boolean invalidIDType = List.of(String.class, char.class, Character.class, boolean.class, Boolean.class, UUID.class, Enum.class, float.class, Float.class, double.class, Double.class).contains(fieldType);
        boolean declaredID = field.isAnnotationPresent(ID.class) && !invalidIDType;
        
        boolean fieldIsLong = field.getType().equals(long.class) || field.getType().equals(Long.class);
        boolean fieldIsInt = field.getType().equals(int.class) || field.getType().equals(Integer.class);
        boolean fieldIsCorrectType = fieldIsInt || fieldIsLong;
        boolean fieldIsId = field.getName().equalsIgnoreCase("id");
        boolean fieldIsSoftPrimary = fieldIsCorrectType && fieldIsId;
        
        if (declaredID || fieldIsSoftPrimary) {
            autoIncrement = true;
            primaryKey = true;
            unique = true;
        } else {
            primaryKey = field.isAnnotationPresent(PrimaryKey.class);
            autoIncrement = field.isAnnotationPresent(AutoIncrement.class);
            unique = field.isAnnotationPresent(Unique.class);
        }

        notNull = field.isAnnotationPresent(DBNotNull.class);

        if (field.isAnnotationPresent(Order.class)) {
            Order order = field.getAnnotation(Order.class);
            Column otherOrderColumn = table.getColumnByOrder(order.value());
            if (otherOrderColumn != null) {
                table.getLogger().warning("Column " + name + " has a duplicate order value with " + otherOrderColumn.getName() + ". Using normal order increment for unordered columns.");
                this.order = table.getColumnOrderIndex();
                table.setColumnOrderIndex(this.order + 1);
            } else {
                this.order = order.value();
            }
        } else {
            this.order = table.getColumnOrderIndex();
            table.setColumnOrderIndex(this.order + 1);
        }
    }

    public SqlColumnKey toKey() {
        return toKey(null);
    }

    public SqlColumnKey toKey(String alias) {
        return new SqlColumnKey(this.table.getName(), this.name, alias);
    }

    public Class<?> getFieldClass() {
        return this.fieldClass;
    }

    public FKAction getForeignKeyOnDeleteAction() {
        return this.fkOnDelete;
    }

    public FKAction getForeignKeyOnUpdateAction() {
        return this.fkOnUpdate;
    }

    public List<ForeignKeyStorageInfo> getForeignKeyStorageInfos() {
        return foreignKeyStorageInfos;
    }

    public Table getParentForeignKeyTable() {
        return parentForeignKeyTable;
    }

    public Column getParentForeignKeyColumn() {
        return parentForeignKeyColumn;
    }

    public boolean hasForeignKey() {
        return this.parentForeignKeyTable != null && this.parentForeignKeyColumn != null;
    }

    public Logger getLogger() {
        return table.getLogger();
    }

    public int getOrder() {
        return order;
    }

    public ObjectConverter getTypeHandler() {
        return typeHandler;
    }

    public Table getTable() {
        return table;
    }

    public Field getField() {
        return field;
    }

    public Object getFieldValue(Object holder) throws Exception {
        return field.get(holder);
    }

    public String getName() {
        return name;
    }

    public Table getParent() {
        return null;
    }

    public String getType() {
        return type;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public ObjectCodec<?> getCodec() {
        return codec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Column column = (Column) o;
        return Objects.equals(name, column.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Column other) {
        return Integer.compare(this.order, other.getOrder());
    }
}