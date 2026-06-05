package com.transitsyndicate.core.utils;

public final class TimeUtils {

    private static final long TICKS_PER_MINUTE = 60L;
    private static final long TICKS_PER_HOUR = 3_600L;

    private TimeUtils() {}

    public static String formatTicksAsTime(long ticks) {
        if (ticks < TICKS_PER_MINUTE) {
            return ticks + "s";
        }
        if (ticks < TICKS_PER_HOUR) {
            return (ticks / TICKS_PER_MINUTE) + "m";
        }
        return (ticks / TICKS_PER_HOUR) + "h";
    }

    public static long minutesToTicks(int minutes) {
        return (long) minutes * TICKS_PER_MINUTE;
    }

    public static long hoursToTicks(int hours) {
        return (long) hours * TICKS_PER_HOUR;
    }
}
