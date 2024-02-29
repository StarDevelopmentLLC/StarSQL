package com.stardevllc.starsql.api.interfaces;

import com.stardevllc.starsql.api.interfaces.model.Database;
import com.stardevllc.starlib.Value;

import java.util.Set;

/**
 * This interface defines the contract for how Java Types are converted into Database Types and Database Types to Java Types. <br>
 * By default, there will be support for Java Primitives and their wrapper classes, UUID, String and the {@link Value} class from StarLib for each database type. <br
 * TypeHandlers able to be made for multiple database types if desired.
 */
public interface TypeHandler<T extends Database> {
    TypeSerializer<T> getSerializer();

    TypeDeserializer<T> getDeserializer();

    void addAdditionalClass(Class<?>... classes);

    Class<?> getMainClass();

    Set<Class<?>> getAdditionalClasses();

    String getDataType();
    
    Set<DatabaseType> getDatabaseTypes();
    
    default boolean supportsDatabse(DatabaseType databaseType) {
        return getDatabaseTypes().contains(databaseType);
    }

    default boolean matches(Class<?> clazz) {
        if (getMainClass().equals(clazz)) {
            return true;
        }
        
        if (getAdditionalClasses() != null && !getAdditionalClasses().isEmpty()) {
            return getAdditionalClasses().contains(clazz);
        }
        
        return false;
    }
}
