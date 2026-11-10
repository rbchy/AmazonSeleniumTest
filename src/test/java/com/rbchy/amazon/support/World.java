package com.rbchy.amazon.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * One browser per scenario, shared by every step class through PicoContainer.
 * (Before, GoogleSearch and GmailSteps each had their own driver field, so the Gmail
 * scenario's "browser is open" step opened a Chrome window nobody used or closed.)
 */
public class World {

    private WebDriver driver;
    private WebDriverWait wait;

    public WebDriver driver() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            if (Boolean.parseBoolean(System.getProperty("headless", System.getenv().getOrDefault("CI", "false")))) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1366,900", "--lang=en-US");
            driver = new ChromeDriver(options); // Selenium Manager resolves the matching chromedriver
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        }
        return driver;
    }

    public WebDriverWait await() {
        driver();
        return wait;
    }

    public boolean hasDriver() {
        return driver != null;
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
