package com.stardevllc.starsql.api.annotations;

import java.lang.annotation.*;

/**
 * When annotating a field with this annotation, the library will ignore that field within the table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ignored {
    
}
