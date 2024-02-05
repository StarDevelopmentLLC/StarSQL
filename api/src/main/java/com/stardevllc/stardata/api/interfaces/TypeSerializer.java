package com.stardevllc.stardata.api.interfaces;

import com.stardevllc.stardata.api.interfaces.model.Database;
import com.stardevllc.stardata.api.interfaces.model.FieldModel;

/**
 * The functional interface for the serializer in the {@link TypeHandler} class
 */
@FunctionalInterface
public interface TypeSerializer<T extends Database> {
    /**
     * Takes in an object and the model to return the database value for it. Please see the database documentation for how this is done
     * @param fieldModel The field model that the object is represented by
     * @param object The actual object
     * @return The supported Database object
     */
    Object serialize(FieldModel<T> fieldModel, Object object);
}
