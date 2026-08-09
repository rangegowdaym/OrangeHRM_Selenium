package com.testsuites;

import com.common.helpers.PageObjectManager;
import com.common.helpers.TestContext;
import com.reports.AllureReportUtils;
import com.ui.driverfactory.DriverManager;
import com.ui.helpers.ScreenshotHelper;
import com.utils.ConfigReader;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public abstract class BaseTest {
    protected WebDriver driver;
    protected TestContext testContext;

    static {
        String projectDir = System.getProperty("user.dir");
        String env = System.getProperty("env", "dev");
        String globalConfig = projectDir + "/src/test/resources/config/global.properties";
        String envConfig = projectDir + "/src/test/resources/config/" + env + ".properties";
        ConfigReader.loadAllProperties(globalConfig, envConfig);
    }

    @BeforeSuite(alwaysRun = true)
    public void loadConfiguration() {
        testContext = new TestContext();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"platform", "browser", "browserVersion"})
    public void setUp(@Optional String platform, @Optional String browser, @Optional String browserVersion) {
        driver = DriverManager.getInstance().getDriver(platform, browser, browserVersion);
        testContext.setScreenshotHelper(driver);
        logStep("Driver initialized");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess() && testContext.getScreenshotHelper() != null) {
            AllureReportUtils.attachScreenshot(this.driver, "Failed Screenshot");
        }
        DriverManager.getInstance().quitDriver();
    }

    protected void logStep(String stepDescription) {
        Allure.step(stepDescription);
        Reporter.log(stepDescription, true);
    }

    protected void attachText(String name, String value) {
        AllureReportUtils.attachText(name, value);
    }

    protected void attachScreenshot(String screenshotName) {
        AllureReportUtils.attachScreenshot(this.driver, screenshotName);
    }

    protected WebDriver getDriver() {
        return driver;
    }
}
