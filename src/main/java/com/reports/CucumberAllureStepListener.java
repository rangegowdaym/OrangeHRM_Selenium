package com.reports;

import com.ui.driverfactory.DriverManager;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;

public class CucumberAllureStepListener implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
    }

    private void onStepFinished(TestStepFinished event) {
        TestStep testStep = event.getTestStep();
        if (!(testStep instanceof PickleStepTestStep pickleStepTestStep)) {
            return;
        }

        String stepLabel = pickleStepTestStep.getStep().getKeyword() + pickleStepTestStep.getStep().getText();
        Status cucumberStatus = event.getResult().getStatus();

        Allure.step("BDD step executed: " + stepLabel, mapStatus(cucumberStatus));

        if (cucumberStatus == Status.FAILED) {
            Throwable error = event.getResult().getError();
            if (error != null) {
                AllureReportUtils.attachStackTrace("BDD step failure", error);
            }
            WebDriver driver = DriverManager.getInstance().getCurrentDriver();
            if (driver != null) {
                AllureReportUtils.attachScreenshot(driver, "BDD failed step screenshot");
            }
        }
    }

    private io.qameta.allure.model.Status mapStatus(Status cucumberStatus) {
        return switch (cucumberStatus) {
            case PASSED -> io.qameta.allure.model.Status.PASSED;
            case FAILED -> io.qameta.allure.model.Status.FAILED;
            case SKIPPED, PENDING -> io.qameta.allure.model.Status.SKIPPED;
            case AMBIGUOUS, UNDEFINED -> io.qameta.allure.model.Status.BROKEN;
            default -> io.qameta.allure.model.Status.BROKEN;
        };
    }
}
