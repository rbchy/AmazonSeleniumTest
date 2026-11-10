package com.rbchy.amazon.support;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds Amazon search URLs. The price-range filter is applied through the URL
 * ({@code low-price} / {@code high-price}) because the on-page price inputs are unreliable.
 *
 * <p>The old code did {@code currentUrl + "&low-price=400&high-price=2500"} only when the URL
 * did not already contain "low-price": a URL without a query string got a broken "&" path,
 * a fragment (#...) swallowed the parameters, and a different range could never be applied.
 */
public final class AmazonUrls {

    public static final String HOME = "https://www.amazon.com/";

    private AmazonUrls() {
    }

    public static String search(String query) {
        return HOME + "s?k=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    /** Returns the URL with low-price / high-price set (replacing any previous range). */
    public static String withPriceRange(String url, int low, int high) {
        if (low < 0 || high < low) {
            throw new IllegalArgumentException("price range must satisfy 0 <= low <= high, got " + low + ".." + high);
        }
        String fragment = "";
        int hash = url.indexOf('#');
        if (hash >= 0) {
            fragment = url.substring(hash);
            url = url.substring(0, hash);
        }
        String base = url;
        Map<String, String> params = new LinkedHashMap<>();
        int q = url.indexOf('?');
        if (q >= 0) {
            base = url.substring(0, q);
            for (String pair : url.substring(q + 1).split("&")) {
                if (pair.isEmpty()) continue;
                int eq = pair.indexOf('=');
                String key = eq < 0 ? pair : pair.substring(0, eq);
                params.put(key, eq < 0 ? "" : pair.substring(eq + 1));
            }
        }
        params.put("low-price", String.valueOf(low));
        params.put("high-price", String.valueOf(high));
        StringBuilder sb = new StringBuilder(base).append('?');
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) sb.append('&');
            sb.append(e.getKey()).append('=').append(e.getValue());
            first = false;
        }
        return sb.append(fragment).toString();
    }

    /** Parses a displayed Amazon price such as "$1,049.99" into a number; null when it is not a price. */
    public static Double parsePrice(String text) {
        if (text == null) return null;
        String digits = text.replaceAll("[^0-9.]", "");
        if (!digits.matches("\\d+(\\.\\d+)?")) return null; // e.g. "." left over from "unavailable."
        return Double.valueOf(digits);
    }
}
