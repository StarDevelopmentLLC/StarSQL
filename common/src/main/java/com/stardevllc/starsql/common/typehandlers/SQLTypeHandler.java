package com.stardevllc.starsql.common.typehandlers;

import com.stardevllc.starsql.api.interfaces.DatabaseType;
import com.stardevllc.starsql.api.interfaces.TypeDeserializer;
import com.stardevllc.starsql.api.interfaces.TypeHandler;
import com.stardevllc.starsql.api.interfaces.TypeSerializer;
import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.api.model.DefaultTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SQLTypeHandler implements TypeHandler<SQLDatabase> {
    protected final Class<?> mainClass;
    protected final Set<Class<?>> additionalClasses = new HashSet<>();
    protected final String mysqlType;
    
    protected final TypeSerializer<SQLDatabase> serializer;
    protected final TypeDeserializer<SQLDatabase> deserializer;
    
    public SQLTypeHandler(Class<?> mainClass, String mysqlType, TypeSerializer<SQLDatabase> serializer, TypeDeserializer<SQLDatabase> deserializer) {
        this.mainClass = mainClass;
        this.mysqlType = mysqlType;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }
    
    @Override
    public TypeSerializer<SQLDatabase> getSerializer() {
        return serializer;
    }
    
    @Override
    public TypeDeserializer<SQLDatabase> getDeserializer() {
        return deserializer;
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
    public Set<DatabaseType> getDatabaseTypes() {
        return Set.of(DefaultTypes.SQL);
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
        SQLTypeHandler that = (SQLTypeHandler) o;
        return Objects.equals(mainClass, that.mainClass) && Objects.equals(additionalClasses, that.additionalClasses);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mainClass, additionalClasses);
    }
}
