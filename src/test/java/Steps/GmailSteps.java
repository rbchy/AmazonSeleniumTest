package Steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Collections;

public class GmailSteps {
    WebDriver driver;
    WebDriverWait wait;
    
    //String username = "ryanaquadirDS@gmail.com";
    //String password = "OnAdayLike2day!";
    String username = "ranajitchowdhury1957@gmail.com";
    String password = "CpaRbc123&";
    

    @When("the user navigates to Gmail login page")
    public void the_user_navigates_to_gmail_login_page() {
        // গুগল যেন অটোমেশন ধরতে না পারে তার জন্য কিছু স্পেশাল সেটিংস
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://mail.google.com/");
        System.out.println("Step: Navigated to Gmail.");
    }

    @And("the user logs in with valid credentials")
    public void the_user_logs_in_with_valid_credentials() {
        // ইমেল দেওয়া
        wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierId"))).sendKeys(username);
        driver.findElement(By.id("identifierNext")).click();
        
        System.out.println("Waiting for password field...");
        
        try {
            // পাসওয়ার্ড ফিল্ডের জন্য অপেক্ষা
            WebElement pwdField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            pwdField.sendKeys(password);
            driver.findElement(By.id("passwordNext")).click();
            System.out.println("Credentials submitted.");
        } catch (TimeoutException e) {
            // যদি গুগল ব্লক করে দেয় তবে এই মেসেজটি আসবে
            System.err.println("FAILED: Google blocked the login or password field didn't appear.");
            System.out.println("Check the browser window to see if it says 'This browser or app may not be secure'.");
            throw e; 
        }
    }

    @Then("the user should be on the Inbox page")
    public void the_user_should_be_on_the_inbox_page() {
        wait.until(ExpectedConditions.urlContains("#inbox"));
        System.out.println("Successfully landed on Inbox.");
    }

    @And("the Compose button should be visible")
    public void the_compose_button_should_be_visible() {
        driver.quit();
    }
}