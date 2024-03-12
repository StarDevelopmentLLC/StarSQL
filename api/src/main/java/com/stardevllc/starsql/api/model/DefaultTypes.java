package com.stardevllc.starsql.api.model;

import com.stardevllc.starsql.api.interfaces.DatabaseType;

/**
 * Default Types that the StarData library directly supports
 */
public enum DefaultTypes implements DatabaseType {
    H2, MYSQL, POSTGRES, SQLITE
}
