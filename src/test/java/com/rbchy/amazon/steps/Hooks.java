package com.rbchy.amazon.steps;

import com.rbchy.amazon.support.BotWall;
import com.rbchy.amazon.support.World;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import org.opentest4j.TestAbortedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    private final World world;

    public Hooks(World world) {
        this.world = world;
    }

    /** After every step: if the site answered with a bot wall, stop and report SKIPPED with the reason. */
    @AfterStep
    public void stopAtBotWall() {
        if (!world.hasDriver()) return;
        WebDriver d = world.driver();
        BotWall.detect(d.getCurrentUrl(), d.getTitle(), d.getPageSource()).ifPresent(reason -> {
            throw new TestAbortedException("Blocked by the site's bot protection - " + reason
                    + ". This is the environment, not a defect; run from a normal network to exercise it.");
        });
    }

    @After
    public void tearDown(Scenario scenario) {
        if (!world.hasDriver()) return;
        try {
            if (scenario.isFailed()) {
                byte[] png = ((TakesScreenshot) world.driver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(png, "image/png", "failure");
                scenario.log("URL at failure: " + world.driver().getCurrentUrl());
            }
        } finally {
            world.quit();
        }
    }
}
