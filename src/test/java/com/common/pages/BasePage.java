package com.common.pages;

import com.ui.actions.ActionHandler;
import com.ui.actions.BaseHandler;
import com.ui.actions.ElementHandler;
import org.openqa.selenium.WebDriver;

public abstract class BasePage {
    protected WebDriver driver;
    protected BaseHandler baseHandler;
    protected ElementHandler elementHandler;
    protected ActionHandler actionHandler;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.baseHandler = new BaseHandler(driver);
        this.elementHandler = new ElementHandler(driver);
        this.actionHandler = new ActionHandler(driver);
    }

    public abstract boolean isPageLoaded();

    public void launchApplication(String url) {
        baseHandler.launchUrl(url);
    }
}
