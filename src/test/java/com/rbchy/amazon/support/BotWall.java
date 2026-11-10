package com.rbchy.amazon.support;

import java.util.Locale;
import java.util.Optional;

/**
 * Recognises the pages big sites show to automated browsers instead of the real content.
 * When a test lands on one, the scenario is reported as SKIPPED with the reason: it says
 * nothing about the site under test, and the framework does not try to get around it.
 */
public final class BotWall {

    private BotWall() {
    }

    public static Optional<String> detect(String url, String title, String pageSource) {
        String u = lower(url), t = lower(title), s = lower(pageSource);

        // Amazon robot check
        if (s.contains("enter the characters you see below") || s.contains("/errors/validatecaptcha")
                || t.contains("robot check")) {
            return Optional.of("Amazon robot check (captcha)");
        }
        if (s.contains("to discuss automated access to amazon data")) {
            return Optional.of("Amazon automated-access block page");
        }
        // Google "unusual traffic" interstitial
        if (u.contains("google.") && u.contains("/sorry/")) {
            return Optional.of("Google 'unusual traffic' check (/sorry/)");
        }
        if (s.contains("our systems have detected unusual traffic")) {
            return Optional.of("Google 'unusual traffic' check");
        }
        // Google sign-in refusing automated browsers
        if (s.contains("this browser or app may not be secure") || s.contains("couldn’t sign you in")
                || s.contains("couldn't sign you in")) {
            return Optional.of("Google sign-in refuses automated browsers");
        }
        // CDN block pages
        if (t.contains("access denied") || t.equals("403 forbidden")) {
            return Optional.of("CDN 'Access Denied' page");
        }
        return Optional.empty();
    }

    private static String lower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }
}
