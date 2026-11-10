package com.rbchy.amazon.steps;

import com.rbchy.amazon.support.World;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoogleSearchSteps {

    private final World world;
    private String query;

    public GoogleSearchSteps(World world) {
        this.world = world;
    }

    @And("the user is on google search page")
    public void onGoogle() {
        world.driver().get("https://www.google.com/ncr?hl=en");
    }

    @When("the user enters {string} in the search box")
    public void typeQuery(String text) {
        query = text;
        WebElement box = world.await().until(ExpectedConditions.elementToBeClickable(By.name("q")));
        box.clear();
        box.sendKeys(text);
    }

    @And("the user hits enter")
    public void hitEnter() {
        world.driver().findElement(By.name("q")).sendKeys(Keys.ENTER);
    }

    @Then("the user should be navigated to search result")
    public void onResults() {
        // The old step caught every exception and only printed "FAILED", so it could never fail.
        world.await().until(ExpectedConditions.titleContains(query));
        assertTrue(world.driver().getTitle().toLowerCase().contains(query.toLowerCase()),
                "results page title should contain the query: " + world.driver().getTitle());
        List<WebElement> results = world.driver().findElements(By.cssSelector("#search a h3"));
        assertFalse(results.isEmpty(), "at least one organic result expected");
    }
}
