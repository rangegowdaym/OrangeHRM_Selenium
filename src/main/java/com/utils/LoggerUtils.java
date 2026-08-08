package com.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public final class LoggerUtils {
    private static final Logger LOGGER = LogManager.getLogger(LoggerUtils.class);

    private LoggerUtils() {
    }

    public static Logger getLogger(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        return LogManager.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return LogManager.getLogger(name);
    }

    public static void trace(String message) {
        LOGGER.trace(message);
    }

    public static void trace(String message, Object... args) {
        LOGGER.trace(message, args);
    }

    public static void trace(String message, Throwable throwable) {
        LOGGER.trace(message, throwable);
    }

    public static void debug(String message) {
        LOGGER.debug(message);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void debug(String message, Throwable throwable) {
        LOGGER.debug(message, throwable);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void info(String message, Throwable throwable) {
        LOGGER.info(message, throwable);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void warn(String message, Throwable throwable) {
        LOGGER.warn(message, throwable);
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    public static void fatal(String message) {
        LOGGER.fatal(message);
    }

    public static void fatal(String message, Object... args) {
        LOGGER.fatal(message, args);
    }

    public static void fatal(String message, Throwable throwable) {
        LOGGER.fatal(message, throwable);
    }

    public static void log(Level level, String message) {
        Objects.requireNonNull(level, "level must not be null");
        LOGGER.log(level, message);
    }

    public static void log(Level level, String message, Object... args) {
        Objects.requireNonNull(level, "level must not be null");
        LOGGER.log(level, message, args);
    }

    public static boolean isTraceEnabled() {
        return LOGGER.isTraceEnabled();
    }

    public static boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }
}
