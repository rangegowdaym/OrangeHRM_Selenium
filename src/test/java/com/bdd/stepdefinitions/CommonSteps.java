package com.bdd.stepdefinitions;

import com.common.helpers.TestContext;
import com.ui.driverfactory.DriverManager;
import com.utils.LoggerUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class CommonSteps {
    private final TestContext testContext;
    private static final Logger logger = LoggerUtils.getLogger(CommonSteps.class);
    private final WebDriver driver;

    public CommonSteps(TestContext testContext) {
        this.testContext = testContext;
        this.testContext.setBrowser();
        this.testContext.setBrowserVersion();
        this.testContext.setPlatform();
        testContext.setDriver(DriverManager.getInstance()
                .getDriver(testContext.getPlatform(), testContext.getBrowser(), testContext.getBrowserVersion())
        );
        this.driver = testContext.getDriver();
        testContext.setScreenshotHelper(testContext.getDriver());
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        testContext.setScenario(scenario);
        System.out.println("Starting scenario: " + testContext.getScenario().getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario != null && scenario.isFailed()) {
            testContext.getScenario().attach(testContext.getScreenshotHelper().getScreenshotAsByteArray(), "image/png", "screenshot");
        }
        System.out.println("Finished scenario: " + testContext.getScenario().getName());
        if (driver != null) {
            DriverManager.getInstance().quitDriver();
        }
    }
}