package com.stardevllc.starsql.api.annotations;

import java.lang.annotation.*;

/**
 * This annotation allows you to tell the library that this field is the id of the table.<br>
 * This will set the Primary Key flag to true as if you used the {@link PrimaryKey} annotation<br>
 * This will also set the auto_increment flag to true as if you used the {@link AutoIncrement} annotation and as long as the field is a supported number type
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ID { 
    
}
