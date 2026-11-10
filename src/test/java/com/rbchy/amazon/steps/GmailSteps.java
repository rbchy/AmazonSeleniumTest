package com.rbchy.amazon.steps;

import com.rbchy.amazon.support.Credentials;
import com.rbchy.amazon.support.World;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.opentest4j.TestAbortedException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Logs in to a real Gmail account. Tagged @manual: it needs a real account and Google refuses
 * sign-in from automated browsers, so it is not part of CI. Credentials come only from the
 * environment variables GMAIL_USER / GMAIL_PASSWORD - they used to be hard-coded here.
 */
public class GmailSteps {

    private final World world;
    private Credentials credentials;

    public GmailSteps(World world) {
        this.world = world;
    }

    @When("the user navigates to Gmail login page")
    public void openGmail() {
        credentials = Credentials.fromEnv("GMAIL").orElseThrow(() -> new TestAbortedException(
                "Set GMAIL_USER and GMAIL_PASSWORD to run the Gmail scenario"));
        world.driver().get("https://mail.google.com/");
    }

    @And("the user logs in with valid credentials")
    public void logIn() {
        world.await().until(ExpectedConditions.elementToBeClickable(By.id("identifierId"))).sendKeys(credentials.username);
        world.driver().findElement(By.id("identifierNext")).click();
        world.await().until(ExpectedConditions.visibilityOfElementLocated(By.name("Passwd"))).sendKeys(credentials.password);
        world.driver().findElement(By.id("passwordNext")).click();
    }

    @Then("the user should be on the Inbox page")
    public void onInbox() {
        world.await().until(ExpectedConditions.urlContains("#inbox"));
    }

    @And("the Compose button should be visible")
    public void composeVisible() {
        // The old step only closed the browser; it never looked for the button.
        assertTrue(world.await().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='button' and normalize-space()='Compose']"))).isDisplayed());
    }
}
