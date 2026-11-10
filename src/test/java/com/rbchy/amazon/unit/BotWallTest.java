package com.rbchy.amazon.unit;

import com.rbchy.amazon.support.BotWall;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotWallTest {

    @Test
    void amazonCaptchaPage() {
        assertEquals("Amazon robot check (captcha)", BotWall.detect("https://www.amazon.com/errors/validateCaptcha",
                "Amazon.com", "<p>Enter the characters you see below</p>").orElseThrow());
    }

    @Test
    void amazonAutomatedAccessPage() {
        assertTrue(BotWall.detect("https://www.amazon.com/", "Sorry!",
                "To discuss automated access to Amazon data please contact api-services-support@amazon.com").isPresent());
    }

    @Test
    void googleUnusualTraffic() {
        assertTrue(BotWall.detect("https://www.google.com/sorry/index?continue=...", "", "").isPresent());
        assertTrue(BotWall.detect("https://www.google.com/search?q=x", "",
                "Our systems have detected unusual traffic from your computer network").isPresent());
    }

    @Test
    void googleSignInRefusal() {
        assertTrue(BotWall.detect("https://accounts.google.com/", "Sign in",
                "Couldn’t sign you in. This browser or app may not be secure.").isPresent());
    }

    @Test
    void cdnAccessDenied() {
        assertTrue(BotWall.detect("https://example.com", "Access Denied", "").isPresent());
    }

    @Test
    void normalPagesAreNotBotWalls() {
        assertTrue(BotWall.detect("https://www.amazon.com/s?k=pixel", "Amazon.com : pixel",
                "<span class='a-color-state'>\"pixel\"</span>").isEmpty());
        assertTrue(BotWall.detect("https://www.google.com/search?q=Cucumber+Java", "Cucumber Java - Google Search",
                "<h3>Cucumber</h3>").isEmpty());
        assertTrue(BotWall.detect(null, null, null).isEmpty());
    }
}
