package com.reports;

import com.ui.driverfactory.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Status;
import io.qameta.allure.util.ResultsUtils;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AllureTestNgListener implements ITestListener, IInvokedMethodListener {

    @Override
    public void onTestFailure(ITestResult result) {
        attachMetadata(result);
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            AllureReportUtils.attachStackTrace("Failure stack trace", throwable);
        }
        WebDriver driver = DriverManager.getInstance().getCurrentDriver();
        if (driver != null) {
            AllureReportUtils.attachScreenshot(driver, "Failure screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        attachMetadata(result);
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            AllureReportUtils.attachStackTrace("Skip reason", throwable);
        }
    }

    /*@Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        attachMetadata(result);
    }*/

    @Override
    public void onStart(ITestContext context) {
        AllureReportUtils.attachText("TestNG context", context.getName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod()) {
            return;
        }

        customiseSuiteTree(testResult);

       /* String stepName = testResult.getTestClass().getName() + "." + testResult.getMethod().getMethodName();
        Allure.step("TDD step executed: " + stepName, mapStatus(testResult.getStatus()));*/

        List<String> logLines = Reporter.getOutput(testResult);
        if (logLines != null && !logLines.isEmpty()) {
            AllureReportUtils.attachText("TDD step logs: " + testResult.getMethod().getDescription(),
                    String.join(System.lineSeparator(), logLines));
        }
    }

    private void customiseSuiteTree(ITestResult testResult) {
        String simpleClassName = testResult.getTestClass().getRealClass().getSimpleName();

        Allure.getLifecycle().updateTestCase(tc -> {
            String parentSuite = labelValue(tc.getLabels(), ResultsUtils.PARENT_SUITE_LABEL_NAME);
            String suite = labelValue(tc.getLabels(), ResultsUtils.SUITE_LABEL_NAME);

            List<Label> labels = tc.getLabels().stream()
                    .filter(label -> !ResultsUtils.SUB_SUITE_LABEL_NAME.equals(label.getName()))
                    .collect(Collectors.toList());
            labels.add(ResultsUtils.createSubSuiteLabel(simpleClassName));
            tc.setLabels(labels);

            List<String> titlePath = new ArrayList<>();
            /*if (parentSuite != null) {
                titlePath.add(parentSuite);
            }*/
            if (suite != null) {
                titlePath.add(suite);
            }
            titlePath.add(simpleClassName);
            tc.setTitlePath(titlePath);
        });
    }

    private String labelValue(List<Label> labels, String name) {
        return labels.stream()
                .filter(label -> name.equals(label.getName()))
                .map(Label::getValue)
                .findFirst()
                .orElse(null);
    }

    private void attachMetadata(ITestResult result) {
        String className = result.getTestClass() == null
                ? "unknown"
                : result.getTestClass().getName();
        AllureReportUtils.attachText("Test class", className);
        AllureReportUtils.attachText("Test method", result.getMethod().getDescription());
        AllureReportUtils.attachText("Parameters", Arrays.toString(result.getParameters()));
    }

    private Status mapStatus(int testNgStatus) {
        return switch (testNgStatus) {
            case ITestResult.SUCCESS -> Status.PASSED;
            case ITestResult.FAILURE -> Status.FAILED;
            case ITestResult.SKIP -> Status.SKIPPED;
            default -> Status.BROKEN;
        };
    }
}
