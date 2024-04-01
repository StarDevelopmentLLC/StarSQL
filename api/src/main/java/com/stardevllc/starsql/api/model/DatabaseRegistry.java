package com.stardevllc.starsql.api.model;

import com.stardevllc.starsql.api.interfaces.model.Database;
import com.stardevllc.starlib.registry.StringRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Parent Registry class that defines the basics of a DatabaseRegistry. Extends from StringRegistry
 * @param <D> The type of Database that this Registry Represents
 */
public abstract class DatabaseRegistry<D extends Database> extends StringRegistry<D> {
    protected final Logger logger;
    public DatabaseRegistry(Logger logger, Map<String, D> initialObjects) {
        super(initialObjects, key -> key.toLowerCase().replace(" ", "_"), null);
        this.logger = logger;
    }

    public DatabaseRegistry(Logger logger) {
        this(logger, null);
    }

    public Logger getLogger() {
        return logger;
    }

    public abstract void setup() throws Exception;
    public abstract boolean isSetup();
    public abstract void register(D database);
    public abstract void registerAll(Collection<D> databases);
}