package com.rbchy.amazon.steps;

import com.rbchy.amazon.support.World;
import io.cucumber.java.en.Given;

public class CommonSteps {

    private final World world;

    public CommonSteps(World world) {
        this.world = world;
    }

    @Given("browser is open")
    public void browserIsOpen() {
        world.driver();
    }
}
