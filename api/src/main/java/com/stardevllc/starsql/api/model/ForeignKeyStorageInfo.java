package com.stardevllc.starsql.api.model;

import java.lang.reflect.Field;

/**
 * Internal class, ignore
 */
public class ForeignKeyStorageInfo {
    private Field field;
    private Class<?> childModelClass; //This is the class that maps to the table with the foreign key.
    private String foreignKeyField; //This is the field in the child table's model class that holds to the foreign key link to the parent table.
    private String mapKeyField; //This is the field in the child model class to use as the Map key. ONLY USE WITH MAPS

    public ForeignKeyStorageInfo(Field field, Class<?> childModelClass, String foreignKeyField, String mapKeyField) {
        this.field = field;
        this.childModelClass = childModelClass;
        this.foreignKeyField = foreignKeyField;
        this.mapKeyField = mapKeyField;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getChildModelClass() {
        return childModelClass;
    }

    public String getForeignKeyField() {
        return foreignKeyField;
    }

    public String getMapKeyField() {
        return mapKeyField;
    }

    @Override
    public String toString() {
        return "ForeignKeyStorageInfo{" +
                "field=" + field.getName() +
                ", childModelClass=" + childModelClass +
                ", foreignKeyField='" + foreignKeyField + '\'' +
                ", mapKeyField='" + mapKeyField + '\'' +
                '}';
    }
}