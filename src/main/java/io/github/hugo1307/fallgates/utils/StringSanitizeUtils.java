package io.github.hugo1307.fallgates.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringSanitizeUtils {

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[^a-zA-Z0-9_\\-]", "_").trim();
    }

}
