package com.stardevllc.starsql.api.interfaces.model;

import com.stardevllc.starsql.api.interfaces.ObjectCodec;
import com.stardevllc.starsql.api.interfaces.TypeHandler;

/**
 * This class represents a Row or a Record in a Table<br>
 * This is mainly to allow closing the connection to the database as quick as possible as it stores the data in a HashMap based on the column<br>
 * The values from each database cell are parsed using {@link TypeHandler}'s for the field/class and any defined {@link ObjectCodec}'s for the columns<br>
 * This means that you can case the value from the {@code getObject} method to what you know the type to be.<br>
 * You can also use the other methods to do this for you, they will attempt to parse the values as well depending on the type of value<br>
 * Please see each individual method for more information
 */
public interface Row extends RecordModel {
    /**
     * @return The table related to this row.
     */
    Table getTable();
}
