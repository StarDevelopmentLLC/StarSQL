package com.stardevllc.starsql.converters;

import com.stardevllc.starsql.interfaces.ObjectConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class SQLConverter<T> implements ObjectConverter<T> {
    protected final Class<?> mainClass;
    protected final Set<Class<?>> additionalClasses = new HashSet<>();
    protected final String mysqlType;
    
    public SQLConverter(Class<?> mainClass, String mysqlType) {
        this.mainClass = mainClass;
        this.mysqlType = mysqlType;
    }
    
    @Override
    public void addAdditionalClass(Class<?>... classes) {
        if (classes != null) {
            this.additionalClasses.addAll(List.of(classes));
        }
    }
    
    @Override
    public Class<?> getMainClass() {
        return mainClass;
    }
    
    @Override
    public Set<Class<?>> getAdditionalClasses() {
        return additionalClasses;
    }
    
    @Override
    public String getDataType() {
        return mysqlType;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        if (this.mainClass.equals(clazz)) {
            return true;
        }
    
        for (Class<?> additionalClass : this.additionalClasses) {
            if (additionalClass.equals(clazz)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SQLConverter that = (SQLConverter) o;
        return Objects.equals(mainClass, that.mainClass) && Objects.equals(additionalClasses, that.additionalClasses);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mainClass, additionalClasses);
    }
}
