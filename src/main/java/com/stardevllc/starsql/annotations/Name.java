package com.stardevllc.starsql.annotations;

import java.lang.annotation.*;

/**
 * This annotation allows customization of the name of models and keys. This annotation has the highest priority for naming these things.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Name {
    String value();
}
