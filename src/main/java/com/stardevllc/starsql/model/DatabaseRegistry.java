package com.stardevllc.starsql.model;

import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starsql.interfaces.ObjectConverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DatabaseRegistry extends StringRegistry<Database> {
    protected Logger logger;
    private boolean setup;
    private Set<ObjectConverter> objectConverters = new HashSet<>();
    
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

    public Database register(Database object) {
        super.register(object.getName(), object);
        object.setRegistry(this);
        if (this.setup) {
            try {
                object.setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public void registerAll(Collection<Database> databases) {
        databases.forEach(this::register);
    }

    public Set<ObjectConverter> getObjectConverters() {
        return new HashSet<>(objectConverters);
    }
    
    public void addObjectConverter(ObjectConverter converter) {
        this.objectConverters.add(converter);
    }
}