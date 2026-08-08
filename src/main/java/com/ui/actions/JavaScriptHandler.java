package com.ui.actions;

import com.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

public class JavaScriptHandler extends BaseHandler {
    private static final Logger logger = LoggerUtils.getLogger(JavaScriptHandler.class);
    private final JavascriptExecutor jsExecutor;

    public JavaScriptHandler(WebDriver driver) {
        super(driver);
        if (!(driver instanceof JavascriptExecutor executor)) {
            throw new IllegalArgumentException("WebDriver must implement JavascriptExecutor");
        }
        this.jsExecutor = executor;
    }

    public void scrollToElement(Object locator) {
        executeOnElement("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", locator);
    }

    public void javaScriptClick(Object locator) {
        executeOnElement("arguments[0].click();", locator);
    }

    public void scrollToElementAndClick(Object locator) {
        executeOnElement("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'}); arguments[0].click();", locator);
    }

    public void mouseHoverJScript(Object locator) {
        executeOnElement("""
                var event;
                if (typeof MouseEvent === 'function') {
                    event = new MouseEvent('mouseover', { bubbles: true, cancelable: true, view: window });
                } else {
                    event = document.createEvent('MouseEvents');
                    event.initEvent('mouseover', true, true);
                }
                arguments[0].dispatchEvent(event);
                """, locator);
    }

    public void highlightElement(Object locator) {
        executeOnElement("arguments[0].setAttribute('style', arguments[1]);", locator,
                "color: red; border: 2px solid yellow;");
    }

    public void sendKeysWithJS(Object locator, String text) {
        executeOnElement("""
                arguments[0].value = arguments[1];
                arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
                arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
                """, locator, text);
    }

    public void scrollToPageBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollToPageTop() {
        executeScript("window.scrollTo(0, 0);");
    }

    public String getElementInnerText(Object locator) {
        return (String) executeOnElement("return arguments[0].innerText;", locator);
    }

    public String getElementAttribute(Object locator, String attribute) {
        return (String) executeOnElement("return arguments[0].getAttribute(arguments[1]);", locator, attribute);
    }

    public void clearElement(Object locator) {
        executeOnElement("""
                arguments[0].value = '';
                arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
                arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
                """, locator);
    }

    public void zoomPage(String scale) {
        executeScript("document.body.style.zoom = arguments[0];", scale);
    }

    public void scrollToElementAndHighlight(Object locator) {
        executeOnElement("""
                arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});
                arguments[0].setAttribute('style', 'color: red; border: 2px solid yellow;');
                """, locator);
    }

    public void scrollToElementAndSendKeys(Object locator, String keys) {
        executeOnElement("""
                arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});
                arguments[0].value = arguments[1];
                arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
                arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
                """, locator, keys);
    }

    private Object executeScript(String script, Object... arguments) {
        return jsExecutor.executeScript(script, arguments);
    }

    private Object executeOnElement(String script, Object locator, Object... arguments) {
        logger.info("Executing JavaScript on element: {}", locator);
        Object[] executionArguments = new Object[arguments.length + 1];
        executionArguments[0] = getElement(locator);
        System.arraycopy(arguments, 0, executionArguments, 1, arguments.length);
        return jsExecutor.executeScript(script, executionArguments);
    }
}