package com.common.helpers;

import com.ui.helpers.ScreenshotHelper;
import com.utils.ConfigReader;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class TestContext {
    private PageObjectManager pageObjectManager;
    private Scenario scenario;
    private WebDriver driver;
    private ScreenshotHelper screenshotHelper;
    private String platform;
    private String browser;
    private String browserVersion;

    // ── Driver ───────────────────────────────────────────────────────────────

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    // ── Scenario ─────────────────────────────────────────────────────────────

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    // ── Screenshot helper ────────────────────────────────────────────────────

    public ScreenshotHelper getScreenshotHelper() {
        return screenshotHelper;
    }

    public void setScreenshotHelper(WebDriver driver) {
        this.screenshotHelper = new ScreenshotHelper(driver);
    }

    // ── Browser / platform resolution ────────────────────────────────────────

    public String getPlatform() {
        return platform;
    }

    public void setPlatform() {
        this.platform = System.getProperty("platform");
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser() {
        this.browser = System.getProperty("browser");
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion() {
        this.browserVersion = System.getProperty("browserVersion");
    }

    // ── Page object manager ──────────────────────────────────────────────────

    public PageObjectManager getPageObjectManager() {
        if (pageObjectManager == null) {
            pageObjectManager = new PageObjectManager();
        }
        return pageObjectManager;
    }

    // ── Test data path ───────────────────────────────────────────────────────

    public File getResourcePath(String filePath) {
        String testDataPath = ConfigReader.getString("testdata.path");
        if (testDataPath == null || testDataPath.isEmpty()) {
            testDataPath = "src/test/resources/";
        }
        return new File(System.getProperty("user.dir") + "/" + testDataPath + filePath);
    }
}