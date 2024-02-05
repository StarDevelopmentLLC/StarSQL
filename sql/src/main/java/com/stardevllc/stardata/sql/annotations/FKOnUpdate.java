package com.stardevllc.stardata.sql.annotations;

import com.stardevllc.stardata.sql.FKAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows you to customize the behavior of foreign keys when an entry is updated.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FKOnUpdate {
    FKAction value();
}