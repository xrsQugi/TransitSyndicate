package com.transitsyndicate.core.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.transitsyndicate.R;

public final class GameNotification {

    public enum Type { INFO, SUCCESS, ERROR }

    private static final long ANIM_IN_MS  = 250;
    private static final long HOLD_MS     = 2800;
    private static final long ANIM_OUT_MS = 300;

    public static void show(ViewGroup host, String message, Type type) {
        Context ctx = host.getContext();
        View card = LayoutInflater.from(ctx).inflate(R.layout.view_game_notification, host, false);

        TextView tvText = card.findViewById(R.id.notification_text);
        TextView tvIcon = card.findViewById(R.id.notification_icon);
        View accent    = card.findViewById(R.id.notification_accent);

        tvText.setText(message);

        int accentColor;
        String icon;
        switch (type) {
            case SUCCESS:
                accentColor = ContextCompat.getColor(ctx, R.color.ts_green);
                icon = "✓";
                break;
            case ERROR:
                accentColor = ContextCompat.getColor(ctx, R.color.ts_red);
                icon = "✕";
                break;
            default:
                accentColor = ContextCompat.getColor(ctx, R.color.ts_purple);
                icon = "ℹ";
                break;
        }
        accent.setBackgroundColor(accentColor);
        tvIcon.setText(icon);
        tvIcon.setTextColor(accentColor);

        card.setAlpha(0f);
        card.setTranslationY(40f);

        host.addView(card);

        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIM_IN_MS)
                .withEndAction(() ->
                        card.postDelayed(() ->
                                card.animate()
                                        .alpha(0f)
                                        .translationY(20f)
                                        .setDuration(ANIM_OUT_MS)
                                        .withEndAction(() -> host.removeView(card))
                                        .start(),
                                HOLD_MS))
                .start();
    }

    public static void info(ViewGroup host, String message) {
        show(host, message, Type.INFO);
    }

    public static void success(ViewGroup host, String message) {
        show(host, message, Type.SUCCESS);
    }

    public static void error(ViewGroup host, String message) {
        show(host, message, Type.ERROR);
    }
}
