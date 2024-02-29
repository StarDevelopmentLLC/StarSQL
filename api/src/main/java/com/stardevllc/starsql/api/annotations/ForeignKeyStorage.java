package com.stardevllc.starsql.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This tells the library to use a field as storage. The field should be the same type as the foreign key link <br>
 * You can annotate direct fields, or fields that are a Map and/or a Collection to store things.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKeyStorage {
    /**
     * This is the class that maps to the table with the foreign key.
     */
    Class<?> clazz();

    /**
     * This is the field in the child table's model class that holds to the foreign key link to the parent table.
     */
    String field();

    /**
     * This is the field in the child model class to use as the Map key. ONLY USE WITH MAPS
     */
    String mapKeyField() default "";
}