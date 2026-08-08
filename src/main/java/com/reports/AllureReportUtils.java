package com.reports;

import com.utils.JsonUtils;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class AllureReportUtils {

    private static final String TEXT_PLAIN = "text/plain";
    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_XML = "application/xml";
    private static final String TEXT_CSV = "text/csv";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String APPLICATION_PDF = "application/pdf";
    private static final String BINARY = "application/octet-stream";

    private AllureReportUtils() {
    }

    public static void attachScreenshot(WebDriver driver, String name) {
        Objects.requireNonNull(driver, "driver must not be null");
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            throw new IllegalArgumentException("Driver does not support screenshots");
        }
        attachBytes(name, takesScreenshot.getScreenshotAs(OutputType.BYTES), IMAGE_PNG, ".png");
    }

    public static void attachScreenshot(WebDriver driver) {
        attachScreenshot(driver, "Screenshot");
    }

    public static void attachScreenshotFromBase64(String name, String base64Screenshot) {
        attachBytes(name, Base64.getDecoder().decode(validateText(base64Screenshot, "base64Screenshot")), IMAGE_PNG, ".png");
    }

    public static void attachLog(String name, String logText) {
        attachText(name, logText);
    }

    public static void attachText(String name, String text) {
        addAttachment(name, TEXT_PLAIN, validateText(text, "text"), ".txt");
    }

    public static void attachJson(String name, Object jsonObject) {
        attachJson(name, JsonUtils.toJson(jsonObject));
    }

    public static void attachJson(String name, String jsonText) {
        addAttachment(name, APPLICATION_JSON, validateText(jsonText, "jsonText"), ".json");
    }

    public static void attachHtml(String name, String htmlText) {
        addAttachment(name, TEXT_HTML, validateText(htmlText, "htmlText"), ".html");
    }

    public static void attachXml(String name, String xmlText) {
        addAttachment(name, TEXT_XML, validateText(xmlText, "xmlText"), ".xml");
    }

    public static void attachCsv(String name, String csvText) {
        addAttachment(name, TEXT_CSV, validateText(csvText, "csvText"), ".csv");
    }

    public static void attachStackTrace(String name, Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable must not be null");
        addAttachment(name, TEXT_PLAIN, stackTraceToString(throwable), ".txt");
    }

    public static void attachFile(Path file) {
        attachFile(file.getFileName().toString(), file);
    }

    public static void attachFile(String name, Path file) {
        Objects.requireNonNull(file, "file must not be null");
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Attachment file does not exist: " + file.toAbsolutePath());
        }

        try (InputStream inputStream = Files.newInputStream(file)) {
            String contentType = detectContentType(file);
            String extension = normalizeExtension(extensionFor(file, contentType));
            addAttachment(name, contentType, inputStream, extension);
        } catch (IOException e) {
            throw new RuntimeException("Failed to attach file: " + file.toAbsolutePath(), e);
        }
    }

    public static void attachBytes(String name, byte[] content, String contentType, String fileExtension) {
        Objects.requireNonNull(content, "content must not be null");
        addAttachment(name, contentType, new ByteArrayInputStream(content), normalizeExtension(fileExtension));
    }

    public static void attachBinary(String name, byte[] content) {
        attachBytes(name, content, BINARY, ".bin");
    }

    private static void addAttachment(String name, String contentType, String content, String fileExtension) {
        Allure.addAttachment(validateText(name, "name"), contentType, content, normalizeExtension(fileExtension));
    }

    private static void addAttachment(String name, String contentType, InputStream content, String fileExtension) {
        Allure.addAttachment(validateText(name, "name"), contentType, content, normalizeExtension(fileExtension));
    }

    private static String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static String stackTraceToString(Throwable throwable) {
        StringWriter writer = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            throwable.printStackTrace(printWriter);
        }
        return writer.toString();
    }

    private static String detectContentType(Path file) throws IOException {
        String contentType = Files.probeContentType(file);
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }
        return switch (extensionOf(file).toLowerCase()) {
            case "png" -> IMAGE_PNG;
            case "jpg", "jpeg" -> IMAGE_JPEG;
            case "json" -> APPLICATION_JSON;
            case "html", "htm" -> TEXT_HTML;
            case "xml" -> TEXT_XML;
            case "csv" -> TEXT_CSV;
            case "pdf" -> APPLICATION_PDF;
            case "txt", "log" -> TEXT_PLAIN;
            default -> BINARY;
        };
    }

    private static String extensionFor(Path file, String contentType) {
        String extension = extensionOf(file);
        if (!extension.isBlank()) {
            return "." + extension;
        }
        return switch (contentType) {
            case IMAGE_PNG -> ".png";
            case IMAGE_JPEG -> ".jpg";
            case APPLICATION_JSON -> ".json";
            case TEXT_HTML -> ".html";
            case TEXT_XML -> ".xml";
            case TEXT_CSV -> ".csv";
            case APPLICATION_PDF -> ".pdf";
            case TEXT_PLAIN -> ".txt";
            default -> ".bin";
        };
    }

    private static String extensionOf(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex >= 0 && dotIndex < fileName.length() - 1 ? fileName.substring(dotIndex + 1) : "";
    }

    private static String normalizeExtension(String fileExtension) {
        validateText(fileExtension, "fileExtension");
        return fileExtension.startsWith(".") ? fileExtension : "." + fileExtension;
    }
}
