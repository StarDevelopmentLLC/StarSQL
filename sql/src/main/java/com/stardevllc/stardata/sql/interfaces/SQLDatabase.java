package com.stardevllc.stardata.sql.interfaces;

import com.stardevllc.stardata.api.interfaces.model.Database;
import com.stardevllc.stardata.api.interfaces.TypeHandler;
import com.stardevllc.stardata.sql.JoinType;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * This class represents a SQLDatabase<br>
 * Please be aware, that Databases must already exist, this library will not create databases<br>
 * Normal Database actions happen in their own connection to prevent memory leaks. <br>
 * In order to bulk push data, please use the queue() and flush() methods <br>
 * Please see the documentation for the get() and the save() methods for saving and loading data from the database<br>
 * All actions happen on the same thread they are called on, nothing is async. There is plans to add methods for this.
 */
public interface SQLDatabase extends Database {
    /**
     * @return The name of this database
     */
    String getName();

    /**
     * @return The user of this database
     */
    String getUser();

    /**
     * @return The password of this database
     */
    String getPassword();

    /**
     * @return The URL of the database
     */
    String getUrl();

    /**
     * Executes a given SQL Statement
     *
     * @param sql The SQL string
     * @throws Exception Any SQL Errors
     */
    void execute(String sql) throws Exception;

    /**
     * Executes a given Prepared SQL Statement
     *
     * @param sql  The SQL Statement
     * @param args The arguments for the prepared statement
     * @throws Exception Any SQL Errors
     */
    void executePrepared(String sql, Object... args) throws Exception;

    /**
     * Executes a query on the database
     * It is safe to use the data from the rows whenever as it is cached
     *
     * @param sql The query sql
     * @return The rows from the statement
     * @throws SQLException Any SQL Errors
     */
    List<Row> executeQuery(String sql) throws Exception;

    /**
     * Executs a prepared query on the database
     *
     * @param sql  The SQL statement
     * @param args The arugments for the prepared statement
     * @return The list of rows from executing
     * @throws SQLException Any SQL Errors
     */
    List<Row> executePreparedQuery(String sql, Object... args) throws Exception;

    /**
     * @return The defined type handles. These include the default ones in the StarSQL default type handlers field
     */
    Set<TypeHandler<SQLDatabase>> getTypeHandlers();

    /**
     * Adds a custom type handler specific to this database.
     *
     * @param handler The handler to add
     */
    void addTypeHandler(TypeHandler<SQLDatabase> handler);

    /**
     * @return All registered tables. This is Thread-Safe as it returns a copy of the Set
     */
    Set<Table> getTables();

    /**
     * Gets all tables that are registered under child-classes of the provided class
     *
     * @param clazz The parent class
     * @return All tables registered via child-classes
     */
    Set<Table> getTablesOfSuperType(Class<?> clazz);

    /**
     * Gets a registered table: Case Insensitive
     *
     * @param name The name of the table
     * @return The registered table or null if one does not exist
     */
    Table getTable(String name);

    /**
     * Gets a registered rable
     * Note: This will check against super classes if one is not found by the direct class provided
     *
     * @param clazz The class of the table
     * @return The registered table or null if one does not exist
     */
    Table getTable(Class<?> clazz);

    @Deprecated
    <T> List<T> join(Class<T> joinHolderClass, JoinType joinType, Class<?> leftSide, Class<?> rightSide) throws Exception;
}
