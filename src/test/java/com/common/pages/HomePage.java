package com.common.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private static final String DASHBOARD_PATH = "/dashboard";
    private static final By USER_DROPDOWN = By.cssSelector(".oxd-userdropdown-name");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return baseHandler.waitForUrlContains(DASHBOARD_PATH)
                && elementHandler.isElementDisplayed(USER_DROPDOWN);
    }
}
