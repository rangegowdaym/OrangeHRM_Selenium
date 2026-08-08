package com.ui.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ActionHandler extends BaseHandler {
    private final Actions actions;

    public ActionHandler(WebDriver driver) {
        super(driver);
        this.actions = new Actions(driver);
    }

    public void click(Object locator) {
        performAction(locator, element -> actions.click(element).perform(),
                "Unable to click on the element: " + locator);
    }

    public void doubleClick(Object locator) {
        performAction(locator, element -> actions.doubleClick(element).perform(),
                "Unable to double click on the element: " + locator);
    }

    public void moveToElementAndClick(Object locator) {
        performAction(locator, element -> {
            actions.moveToElement(element).click().perform();
        }, "Unable to move to element and click on the element: " + locator);
    }

    public void dragAndDrop(Object sourceLocator, Object targetLocator) {
        performAction(sourceLocator, sourceElement -> {
            WebElement targetElement = getElement(targetLocator);
            actions.dragAndDrop(sourceElement, targetElement).perform();
        }, "Unable to drag and drop from: " + sourceLocator + " to: " + targetLocator);
    }

    public void hoverOverElement(Object locator) {
        performAction(locator, element -> actions.moveToElement(element).perform(),
                "Unable to move to element: " + locator);
    }

    public void rightClick(Object locator) {
        performAction(locator, element -> actions.contextClick(element).perform(),
                "Unable to right click on the element: " + locator);
    }

    public void moveToElementAndSendKeys(Object locator, String keys) {
        performAction(locator, element -> actions.moveToElement(element).click().sendKeys(keys).perform(),
                "Unable to move to element and send keys: " + keys + " on locator: " + locator);
    }
}