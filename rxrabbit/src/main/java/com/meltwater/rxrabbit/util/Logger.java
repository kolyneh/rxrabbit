package com.meltwater.rxrabbit.util;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.meltwater.rxrabbit.util.LogFiltering.filterStackTrace;

/**
 * Logger class which provides a standardized way of outputting variables and their values.
 */
public class Logger {
    private final org.slf4j.Logger logger;

    private static final List<Class<?>> JAVA_WRAPPER_TYPES = new ArrayList<Class<?>>() {{
        add(Boolean.class);
        add(Byte.class);
        add(Character.class);
        add(Double.class);
        add(Float.class);
        add(Integer.class);
        add(Long.class);
        add(Short.class);
        add(Void.class);
    }};

    public Logger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public Logger(String loggerName) {
        this(LoggerFactory.getLogger(loggerName));
    }

    protected Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return logger.getName();
    }


    public void traceWithParams(String message, Object... arguments) {
        if (!logger.isTraceEnabled()) {
            return;
        }
        try {
            logger.trace(buildLogMessage(message, arguments));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
        }
    }

    public void traceWithParams(String message, Throwable t, Object... arguments) {
        if (!logger.isTraceEnabled()) {
            return;
        }
        try {
            logger.trace(buildLogMessage(message, arguments), filterStackTrace(t));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
            logger.trace(message, filterStackTrace(t));
        }
    }


    public void debugWithParams(String message, Object... arguments) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        try {
            logger.debug(buildLogMessage(message, arguments));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
        }
    }

    public void debugWithParams(String message, Throwable t, Object... arguments) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        try {
            logger.debug(buildLogMessage(message, arguments), filterStackTrace(t));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
            logger.debug(message, filterStackTrace(t));
        }
    }

    public void infoWithParams(String message, Object... arguments) {
        if (!logger.isInfoEnabled()) {
            return;
        }
        try {
            logger.info(buildLogMessage(message, arguments));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
        }
    }

    public void infoWithParams(String message, Throwable t, Object... arguments) {
        if (!logger.isInfoEnabled()) {
            return;
        }
        try {
            logger.info(buildLogMessage(message, arguments), filterStackTrace(t));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
            logger.info(message, filterStackTrace(t));
        }
    }

    public void warnWithParams(String message, Object... arguments) {
        if (!logger.isWarnEnabled()) {
            return;
        }
        try {
            logger.warn(buildLogMessage(message, arguments));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
        }
    }

    public void warnWithParams(String message, Throwable t, Object... arguments) {
        if (!logger.isWarnEnabled()) {
            return;
        }
        try {
            logger.warn(buildLogMessage(message, arguments), filterStackTrace(t));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
            logger.warn(message, filterStackTrace(t));
        }
    }

    public void errorWithParams(String message, Object... arguments) {
        if (!logger.isErrorEnabled()) {
            return;
        }
        try {
            logger.error(buildLogMessage(message, arguments));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
        }
    }

    public void errorWithParams(String message, Throwable t, Object... arguments) {
        if (!logger.isErrorEnabled()) {
            return;
        }
        try {
            logger.error(buildLogMessage(message, arguments), filterStackTrace(t));
        } catch (IllegalArgumentException e) {
            logMessageAssemblyFailure(message, arguments);
            logger.error(message, filterStackTrace(t));
        }
    }

    private void logMessageAssemblyFailure(String message, Object... arguments) {
        logger.error(
                "Failed to assemble log message for logger {}! Arguments must be declared in pairs! message={}, arguments={}",
                getName(), message, Arrays.toString(arguments));
    }

    protected String buildLogMessage(String message, Object[] arguments) {
        if (arguments.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Arguments must be declared in pairs: (message, key, value, key2, value2, ...)");
        }
        final StringBuilder sb = new StringBuilder(message);
        if (arguments.length == 0) {
            return sb.toString();
        }
        sb.append(" [ ");
        for (int i = 0; i < arguments.length; i += 2) {
            append(sb, arguments[i], arguments[i+1]);

            if (i + 2 < arguments.length) {
                sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    private void appendList(StringBuilder sb, Object key, List list) {
        for(int i=0; i<list.size(); i++) {
            append(sb, key, list.get(i));

            if (i + 1 < list.size()) {
                sb.append(", ");
            }
        }
    }

    private void append(StringBuilder sb, Object key, Object value) {
        if(value instanceof Object[]) {
            appendList(sb, key, Arrays.asList((Object[]) value));
        } else if (value instanceof List) {
            appendList(sb, key, (List) value);
        } else {
            sb.append(key);
            sb.append('=');
            if(!isPrimitive(value)) {
                sb.append('"');
                sb.append(value);
                sb.append('"');
            } else {
                sb.append(value);
            }
        }
    }

    private boolean isPrimitive(Object o) {
        return o==null || JAVA_WRAPPER_TYPES.contains(o.getClass());
    }
}