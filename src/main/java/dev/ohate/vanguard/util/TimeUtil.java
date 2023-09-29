package dev.ohate.vanguard.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class TimeUtil {

    private static final long MILLS_IN_SECOND = 1000L;
    private static final long MILLS_IN_MINUTE = MILLS_IN_SECOND * 60L;
    private static final long MILLS_IN_HOUR = MILLS_IN_MINUTE * 60L;
    private static final long MILLS_IN_DAY = MILLS_IN_HOUR * 24L;
    private static final long MILLS_IN_WEEK = MILLS_IN_DAY * 7L;
    private static final long MILLS_IN_MONTH = MILLS_IN_DAY * 30L;
    private static final long MILLS_IN_YEAR = MILLS_IN_MONTH * 12;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd yyyy hh:mm a zzz");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
    }

    public static String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String dateToString() {
        return dateToString(new Date());
    }

    public static String millisToString(long milliseconds) {
        return dateToString(new Date(milliseconds));
    }

    public static String millisToTimestamp(long milliseconds) {
        return "<t:" + milliseconds / 1000L + ":R>";
    }

}
