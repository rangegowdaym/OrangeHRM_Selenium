package com.testsuites;

import com.common.helpers.GlobalConstants;
import com.common.pages.HomePage;
import com.common.pages.LoginPage;
import com.common.pojo.InvalidCredential;
import com.common.pojo.LoginTestData;
import com.utils.ConfigReader;
import com.utils.JsonUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

@Epic("OrangeHRM Web Application")
@Feature("Authentication")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void navigateToLoginPage() {
        String appUrl = ConfigReader.getString("application.url");
        loginPage = new LoginPage(driver);
        loginPage.launchApplication(appUrl);
        logStep("Navigated to login page: " + appUrl);
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page did not load");
        logStep("Login page verified as loaded");
        attachScreenshot("Login-page-initial-load");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Happy path
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "VALID LOGIN", groups = {"smoke"})
    @Story("Valid login")
    @Description("Verify that a user with valid credentials can log in successfully")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidLogin() {
        String username = ConfigReader.getString("admin.username");
        String password = ConfigReader.getString("admin.password");
        HomePage homePage = new HomePage(driver);

        logStep("Entering valid credentials — username: " + username);
        loginPage.login(username, password);

        logStep("Verifying successful navigation to dashboard");
        Assert.assertTrue(homePage.isPageLoaded(), "Dashboard page did not load after login");
        String currentUrl = driver.getCurrentUrl();
        logStep("Post-login URL: " + currentUrl);
        Assert.assertNotNull(currentUrl);
        Assert.assertTrue(
                currentUrl.contains("/dashboard"),
                "Expected to land on dashboard after login but got: " + currentUrl
        );
        attachScreenshot("Valid-login-screenshot");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Invalid credential scenarios (data-driven)
    // ─────────────────────────────────────────────────────────────────────────

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        String testDataDir = System.getProperty("user.dir") + "/"
                + ConfigReader.getString("testdata.path");
        File jsonFile = new File(testDataDir, "login_test_data.json");

        LoginTestData testData = JsonUtils.fromJson(jsonFile, LoginTestData.class);

        return testData.getInvalidCredentials()
                .stream()
                .map(entry -> new Object[]{
                        entry.username(),
                        entry.password(),
                        entry.scenario()
                })
                .toArray(Object[][]::new);
    }

    @Test(description = "INVALID CREDENTIALS", dataProvider = "invalidCredentials", groups = {"regression"})
    @Story("Invalid credentials")
    @Description("Verify that invalid credentials display the correct error message")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidCredentialsShowError(String username, String password, String scenario) {
        logStep("[" + scenario + "] Attempting login with username: '" + username + "'");
        loginPage.login(username, password);

        logStep("Verifying error message is displayed");
        String actualErrorMessage = loginPage.getErrorMessage();
        attachText("Displayed error message", GlobalConstants.INVALID_CREDENTIALS_ERROR_MESSAGE);

        Assert.assertEquals(
                actualErrorMessage,
                GlobalConstants.INVALID_CREDENTIALS_ERROR_MESSAGE,
                "[" + scenario + "] Expected error message '" + GlobalConstants.INVALID_CREDENTIALS_ERROR_MESSAGE + "' but got: " + actualErrorMessage
        );
        attachScreenshot("Invalid-login-screenshot");
        logStep("[" + scenario + "] Error message correctly displayed: " + actualErrorMessage);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Blank / empty field validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "EMPTY FIELD VALIDATION", groups = {"regression"})
    @Story("Empty field validation")
    @Description("Verify that submitting with both fields empty shows a validation error")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyCredentialsShowError() {
        logStep("Clicking login button with both username and password left empty");
        loginPage.clickLoginButton();

        logStep("Verifying error message is displayed for empty fields");
        List<String> errorMessages = loginPage.getRequiredFieldErrorMessages();
        attachText("Displayed error messages", String.join(", ", errorMessages));

        errorMessages.forEach(errorMessage -> {
            Assert.assertEquals(
                    errorMessage,
                    GlobalConstants.REQUIRED_FIELD_ERROR_MESSAGE,
                    "Expected a validation error for empty field but got: " + errorMessage
            );
        });
        attachScreenshot("Empty-login-screenshot");
        logStep("Validation errors correctly displayed: " + String.join(", ", errorMessages));
    }

    @Test(description = "EMPTY USERNAME VALIDATION", groups = {"regression"})
    @Story("Empty field validation")
    @Description("Verify that submitting with only username empty shows a validation error")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyUsernameShowsError() {
        String password = ConfigReader.getString("admin.password");

        logStep("Entering password only, leaving username blank");
        loginPage.enterCredentials("", password);
        loginPage.clickLoginButton();

        logStep("Verifying error message is displayed for missing username");
        String errorMessage = loginPage.getRequiredFieldErrorMessage();
        attachText("Displayed error message", errorMessage);

        Assert.assertEquals(
                errorMessage,
                GlobalConstants.REQUIRED_FIELD_ERROR_MESSAGE,
                "Expected a validation error for empty username but got: " + errorMessage
        );
        attachScreenshot("Empty-login-screenshot");
        logStep("Validation error correctly displayed: " + errorMessage);
    }

    @Test(description = "EMPTY PASSWORD VALIDATION", groups = {"regression"})
    @Story("Empty field validation")
    @Description("Verify that submitting with only password empty shows a validation error")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyPasswordShowsError() {
        String username = ConfigReader.getString("admin.username");

        logStep("Entering username only, leaving password blank");
        loginPage.enterCredentials(username, "");
        loginPage.clickLoginButton();

        logStep("Verifying error message is displayed for missing password");
        String errorMessage = loginPage.getRequiredFieldErrorMessage();
        attachText("Displayed error message", errorMessage);

        Assert.assertEquals(
                errorMessage,
                GlobalConstants.REQUIRED_FIELD_ERROR_MESSAGE,
                "Expected a validation error for empty username but got: " + errorMessage
        );
        attachScreenshot("Empty-login-screenshot");
        logStep("Validation error correctly displayed: " + errorMessage);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Login page integrity
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "LOGIN PAGE LOAD", groups = {"smoke"})
    @Story("Login page load")
    @Description("Verify that the login page loads correctly with all required elements visible")
    @Severity(SeverityLevel.BLOCKER)
    public void testLoginPageIsLoaded() {
        logStep("Asserting login page is fully loaded with branding visible");
        Assert.assertTrue(loginPage.isPageLoaded(), "OrangeHRM logo / login branding is not visible");
        attachText("Page title", driver.getTitle());
        logStep("Login page load verified successfully");
    }
}
