package com.stardevllc.starsql.api.annotations;

import java.lang.annotation.*;

/**
 * This sets the field to be an auto-increment field<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoIncrement {
    
}
