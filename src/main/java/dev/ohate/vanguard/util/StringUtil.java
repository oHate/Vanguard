package dev.ohate.vanguard.util;

import java.security.SecureRandom;

public class StringUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String NUMBERS = "0123456789";
    private static final String ALPHABET = NUMBERS + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String repeat(String string, int times) {
        return (new String(new char[times])).replace("\u0000", string);
    }

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }

    public static String randomInt(int length) {
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }

        return builder.toString();
    }

    public static String beautifyString(String string) {
        String[] name = string.toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();

        for (String splitName : name) {
            builder.append(splitName.substring(0, 1)
                            .toUpperCase())
                    .append(splitName.substring(1))
                    .append(" ");
        }

        return builder.toString().trim();
    }

    public static String clampLength(String string, int maxLength) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        if (string.length() <= maxLength) {
            return string;
        }

        return string.substring(0, maxLength);
    }

}
