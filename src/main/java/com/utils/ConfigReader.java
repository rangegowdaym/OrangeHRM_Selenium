package com.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties CONFIG_PROPERTIES = new Properties();

    private ConfigReader() {
    }

    public static Properties loadProperties(String fileName) {
        String resolvedFileName = requireFileName(fileName);
        Properties loadedProperties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(resolvedFileName)) {
            loadedProperties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config file: " + resolvedFileName, e);
        }
        return loadedProperties;
    }

    public static synchronized void loadAllProperties(String... fileNames) {
        Objects.requireNonNull(fileNames, "fileNames must not be null");

        for (String fileName : fileNames) {
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("Config file path must not be blank");
            }

            try (FileInputStream inputStream = new FileInputStream(fileName)) {
                Properties tempProperties = new Properties();
                tempProperties.load(inputStream);
                CONFIG_PROPERTIES.putAll(tempProperties);
            } catch (FileNotFoundException e) {
                LoggerUtils.warn("Config file not found: {}", fileName);
            } catch (IOException e) {
                throw new RuntimeException("Could not load config file: " + fileName, e);
            }
        }
    }

    public static String getString(String key) {
        String resolvedKey = requireKey(key);
        return CONFIG_PROPERTIES.getProperty(resolvedKey);
    }

    public static int getInt(String key) {
        String value = requireValue(key);
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Property is not a valid integer: " + key + " = " + value);
        }
    }

    public static boolean getBoolean(String key) {
        String value = requireValue(key).trim();
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Property is not a valid boolean (true/false): " + key + " = " + value);
    }

    public static long getLong(String key) {
        String value = requireValue(key);
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Property is not a valid long: " + key + " = " + value);
        }
    }

    private static String requireFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Config file path must not be blank");
        }
        return fileName;
    }

    private static String requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Config key must not be blank");
        }
        return key;
    }

    private static String requireValue(String key) {
        String value = getString(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Property not found or blank: " + key);
        }
        return value;
    }
}
