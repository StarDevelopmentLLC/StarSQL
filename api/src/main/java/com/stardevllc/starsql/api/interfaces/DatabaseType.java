package com.stardevllc.starsql.api.interfaces;

import com.stardevllc.starsql.api.model.DefaultTypes;

/**
 * An interface to allow supporting different database types. <br>
 * This is used in the API based interfaces, classes and annotations to provide a level of abstraction. <br>
 * It is recommended to use Enums to represent supported database types. <br>
 * Please see {@link DefaultTypes} for the default supported database types. 
 */
@FunctionalInterface
public interface DatabaseType {
    String name();
    boolean equals(Object other);
}
