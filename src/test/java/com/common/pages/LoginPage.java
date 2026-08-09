package com.common.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class LoginPage extends BasePage {
    WebDriver driver;
    private static final By ORANGE_HRM_LOGO = By.cssSelector(".orangehrm-login-branding");
    private static final By USERNAME_TEXTBOX = By.cssSelector("input[name='username']");
    private static final By PASSWORD_TEXTBOX = By.cssSelector("input[name='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By LOGIN_ERROR_MESSAGE = By.cssSelector(".oxd-alert-content--error");
    private static final By REQUIRED_FIELD_ERROR_MESSAGE = By.cssSelector(".oxd-input-field-error-message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return elementHandler.isElementDisplayed(ORANGE_HRM_LOGO);
    }

    public void enterCredentials(String username, String password) {
        elementHandler.enterText(USERNAME_TEXTBOX, username);
        elementHandler.enterText(PASSWORD_TEXTBOX, password);
    }

    public void clickLoginButton() {
        elementHandler.clickElement(LOGIN_BUTTON);
    }

    public void login(String username, String password) {
        enterCredentials(username, password);
        clickLoginButton();
    }

    public String getErrorMessage() {
        return elementHandler.getElementText(LOGIN_ERROR_MESSAGE);
    }

    public String getRequiredFieldErrorMessage() {
        return elementHandler.getElementText(REQUIRED_FIELD_ERROR_MESSAGE);
    }

    public List<String> getRequiredFieldErrorMessages() {
        return elementHandler.getElementTexts(REQUIRED_FIELD_ERROR_MESSAGE);
    }
}
