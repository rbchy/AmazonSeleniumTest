package com.rbchy.amazon.unit;

import com.rbchy.amazon.support.Credentials;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    @Test
    void missingEnvironmentMeansNoCredentials() {
        assertTrue(Credentials.fromEnv("NO_SUCH_PREFIX_" + System.nanoTime()).isEmpty());
    }

    @Test
    void toStringNeverShowsThePassword() throws Exception {
        var m = Credentials.class.getDeclaredMethod("from", String.class, String.class);
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        var c = (java.util.Optional<Credentials>) m.invoke(null, "qa@example.com", "s3cret!");
        assertEquals("Credentials[qa@example.com, password=***]", c.orElseThrow().toString());
        assertTrue(((java.util.Optional<?>) m.invoke(null, " ", "x")).isEmpty());
        assertTrue(((java.util.Optional<?>) m.invoke(null, "a@b.c", "")).isEmpty());
    }

    /** Guard rail: no password or e-mail address may be committed in the test code again. */
    @Test
    void noCredentialsInTheSourceTree() throws Exception {
        try (Stream<Path> files = Files.walk(Path.of("src"))) {
            files.filter(p -> p.toString().endsWith(".java") || p.toString().endsWith(".feature")
                            || p.toString().endsWith(".properties"))
                    .filter(p -> !p.toString().endsWith("CredentialsTest.java"))
                    .forEach(p -> {
                        String s;
                        try {
                            s = Files.readString(p);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        assertFalse(s.matches("(?s).*[A-Za-z0-9._%+-]+@gmail\\.com.*"), p + " contains a Gmail address");
                        assertFalse(s.matches("(?s).*String\\s+password\\s*=\\s*\"[^\"]+\".*"), p + " contains a hard-coded password");
                    });
        }
    }
}
