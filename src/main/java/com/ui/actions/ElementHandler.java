package com.ui.actions;

import com.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class ElementHandler extends BaseHandler {
    private static final Logger logger = LoggerUtils.getLogger(ElementHandler.class);

    public ElementHandler(WebDriver driver) {
        super(driver);
    }

    public void click(Object locator) {
        logger.info("Clicking the element: {}", locator);
        waitForElementToBeClickable(locator);
        performAction(locator, WebElement::click, "Unable to click on the element: " + locator);
        logger.info("Element clicked successfully: {}", locator);
    }

    public void clickElement(Object locator) {
        click(locator);
    }

    public void enterText(Object locator, String text) {
        logger.info("Entering text in element: {} with text: {}", locator, text);
        performAction(locator, element -> {
            waitForElementToBeClickable(element);
            element.clear();
            element.sendKeys(text);
        }, "Unable to enter text in the element: " + locator);
        logger.info("Text: {} entered successfully in element: {}", text, locator);
    }

    public String getElementText(Object locator) {
        logger.info("Getting text from element: {}", locator);
        String text = performFunctionAction(locator, WebElement::getText, String.format("Unable to get text from the element: %s", locator));
        logger.info("Text retrieved successfully from element: {} - Text: {}", locator, text);
        return text;
    }

    public String getElementAttribute(Object locator, String attribute) {
        logger.info("Getting attribute '{}' from element: {}", attribute, locator);
        String attrValue = performFunctionAction(locator, element -> element.getAttribute(attribute),
                String.format("Unable to get attribute '%s' from the element: %s", attribute, locator));
        logger.info("Attribute '{}' retrieved successfully from element: {} - Value: {}", attribute, locator, attrValue);
        return attrValue;
    }

    public String getElementCssValue(Object locator, String propertyName) {
        logger.info("Getting CSS value '{}' from element: {}", propertyName, locator);
        String cssValue = performFunctionAction(locator, element -> element.getCssValue(propertyName),
                String.format("Unable to get CSS value '%s' from the element: %s", propertyName, locator));
        logger.info("CSS value '{}' retrieved successfully from element: {} - Value: {}", propertyName, locator, cssValue);
        return cssValue;
    }

    public List<String> getElementTexts(Object locator) {
        logger.info("Getting text list from elements: {}", locator);
        List<String> textList = getElements(locator).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        logger.info("Text list retrieved successfully from elements: {} - Values: {}", locator, textList);
        return textList;
    }

    public boolean isElementDisplayed(Object locator) {
        logger.info("Checking if element is displayed: {}", locator);
        boolean isDisplayed = performFunctionAction(locator, WebElement::isDisplayed, "Unable to check if element is displayed: ");
        logger.info("Element displayed status: {} - {}", locator, isDisplayed);
        return isDisplayed;
    }

    public boolean isElementEnabled(Object locator) {
        logger.info("Checking if element is enabled: {}", locator);
        boolean isEnabled = performFunctionAction(locator, WebElement::isEnabled, "Unable to check if element is enabled: ");
        logger.info("Element enabled status: {} - {}", locator, isEnabled);
        return isEnabled;
    }

    public boolean isElementSelected(Object locator) {
        logger.info("Checking if element is selected: {}", locator);
        boolean isSelected = performFunctionAction(locator, WebElement::isSelected, "Unable to check if element is selected: ");
        logger.info("Element selected status: {} - {}", locator, isSelected);
        return isSelected;
    }

    public Select getSelectElement(Object locator) {
        return new Select(getElement(locator));
    }

    public void selectByVisibleText(Object locator, String visibleText) {
        logger.info("Selecting option by visible text: '{}' in element: {}", visibleText, locator);
        performAction(locator, e -> new Select(e).selectByVisibleText(visibleText),
                "Unable to select option by text: " + visibleText + " on locator: " + locator);
        logger.info("Option '{}' selected successfully in element: {}", visibleText, locator);
    }

    public void selectByValue(Object locator, String value) {
        logger.info("Selecting option by value: '{}' in element: {}", value, locator);
        performAction(locator, e -> new Select(e).selectByValue(value),
                "Unable to select option by value: " + value + " on locator: " + locator);
        logger.info("Option with value '{}' selected successfully in element: {}", value, locator);
    }

    public void selectByIndex(Object locator, int index) {
        logger.info("Selecting option by index: '{}' in element: {}", index, locator);
        performAction(locator, e -> new Select(e).selectByIndex(index),
                "Unable to select option by index: " + index + " on locator: " + locator);
        logger.info("Option at index '{}' selected successfully in element: {}", index, locator);
    }

    public List<String> getAllSelectOptions(Object locator) {
        logger.info("Getting all options from select element: {}", locator);
        List<String> options = performFunctionAction(locator,
                e -> new Select(e).getOptions().stream().map(WebElement::getText).toList(),
                "Unable to get options from select element: " + locator);
        if (options.isEmpty()) {
            logger.error("No options found in select element: {}", locator);
            throw new WebDriverException("No options found in select element: " + locator);
        }
        logger.info("All options retrieved successfully from select element: {} - Options: {}", locator, options);
        return options;
    }

    public List<String> getAllSelectedOptions(Object locator) {
        logger.info("Getting all selected options from select element: {}", locator);
        List<String> selectedOptions = performFunctionAction(locator,
                e -> new Select(e).getAllSelectedOptions().stream().map(WebElement::getText).toList(),
                "Unable to get selected options from select element: " + locator);
        if (selectedOptions.isEmpty()) {
            logger.error("No selected options found in select element: {}", locator);
            throw new WebDriverException("No selected options found in select element: " + locator);
        }
        logger.info("All selected options retrieved successfully from select element: {} - Selected Options: {}", locator, selectedOptions);
        return selectedOptions;
    }

    public String getFirstSelectedOption(Object locator) {
        logger.info("Getting first selected option from select element: {}", locator);
        String firstSelectedOption = performFunctionAction(locator, e -> new Select(e).getFirstSelectedOption().getText(),
                "Unable to get first selected option");
        logger.info("First selected option retrieved successfully from select element: {} - First Selected Option: {}", locator, firstSelectedOption);
        return firstSelectedOption;
    }

    public void deselectAllOptions(Object locator) {
        logger.info("Deselecting all options from select element: {}", locator);
        performAction(locator, e -> new Select(e).deselectAll(),
                "Unable to deselect all options from select element: " + locator);
        logger.info("All options deselected successfully from select element: {}", locator);
    }

    public void clearText(Object locator) {
        logger.info("Clearing text in element: {}", locator);
        performAction(locator, WebElement::clear, "Unable to clear text in the element: " + locator);
        logger.info("Text cleared successfully in element: {}", locator);
    }

    public void submit(Object locator) {
        logger.info("Submitting element: {}", locator);
        performAction(locator, WebElement::submit, "Unable to submit the element: " + locator);
        logger.info("Element submitted successfully: {}", locator);
    }
}