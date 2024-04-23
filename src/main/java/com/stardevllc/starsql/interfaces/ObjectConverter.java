package com.stardevllc.starsql.interfaces;

import com.stardevllc.starsql.model.Column;

import java.util.Set;

public interface ObjectConverter<T> {
    Object serializeToSQL(Column column, Object input);
    
    T deserializeFromSQL(Column column, Object input);

    void addAdditionalClass(Class<?>... classes);

    Class<?> getMainClass();

    Set<Class<?>> getAdditionalClasses();

    String getDataType();
    
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
