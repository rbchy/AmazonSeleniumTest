package com.rbchy.amazon.support;

import java.util.Optional;

/** Account details come only from the environment - never from the source code. */
public final class Credentials {

    public final String username;
    public final String password;

    private Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /** Reads {@code <prefix>_USER} and {@code <prefix>_PASSWORD}; empty when either is missing. */
    public static Optional<Credentials> fromEnv(String prefix) {
        return from(System.getenv(prefix + "_USER"), System.getenv(prefix + "_PASSWORD"));
    }

    static Optional<Credentials> from(String user, String password) {
        if (user == null || user.isBlank() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Credentials(user.trim(), password));
    }

    @Override
    public String toString() {
        return "Credentials[" + username + ", password=***]";
    }
}
