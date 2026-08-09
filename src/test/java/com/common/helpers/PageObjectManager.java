package com.common.helpers;

import com.common.pages.HomePage;
import com.common.pages.LoginPage;
import com.ui.driverfactory.DriverManager;
import org.openqa.selenium.WebDriver;

public class PageObjectManager {
    private WebDriver driver;

    public PageObjectManager() {
        this.driver = DriverManager.getInstance().getDriver();
    }

    public HomePage getHomePage() {
        return new HomePage(driver);
    }

    public LoginPage getLoginPage() {
        return new LoginPage(driver);
    }
}
