package com.ui.actions;

import com.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Set;
import java.util.Objects;

public class WindowHandler extends BaseHandler {
    private static final Logger logger = LoggerUtils.getLogger(WindowHandler.class);

    public WindowHandler(WebDriver driver) {
        super(driver);
    }

    public String getMainWindowHandle() {
        return driver.getWindowHandle();
    }

    public WebDriver switchToLatestWindow() {
        String latestWindowHandle = driver.getWindowHandles().stream()
                .reduce((previous, current) -> current)
                .orElseThrow(() -> new WebDriverException("No window handles available"));
        driver.switchTo().window(latestWindowHandle);
        logger.info("Switched to window: {}", driver.getTitle());
        return driver;
    }

    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public WebDriver switchToWindow(String name) {
        driver.switchTo().window(name);
        logger.info("Switched to window: {}", driver.getTitle());
        return driver;
    }

    public void closeWindow(String windowHandle) {
        switchToWindow(windowHandle).close();
    }

    public void cleanUp(WebDriver webDriver) {
        Objects.requireNonNull(webDriver, "webDriver must not be null");
        for (String handle : webDriver.getWindowHandles()) {
            webDriver.switchTo().window(handle).close();
        }
    }

    public WebDriver getCurrentWindow() {
        return driver;
    }

    public String getParentWindow() {
        return getMainWindowHandle();
    }

    public void switchToParentWindow(String parentWindow) {
        switchToWindow(parentWindow);
    }

    public WebElement switchToModalDialog() {
        return driver.switchTo().activeElement();
    }

    public WebDriver switchToFrame(int index) {
        return driver.switchTo().frame(index);
    }

    public WebDriver switchToMainFrame() {
        return driver.switchTo().defaultContent();
    }

    public WebDriver switchToFrame(WebElement element) {
        return driver.switchTo().frame(element);
    }

    public WebDriver switchToFrame(String idOrName) {
        return driver.switchTo().frame(idOrName);
    }

    public WebDriver switchToParentFrame() {
        return driver.switchTo().parentFrame();
    }

    public WebDriver waitForFrameAndSwitchIt(int index) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
        return driver;
    }

    public WebDriver waitForFrameAndSwitchIt(String idOrName) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(idOrName));
        return driver;
    }

    public WebDriver waitForFrameAndSwitchIt(WebElement element) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
        return driver;
    }
}