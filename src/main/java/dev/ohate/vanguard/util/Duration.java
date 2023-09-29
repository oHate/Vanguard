package dev.ohate.vanguard.util;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Duration {

    private static final Pattern PATTERN = Pattern.compile("\\d+\\D+");

    private final long value;

    private Duration(long value) {
        this.value = value;
    }

    public static Duration fromString(String source) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = PATTERN.matcher(source);

        while (matcher.find()) {
            String group = matcher.group();
            long value = Long.parseLong(group.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = group.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
                case "m" -> {
                    totalTime += value * 60;
                    found = true;
                }
                case "h" -> {
                    totalTime += value * 60 * 60;
                    found = true;
                }
                case "d" -> {
                    totalTime += value * 60 * 60 * 24;
                    found = true;
                }
            }
        }

        return new Duration(!found ? -1 : totalTime * 1000);
    }

    public boolean isInvalid() {
        return value == -1;
    }

}
