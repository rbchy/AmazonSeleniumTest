package Steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class GoogleSearch {
    
    WebDriver driver;
    WebDriverWait wait;

    @Given("browser is open")
    public void browser_is_open() {
        System.out.println("Step 1: Opening Browser");
        driver = new ChromeDriver();
        
        // ডাইনামিক ওয়েট সেট করা
        wait = new WebDriverWait(driver, Duration.ofSeconds(200));
        
        driver.manage().window().maximize();
    }

    @And("the user is on google search page")
    public void the_user_is_on_google_search_page() {
        System.out.println("Step 2: Navigating to Google");
        driver.get("https://www.google.com");
    }

    @When("the user enters a text in the search box")
    public void the_user_enters_a_text_in_the_search_box() {
        System.out.println("Step 3: Entering search query");
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Cucumber Java");
    }

    @And("the user hits enter")
    public void the_user_hits_enter() {
        System.out.println("Step 4: Pressing Enter");
        driver.findElement(By.name("q")).sendKeys(Keys.ENTER);
    }

    @Then("the user should be navigated to search result")
    public void the_user_should_be_navigated_to_search_result() {
        System.out.println("Step 5: Verifying Title");
        
        try {
            // টাইটেলটি আপডেট হওয়া পর্যন্ত ১০ সেকেন্ড অপেক্ষা করবে
            wait.until(ExpectedConditions.titleContains("Cucumber Java"));
            
            String actualTitle = driver.getTitle();
            System.out.println("Actual Title found: " + actualTitle);

            // ভেরিফিকেশন লজিক
            if (actualTitle.toLowerCase().contains("cucumber java")) {
                System.out.println("RESULT: PASSED - Title matches correctly!");
            } else {
                System.out.println("RESULT: FAILED - Title mismatch.");
            }
        } catch (Exception e) {
            System.out.println("RESULT: FAILED - Timeout waiting for title.");
        } finally {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}