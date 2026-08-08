package com.ui.actions;

import com.utils.ConfigReader;
import com.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Objects;

public class BaseHandler {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private static final Logger logger = LoggerUtils.getLogger(BaseHandler.class);
    private static final int GLOBAL_TIMEOUT = ConfigReader.getInt("global.timeout");

    public BaseHandler(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(GLOBAL_TIMEOUT));
    }

    public WebElement getElement(Object locator) {
        return resolveElement(locator);
    }

    public List<WebElement> getElements(Object locator) {
        List<WebElement> elements = resolveElements(locator);
        if (elements.isEmpty()) {
            throw buildWebDriverException("Elements not found: " + locator, null);
        }
        return elements;
    }

    public void launchUrl(String url) {
        logger.info("Launching URL: {}", url);
        executeDriverAction(() -> driver.get(url), "Failed to launch URL: " + url);
        logger.info("URL launched successfully: {}", url);
    }

    public void refreshPage() {
        logger.info("Refreshing the page");
        executeDriverAction(() -> driver.navigate().refresh(), "Failed to refresh the page");
        logger.info("Page refreshed successfully");
    }

    public boolean waitForElementInvisible(Object locator) {
        return Boolean.TRUE.equals(performFunctionAction(locator,
                element -> wait.until(ExpectedConditions.invisibilityOf(element)),
                "Element not invisible: " + locator));
    }

    public boolean waitForTextToBePresentInElement(Object locator, String text) {
        return Boolean.TRUE.equals(performFunctionAction(locator,
                element -> wait.until(ExpectedConditions.textToBePresentInElement(element, text)),
                "Text not found in element: " + locator + " with text: " + text));
    }

    public WebElement waitForElementToBeClickable(Object locator) {
        WebElement element = resolveElement(locator);
        WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        logger.info("Element is clickable: {}", clickableElement);
        return clickableElement;
    }

    protected void performAction(Object locator, Action action, String errorMessage) {
        performFunctionAction(locator, element -> {
            action.apply(element);
            return null;
        }, errorMessage);
    }

    protected <R> R performFunctionAction(Object locator, Function<WebElement, R> action, String errorMessage) {
        try {
            return action.apply(resolveElement(locator));
        } catch (WebDriverException e) {
            handleException(e, errorMessage);
            return null;
        }
    }

    protected void performConsumerAction(Object locator, Consumer<WebElement> action, String errorMessage) {
        performAction(locator, action::accept, errorMessage);
    }

    protected WebElement resolveElement(Object locator) {
        Objects.requireNonNull(locator, "locator must not be null");

        if (locator instanceof By by) {
            return wait.until(ExpectedConditions.presenceOfElementLocated(by));
        }

        if (locator instanceof WebElement element) {
            wait.until(ExpectedConditions.visibilityOf(element));
            return element;
        }

        throw new IllegalArgumentException("Unsupported locator type: " + locator.getClass().getName());
    }

    protected List<WebElement> resolveElements(Object locator) {
        Objects.requireNonNull(locator, "locator must not be null");

        if (locator instanceof By by) {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        }

        if (locator instanceof WebElement element) {
            wait.until(ExpectedConditions.visibilityOf(element));
            return Collections.singletonList(element);
        }

        throw new IllegalArgumentException("Unsupported locator type: " + locator.getClass().getName());
    }

    protected void handleException(WebDriverException e, String message) {
        logger.error(message, e);
        throw buildWebDriverException(message, e);
    }

    private void executeDriverAction(Runnable action, String errorMessage) {
        try {
            action.run();
        } catch (WebDriverException e) {
            handleException(e, errorMessage);
        }
    }

    private WebDriverException buildWebDriverException(String message, Throwable cause) {
        WebDriverException exception = new WebDriverException(message);
        if (cause != null) {
            exception.initCause(cause);
        }
        return exception;
    }

    @FunctionalInterface
    protected interface Action {
        void apply(WebElement element);
    }
}