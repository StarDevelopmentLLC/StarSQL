package com.stardevllc.starsql.model;

import com.stardevllc.starlib.registry.StringRegistry;
import com.stardevllc.starsql.interfaces.TypeHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DatabaseRegistry extends StringRegistry<Database> {
    protected Logger logger;
    private boolean setup;
    private Set<TypeHandler> typeHandlers = new HashSet<>();
    
    public DatabaseRegistry(Logger logger) {
        this.logger = logger;
    }
    
    public void setup() throws Exception {
        for (Database database : getObjects().values()) {
            database.setup();
        }
        this.setup = true;
    }
    
    public boolean isSetup() {
        return setup;
    }

    public void register(Database object) {
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

    public void registerAll(Collection<Database> databases) {
        databases.forEach(this::register);
    }

    public Set<TypeHandler> getTypeHandlers() {
        return new HashSet<>(typeHandlers);
    }
    
    public void addTypeHandler(TypeHandler handler) {
        this.typeHandlers.add(handler);
    }
}