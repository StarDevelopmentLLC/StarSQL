package com.stardevllc.starsql.common;

import com.stardevllc.starsql.api.interfaces.TypeHandler;
import com.stardevllc.starsql.api.interfaces.model.SQLDatabase;
import com.stardevllc.starsql.common.typehandlers.SQLTypeHandler;
import com.stardevllc.starsql.common.typehandlers.impl.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.*;

/**
 * This library allows a developer to use Java Classes to represent MySQL Tables.<br>
 * You cannot do everything in MySQL with this library, it was created as a means to prevent manual saving and loading of data to database tables and prevent errors related to building statements.<br>
 * There are plans on a complete rewrite of this library into a new library that will not just handle MySQL but some other storage methods like JSON, and Yaml<br>
 * This library is designed to be interacted with in java code, however it is also done in a way where the developer can manage their own files for configuration.<br>
 * I have done my best to document this library thoroughly, with improvements being made to the library and documentation<br>
 * The entry point to using this library is the {@link SQLDatabaseRegistry} class. You can call the constructor yourself providng your own {@link Logger} or call the {@code createDatabaseRegistry()} method in this class and use a default logger.<br>
 * Please see the documentation for that class to learn more about what you can do.
 *
 * @see SQLDatabaseRegistry
 */
public final class StarSQL {
    private static Logger logger = createLogger();

    /**
     * The default {@link SQLTypeHandler}'s supported by this library. You can take a look at these to see implementations
     *
     * @see SQLTypeHandler
     */
    public static final Set<TypeHandler<SQLDatabase>> DEFAULT_TYPE_HANDLERS = Set.of(new BooleanHandler(), new DoubleHandler(), new EnumHandler(), new FloatHandler(), new IntegerHandler(), new LongHandler(), new StringHandler(), new UUIDHander(), new ValueHandler());

    /**
     * Creates an instance of the {@link SQLDatabaseRegistry} with a default logger or the one set with the {@code setLogger()} method
     *
     * @return The created DatabaseRegistry
     */
    public static SQLDatabaseRegistry createDatabaseRegistry() {
        return new SQLDatabaseRegistry(logger);
    }

    /**
     * Sets a default logger for StarSQL to use when using the {@code createDatabaseRegistry()} factory method
     *
     * @param logger The new logger
     */
    public static void setLogger(Logger logger) {
        StarSQL.logger = logger;
    }

    /**
     * @return The logger used by the whole utility
     */
    public static Logger getLogger() {
        return logger;
    }

    private static Logger createLogger() {
        Logger logger = Logger.getLogger(StarSQL.class.getName());
        logger.setUseParentHandlers(false);
        logger.addHandler(new StreamHandler(System.out, new Formatter(StarSQL.class.getName())));
        return logger;
    }

    public static class Formatter extends SimpleFormatter {

        private String name;

        public Formatter(String name) {
            this.name = name;
        }

        @Override
        public String format(LogRecord record) {
            Instant instant = record.getInstant();
            LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return time.getMonthValue() + "/" + time.getDayOfMonth() + "/" + time.getYear() + " " +
                    time.getHour() + ":" + time.getMinute() + ":" + time.getSecond()
                    + " " + record.getLevel().getName() +
                    " [" + name + "] " + record.getMessage() + "\n";
        }
    }
}