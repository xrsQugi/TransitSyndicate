package com.transitsyndicate.core.utils;

import java.util.Locale;

public final class MoneyFormatter {

    private MoneyFormatter() {}

    public static String format(long amount) {
        if (amount >= 1_000_000L) {
            return String.format(Locale.US, "%.1fM", amount / 1_000_000.0);
        }
        if (amount >= 1_000L) {
            return String.format(Locale.US, "%.1fK", amount / 1_000.0);
        }
        return String.valueOf(amount);
    }

    public static String formatWithSymbol(long amount) {
        return "$" + format(amount);
    }
}
