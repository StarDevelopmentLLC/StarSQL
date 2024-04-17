package com.stardevllc.starsql.interfaces;

import com.stardevllc.starsql.model.Column;

/**
 * The functional interface for a deserializer in the {@link TypeHandler} class
 */
@FunctionalInterface
public interface TypeDeserializer {
    /**
     * Takes in an object and the model to return the Java class for it. Please see the database documentation for how this is done
     * @param column The field model that is represented by the value
     * @param object The object to be deserialized
     * @return The deserialized object
     */
    Object deserialize(Column column, Object object);
}
