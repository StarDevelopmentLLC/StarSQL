package com.stardevllc.starsql.api.interfaces.model;

import com.stardevllc.starsql.api.annotations.Codec;
import com.stardevllc.starsql.api.annotations.Ignored;
import com.stardevllc.starsql.api.annotations.Order;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents a Database Table.<br>
 * A table generates columns based on Class Fields. This library will take fields from parent classes as well.<br>
 * It is highly recommended to have a primary key field as the library allows you to update existing data for a specific object.<br>
 * You can have fields ignored using one of the following ways: The {@link Ignored} annotation, the {@code modifiers}; {@code static} AND {@code final}, {@code transient}<br>
 * Fields that are sub-classes of {@link Collection} and {@link Map} must be used with the {@link Codec} annotation for custom processing, or using the {@code Foreign Key} system.
 * Fields that have types extended from {@link com.stardevllc.starlib.observable.Observable} will be ignored unless annotated with {@link Codec} with an appropriate codec
 * You must have a field that is an ID column for it to work. You can technically use any object as it though. The two ways you can do it is from the ID annotation or using the PrimaryKey annotation. <br>
 * The ID annotation will automatically set it as the Primary Key and as an AutoIncrement if it an object type that this library supports as such.<br>
 * Note: Fields that are of type {@code long}, {@link Long}, {@code int} or {@link Integer} and named {@code id} are automatically set as the ID field. This is for compatibility with existing projects that I have.
 */
public interface Table extends ClassModel<SQLDatabase> {
    /**
     * @return The column that is the primary key
     */
    Column getPrimaryKeyColumn();

    /**
     * Internal, used for columns that do not have an {@link Order} annotation.
     */
    int getColumnOrderIndex();

    /**
     * Internal, used for columns that do not have an {@link Order} annotation.
     */
    void setColumnOrderIndex(int columnOrderIndex);

    /**
     * @return All of the registered columns
     */
    Set<Column> getColumns();

    /**
     * @param column Adds a column to the table
     */
    void addColumn(Column column);

    /**
     * @return The generated sql creation statement
     */
    String generateCreationStatement();

    /**
     * @param columnName The name of the column. Case-Insensitive
     * @return The registered column, or null if it does not exist.
     */
    Column getColumn(String columnName);
    
    /**
     * @return The passed in Logger instance
     */
    Logger getLogger();

    /**
     * Gets a column by it's order index
     * @param order The index
     * @return The column assigned to that order.
     */
    Column getColumnByOrder(int order);
    
    Set<Class<?>> getRequiredClasses();
    
    void setupColumns() throws Exception;
}
