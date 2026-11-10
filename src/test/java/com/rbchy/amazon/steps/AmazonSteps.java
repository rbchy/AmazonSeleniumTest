package com.rbchy.amazon.steps;

import com.rbchy.amazon.support.AmazonUrls;
import com.rbchy.amazon.support.World;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmazonSteps {

    private final World world;
    private String query;
    private int low, high;
    private final List<String> applied = new ArrayList<>();

    public AmazonSteps(World world) {
        this.world = world;
    }

    @Given("the user is on the Amazon home page")
    public void openAmazon() {
        world.driver().get(AmazonUrls.HOME);
        world.await().until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.id("twotabsearchtextbox")),
                ExpectedConditions.presenceOfElementLocated(By.id("nav-bb-search")),
                ExpectedConditions.presenceOfElementLocated(By.tagName("form"))));
    }

    @When("the user searches for {string}")
    public void search(String text) {
        query = text;
        List<WebElement> box = world.driver().findElements(By.id("twotabsearchtextbox"));
        if (box.isEmpty()) {
            // Amazon sometimes serves a light home page without the main search box
            world.driver().get(AmazonUrls.search(text));
            return;
        }
        box.get(0).clear();
        box.get(0).sendKeys(text);
        world.driver().findElement(By.id("nav-search-submit-button")).click();
    }

    @And("the user filters by price from {int} to {int} dollars")
    public void priceRange(int low, int high) {
        this.low = low;
        this.high = high;
        world.driver().get(AmazonUrls.withPriceRange(world.driver().getCurrentUrl(), low, high));
    }

    @And("the user selects these filters if available:")
    public void filters(List<String> names) {
        for (String f : names) {
            List<WebElement> link = world.driver().findElements(
                    By.xpath("//div[@id='s-refinements']//li//span[normalize-space()=\"" + f + "\"]/ancestor::a[1]"));
            if (link.isEmpty()) {
                System.out.println("[filter] not offered for this search: " + f);
                continue;
            }
            ((JavascriptExecutor) world.driver()).executeScript("arguments[0].click();", link.get(0));
            try {
                world.await().until(ExpectedConditions.stalenessOf(link.get(0))); // results reload
            } catch (org.openqa.selenium.TimeoutException sameDom) {
                // Amazon sometimes updates in place; carry on
            }
            applied.add(f);
        }
        System.out.println("[filter] applied: " + applied);
    }

    @Then("the results should be for that product")
    public void resultsForProduct() {
        WebElement header = world.await().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("span.a-color-state")));
        assertTrue(header.getText().toLowerCase().contains(query.replace("\"", "").toLowerCase()),
                "results header should echo the search: " + header.getText());
        List<WebElement> titles = world.driver().findElements(
                By.cssSelector("div[data-component-type='s-search-result'] h2"));
        assertFalse(titles.isEmpty(), "at least one search result expected");
    }

    @And("the price filter should be applied")
    public void priceApplied() {
        String url = world.driver().getCurrentUrl();
        assertTrue(url.contains("low-price=" + low) && url.contains("high-price=" + high),
                "price range should be in the URL: " + url);
    }
}
