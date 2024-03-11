package com.stardevllc.starsql.common;

import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.api.interfaces.model.Table;
import com.stardevllc.starsql.api.interfaces.TypeHandler;
import com.stardevllc.starsql.api.model.DatabaseRegistry;
import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class is how {@link SQLDatabase}'s are managed and initialized within this library<br>
 * You can create databases using the Database Constructors or by using the factory style methods in this class.<br>
 * If you create databases through the constructors, you still have to register them here using the register(Database) methods<br>
 * Databases must already exist in the MySQL Database. I am working on reworking this to allow creating them in the library<br>
 * It does not matter if you call the {@code setup()} method before or after you register all databases. The setup method processes them all at once, whereas if you register a database after calling the setup method, it is handled on each registration<br>
 * This is also the first class where you can customize the {@link SQLTypeHandler}'s. All TypeHandlers registered to this class will be shared across all databases registered here as well. These MUST be registered before calling the setup method or before the database you want to use it is registered.
 * @see AbstractSQLDatabase
 */
public class SQLDatabaseRegistry extends DatabaseRegistry<SQLDatabase> {
    
    private boolean setup;
    
    private Set<TypeHandler<SQLDatabase>> typeHandlers = new HashSet<>();
    
    public SQLDatabaseRegistry(Logger logger) {
        super(logger);
    }
    
    /**
     * This is the main setup method. Call this method when you want the library to generate the tables
     *
     * @throws SQLException The passed exception if one occurs
     */
    @Override
    public void setup() throws Exception {
        for (SQLDatabase database : getObjects().values()) {
            database.setup();
        }
        this.setup = true;
    }
    
    /**
     * @return If the setup() method has been called and no exceptions happened
     */
    @Override
    public boolean isSetup() {
        return setup;
    }
    
    /**
     * Registers a Database. <br>
     * If the setup flag is true, this will generate the tables from the database being registered. <br>
     * This does not pass any of the exceptions that can happen
     * @param object The object to register
     */
    @Override
    public void register(SQLDatabase object) {
        super.register(object.getName(), object);
        object.setRegistry(this);
        if (this.setup) {
            try {
                object.setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Registers multiple databases.
     * If the setup flag is true, this will generate the tables from the databases being registered.
     * This does not pass any of the exceptions that can happen
     *
     * @param objects The databases to register
     */
    @Override
    public void registerAll(Collection<SQLDatabase> objects) {
        for (SQLDatabase database : objects) {
            register(database);
            for (Table table : database.getTables()) {
                try {
                    database.execute(table.generateCreationStatement());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @return All type handles associated with this DatabaseRegistry
     */
    public Set<TypeHandler<SQLDatabase>> getTypeHandlers() {
        return new HashSet<>(typeHandlers);
    }
    
    /**
     * Adds a TypeHandler for all databases in this Registry
     * @param handler The TypeHandler to register
     */
    public void addTypeHandler(TypeHandler<SQLDatabase> handler) {
        this.typeHandlers.add(handler);
    }
}
