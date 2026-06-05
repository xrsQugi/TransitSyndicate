package com.transitsyndicate.core.utils;

import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.widget.TextView;

public final class BadgeUtils {

    private BadgeUtils() {}

    public static void set(TextView tv, String text, int textColor, int bgColor) {
        tv.setText(text);
        tv.setTextColor(textColor);
        DisplayMetrics dm = tv.getContext().getResources().getDisplayMetrics();
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(20 * dm.density);
        gd.setColor(bgColor);
        tv.setBackground(gd);
    }
}
