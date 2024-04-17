package com.stardevllc.starsql.interfaces;

import com.stardevllc.starsql.model.Column;

/**
 * The functional interface for the serializer in the {@link TypeHandler} class
 */
@FunctionalInterface
public interface TypeSerializer {
    /**
     * Takes in an object and the model to return the database value for it. Please see the database documentation for how this is done
     * @param column The field model that the object is represented by
     * @param object The actual object
     * @return The supported Database object
     */
    Object serialize(Column column, Object object);
}
