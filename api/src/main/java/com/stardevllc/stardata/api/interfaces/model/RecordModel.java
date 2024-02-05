package com.stardevllc.stardata.api.interfaces.model;

import com.stardevllc.stardata.api.interfaces.ObjectCodec;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a single record within a database. Please see implementing documentation to see how things are handled.
 */
public interface RecordModel {

    /**
     * @return The database that holds the data contained within this record
     */
    Database getDatabase();

    /**
     * @return The class model that contains the data within this record. 
     */
    ClassModel<?> getClassModel();
    
    /**
     * Gets a parsed object from this record
     * @param key The column name
     * @return The object
     */
    Object getObject(String key);

    /**
     * Gets the value as a string
     * @param key The column name
     * @return The value as a string
     */
    String getString(String key);

    /**
     * Gets the value as an int
     * @param key The column name
     * @return The value as an int
     */
    int getInt(String key);

    /**
     * Gets the value as a long
     * @param key The column name
     * @return The value as a long
     */
    long getLong(String key);

    /**
     * Gets the value as a double
     * @param key The column name
     * @return The value as a double
     */
    double getDouble(String key);

    /**
     * Gets the value as a float
     * @param key The column name
     * @return The value as a float
     */
    float getFloat(String key);

    /**
     * Gets the value as a boolean
     * @param key The column name
     * @return The value as a boolean
     */
    boolean getBoolean(String key);

    /**
     * Gets the value as a UUID
     * @param key The column name
     * @return The value as a UUID
     */
    UUID getUuid(String key);

    /**
     * Gets the value using the codec provided
     * @param key The column name
     * @return The value decoded
     */
    <T> T get(String key, ObjectCodec<T> codec);

    /**
     * @return All the data related to this row that was selected from the query
     */
    Map<String, Object> getData();
}