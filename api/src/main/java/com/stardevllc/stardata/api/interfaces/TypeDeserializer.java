package com.stardevllc.stardata.api.interfaces;

import com.stardevllc.stardata.api.interfaces.model.Database;
import com.stardevllc.stardata.api.interfaces.model.FieldModel;

/**
 * The functional interface for a deserializer in the {@link TypeHandler} class
 */
@FunctionalInterface
public interface TypeDeserializer<T extends Database> {
    /**
     * Takes in an object and the model to return the Java class for it. Please see the database documentation for how this is done
     * @param fieldModel The field model that is represented by the value
     * @param object The object to be deserialized
     * @return The deserialized object
     */
    Object deserialize(FieldModel<T> fieldModel, Object object);
}
