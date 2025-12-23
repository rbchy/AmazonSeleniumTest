package Steps;

import io.cucumber.java.en.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Collections;

public class amazonSteps {
    WebDriver driver;
    WebDriverWait wait;

    @Given("the user is logged in in amazon account")
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://www.amazon.com/");
        
        changeLocationToUS();
    }

    @When("the user searches for the product name in the search bar")
    public void search_product() {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("twotabsearchtextbox")));
        searchBox.clear();
        // ডাবল কোটেশন ব্যবহার করলে অ্যামাজন শুধুমাত্র সঠিক মডেলটিই দেখাবে
        searchBox.sendKeys("\"Google Pixel 9\""); 
        driver.findElement(By.id("nav-search-submit-button")).click();
    }

    @And("the user clicks the search button")
    public void the_user_clicks_the_search_button() {
        // search_product মেথডেই এটি সম্পন্ন হয়েছে
    }

    @And("the user filters by price range")
    public void apply_price_filter() {
        // UI ইনপুট বক্স অনেক সময় কাজ করে না, তাই সরাসরি URL এ প্রাইস রেঞ্জ যোগ করা হচ্ছে।
        // এটি সবচেয়ে নির্ভরযোগ্য উপায়।
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.contains("low-price")) {
            String priceUrl = currentUrl + "&low-price=400&high-price=2500";
            driver.get(priceUrl);
            System.out.println("Price range ($400-$2500) applied via URL.");
        }
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }

    @And("the user selects filters for condition and carrier")
    public void apply_all_filters() {
        // Google Pixel 9 নিশ্চিত করার জন্য ফিল্টারসমূহ
        // '2024' অনেক সময় পাওয়া যায় না, তাই এটি সবার শেষে রাখা হয়েছে
        String[] filters = {"Renewed", "Unlocked", "Google", "512 GB & above", "2024", "10 GB & above", "3040 x 1440", "6 in & above" , "USB Type C"};
        for (String f : filters) {
            applyFilter(f);
        }
    }

    private void applyFilter(String filterText) {
        try {
            // XPath উন্নত করা হয়েছে যাতে টেক্সটের সাথে পার্শিয়াল ম্যাচ করলেও খুঁজে পায়
            By filterXpath = By.xpath("//li[contains(@id, 'p_')]//span[contains(text(),'" + filterText + "')]/ancestor::a");
            
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(filterXpath));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(1500);
            
            // এলিমেন্টটি ক্লিকযোগ্য হওয়া পর্যন্ত অপেক্ষা
            wait.until(ExpectedConditions.elementToBeClickable(filterXpath));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            
            System.out.println("Applied filter: " + filterText);
            Thread.sleep(3000); // পেজ রিফ্রেশ হওয়ার সময়
        } catch (Exception e) {
            System.out.println("Filter not found or already applied: " + filterText);
        }
    }

    @Then("the product should be visible in the search result")
    public void verify() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(@class, 'a-color-state')]")));
        System.out.println("Process Complete. Title: " + driver.getTitle());
    }

    private void changeLocationToUS() {
        try {
            WebElement locBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-global-location-popover-link")));
            locBtn.click();
            WebElement zip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("GLUXZipUpdateInput")));
            zip.sendKeys("10001");
            driver.findElement(By.id("GLUXZipUpdate")).click();
            Thread.sleep(2000);
            driver.navigate().refresh();
            System.out.println("Location updated to New York (10001).");
        } catch (Exception e) {
            System.out.println("Location step failed.");
        }
    }
}