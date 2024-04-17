package com.stardevllc.starsql.annotations;

import java.lang.annotation.*;

/**
 * This sets the field as the Primary Key<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
    
}
