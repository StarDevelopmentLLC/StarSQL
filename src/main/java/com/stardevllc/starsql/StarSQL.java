package com.stardevllc.starsql;

import com.stardevllc.starsql.converters.impl.*;
import com.stardevllc.starsql.interfaces.ObjectConverter;
import com.stardevllc.starsql.model.DatabaseRegistry;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public final class StarSQL {
    private static Logger logger = createLogger();

    public static final Set<ObjectConverter> DEFAULT_TYPE_HANDLERS = Set.of(new BooleanConverter(), new DoubleConverter(), new EnumConverter(), new FloatConverter(), new IntegerConverter(), new LongConverter(), new StringConverter(), new UUIDConverter());

    public static DatabaseRegistry createDatabaseRegistry() {
        return new DatabaseRegistry(logger);
    }

    public static void setLogger(Logger logger) {
        StarSQL.logger = logger;
    }

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