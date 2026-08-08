package com.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJson(Object object) {
        Objects.requireNonNull(object, "object must not be null");
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        validateText(jsonString, "jsonString");
        Objects.requireNonNull(clazz, "clazz must not be null");
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON string", e);
        }
    }

    public static <T> T fromJson(File file, Class<T> clazz) {
        Objects.requireNonNull(file, "file must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");
        try {
            return OBJECT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from file: " + file.getAbsolutePath(), e);
        }
    }

    public static void writeJsonToFile(Object object, String filePath) {
        Objects.requireNonNull(object, "object must not be null");
        validateText(filePath, "filePath");
        try {
            OBJECT_MAPPER.writeValue(new File(filePath), object);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON to file: " + filePath, e);
        }
    }

    private static void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
