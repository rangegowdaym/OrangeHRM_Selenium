package com.ui.driverfactory;

import com.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverManager {
    private static DriverManager instance;
    private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private final ThreadLocal<DriverContext> context = new ThreadLocal<>();

    private DriverManager() {
    }

    public static synchronized DriverManager getInstance() {
        if (instance == null) instance = new DriverManager();
        return instance;
    }

    public WebDriver getDriver() {
        if (driver.get() == null) driver.set(createDriver(resolveContext()));
        return driver.get();
    }

    public WebDriver getCurrentDriver() {
        return driver.get();
    }

    public WebDriver getDriver(String platform, String browser, String browserVersion) {
        setDriverContext(platform, browser, browserVersion);
        return getDriver();
    }

    public void setDriverContext(String platform, String browser, String browserVersion) {
        context.set(new DriverContext(platform, browser, browserVersion));
    }

    public void clearDriverContext() {
        context.remove();
    }

    public void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driver.remove();
        }
        context.remove();
    }

    private WebDriver createDriver(DriverContext driverContext) {
        String env = firstNonBlank(driverContext.platform(), System.getProperty("platform", "local")).toUpperCase();
        String browser = firstNonBlank(driverContext.browser(), System.getProperty("browser", "chrome")).toUpperCase();
        BrowserType browserType = BrowserType.valueOf(browser);

        return switch (EnvironmentType.valueOf(env)) {
            case SAUCE_LABS, BROWSER_STACK -> createRemoteDriver(env.toLowerCase(), browserType);
            case GRID -> createGridDriver(browserType);
            default -> createLocalDriver(browserType);
        };
    }

    private WebDriver createLocalDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME -> {
                // options = new ChromeOptions();
                //options.addArguments("--user-data-dir=/tmp/chrome-profile-" + UUID.randomUUID(), "--start-maximized");
                return new ChromeDriver();
            }
            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--start-maximized");
                return new FirefoxDriver(options);
            }
            case SAFARI -> {
                return new SafariDriver(new SafariOptions());
            }
            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--start-maximized");
                return new EdgeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    private WebDriver createRemoteDriver(String provider, BrowserType browserType) {
        try {
            AbstractDriverOptions<?> options = createRemoteOptions(provider, browserType);
            String url = buildRemoteUrl(provider);
            return new RemoteWebDriver(new URL(url), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote URL", e);
        }
    }

    private WebDriver createGridDriver(BrowserType browserType) {
        try {
            AbstractDriverOptions<?> options = createRemoteOptions("grid", browserType);
            String remoteUrl = firstNonBlank(
                    System.getProperty("seleniumRemoteUrl"),
                    ConfigReader.getString("selenium.remote.url"),
                    "http://localhost:4444/wd/hub"
            );
            return new RemoteWebDriver(new URL(remoteUrl), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL", e);
        }
    }

    private AbstractDriverOptions<?> createRemoteOptions(String provider, BrowserType browserType) {
        AbstractDriverOptions<?> options = switch (browserType) {
            case CHROME -> new ChromeOptions();
            case FIREFOX -> new FirefoxOptions();
            case SAFARI -> new SafariOptions();
            case EDGE -> new EdgeOptions();
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        };

        String browserVersion = firstNonBlank(
                resolveContext().browserVersion(),
                System.getProperty("browserVersion"),
                ConfigReader.getString(provider + ".browserVersion." + browserType.getBrowserName()),
                ConfigReader.getString(provider + ".browserVersion")
        );
        if (browserVersion != null) {
            options.setBrowserVersion(browserVersion);
        }

        String platform = ConfigReader.getString(provider + ".platform");
        if (platform != null && !platform.isBlank()) {
            options.setPlatformName(platform);
        }
        String testName = ConfigReader.getString(provider + ".testName");
        if (testName != null && !testName.isBlank()) {
            options.setCapability("name", testName);
        }
        return options;
    }

    private DriverContext resolveContext() {
        DriverContext driverContext = context.get();
        return driverContext == null ? DriverContext.empty() : driverContext;
    }

    private String buildRemoteUrl(String provider) {
        String username = ConfigReader.getString(provider + ".username");
        String accessKey = ConfigReader.getString(provider + ".accessKey");
        String urlBase = provider.equals("sauce") ? "ondemand.saucelabs.com" : "hub-cloud.browserstack.com";
        return "https://" + username + ":" + accessKey + "@" + urlBase + "/wd/hub";
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private record DriverContext(String platform, String browser, String browserVersion) {
        private static DriverContext empty() {
            return new DriverContext(null, null, null);
        }
    }
}