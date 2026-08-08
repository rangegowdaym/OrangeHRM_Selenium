package com.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class StringUtils {
    private static final String DEFAULT_DATE_PATTERN = "MM/dd/yyyy";

    private StringUtils() {
    }

    public static String replaceParameterizedString(String text, String[] replaceList) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        Objects.requireNonNull(replaceList, "replaceList must not be null");

        String result = text;
        for (int index = 0; index < replaceList.length; index++) {
            String token = "{" + index + "}";
            String replacement = replaceList[index] == null ? "" : replaceList[index];
            result = result.replace(token, replacement);
        }
        return result;
    }

    public static String replaceParameterizedString(String text, Map<String, String> replaceList) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        Objects.requireNonNull(replaceList, "replaceList must not be null");

        String result = text;
        for (Map.Entry<String, String> entry : new LinkedHashMap<>(replaceList).entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isBlank()) {
                continue;
            }

            String value = entry.getValue() == null ? "" : entry.getValue();
            String directToken = key;
            String wrappedToken = key.startsWith("{") && key.endsWith("}") ? key : "{" + key + "}";
            result = result.replace(directToken, value).replace(wrappedToken, value);
        }
        return result;
    }

    public static String testDataModification(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        int randomInt = ThreadLocalRandom.current().nextInt(100);
        String randomChars = RandomStringUtils.randomAlphabetic(4);
        return value + randomChars + "_" + randomInt;
    }

    public static Date stringToDate(String date) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("date must not be blank");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        formatter.setLenient(false);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected " + DEFAULT_DATE_PATTERN + ": " + date, e);
        }
    }

    public static boolean isGreaterThanCurrentDate(Date dateToCompare) {
        Objects.requireNonNull(dateToCompare, "dateToCompare must not be null");
        return dateToCompare.after(new Date());
    }

    public static boolean isLesserThanCurrentDate(Date dateToCompare) {
        Objects.requireNonNull(dateToCompare, "dateToCompare must not be null");
        return dateToCompare.before(new Date());
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        return formatter.format(new Date());
    }
}
