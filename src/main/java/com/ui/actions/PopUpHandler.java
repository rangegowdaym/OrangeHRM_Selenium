package com.ui.actions;

import com.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PopUpHandler extends BaseHandler {
    private static final Logger logger = LoggerUtils.getLogger(PopUpHandler.class);

    public PopUpHandler(WebDriver driver) {
        super(driver);
    }

    public boolean isAlertPresent() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public Alert switchToAlert() {
        if (isAlertPresent()) {
            logger.info("Switching to Alert...");
            return driver.switchTo().alert();
        }
        logger.warn("No alert present to switch to");
        return null;
    }

    public void acceptAlert() {
        if (isAlertPresent()) {
            driver.switchTo().alert().accept();
            logger.info("Alert accepted");
        } else {
            logger.warn("No alert present to accept");
        }
    }

    public void dismissAlert() {
        if (isAlertPresent()) {
            driver.switchTo().alert().dismiss();
            logger.info("Alert dismissed");
        } else {
            logger.warn("No alert present to dismiss");
        }
    }

    public String getAlertText() {
        if (isAlertPresent()) {
            String alertText = driver.switchTo().alert().getText();
            logger.info("Retrieved alert text");
            return alertText;
        }
        logger.warn("No alert present to read text from");
        return null;
    }

    public void loginWithoutPopup(String urlWithoutHTTPS, String userName, String password) {
        driver.get("https://" + userName + ":" + password + "@" + urlWithoutHTTPS);
    }

    public void loginWithHttpAuthentication(String url, String userName, String password) {
        if (driver instanceof HasAuthentication authentication) {
            authentication.register(UsernameAndPassword.of(userName, password));
            driver.get(url);
        } else {
            throw new UnsupportedOperationException("The WebDriver instance does not support HasAuthentication.");
        }
    }
}