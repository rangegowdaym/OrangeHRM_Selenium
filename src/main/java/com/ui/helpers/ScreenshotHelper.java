package com.ui.helpers;

import com.reports.AllureReportUtils;
import com.utils.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotHelper {
    private final TakesScreenshot screenshotTaker;
    private static final File SCREENSHOT_PATH = new File(System.getProperty("user.dir"),
            ConfigReader.getString("screenshot.path"));

    public ScreenshotHelper(WebDriver driver) {
        this.screenshotTaker = (TakesScreenshot) driver;
    }

    public byte[] getScreenshotAsByteArray() {
        return screenshotTaker.getScreenshotAs(OutputType.BYTES);
    }

    public BufferedImage getScreenshotAsBufferedImage() {
        try {
            File screenshotFile = takeScreenShots("screenshot");
            return ImageIO.read(screenshotFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture screenshot as BufferedImage", e);
        }
    }

    public File takeScreenShots(String picture) {
        try {
            if (!SCREENSHOT_PATH.exists() && !SCREENSHOT_PATH.mkdirs()) {
                throw new IOException("Failed to create screenshot directory: " + SCREENSHOT_PATH.getAbsolutePath());
            }
            File temp = screenshotTaker.getScreenshotAs(OutputType.FILE);
            File savedFile = new File(SCREENSHOT_PATH, picture);
            FileUtils.copyFile(temp, savedFile);
            return savedFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }

    public void attachScreenshot(String attachmentName) {
        AllureReportUtils.attachBytes(attachmentName, screenshotTaker.getScreenshotAs(OutputType.BYTES), "image/png", ".png");
    }

    public void attachScreenshot() {
        attachScreenshot("Screenshot");
    }
}