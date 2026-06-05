package com.transitsyndicate.data.local.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class GamePreferences {

    private static final String PREFS_NAME              = "transit_syndicate_prefs";
    private static final String KEY_FIRST_LAUNCH        = "first_launch";
    private static final String KEY_GAME_TICK           = "game_tick";
    private static final String KEY_SOUND               = "sound_enabled";
    private static final String KEY_ONBOARDING          = "onboarding_done";
    private static final String KEY_AUTO_DISPATCH       = "auto_dispatch_enabled";
    private static final String KEY_AUTO_DISPATCH_CARGO = "auto_dispatch_cargo";

    private final SharedPreferences prefs;

    public GamePreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    public long getGameTick() {
        return prefs.getLong(KEY_GAME_TICK, 0L);
    }

    public void saveGameTick(long tick) {
        prefs.edit().putLong(KEY_GAME_TICK, tick).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply();
    }

    public boolean isOnboardingDone() {
        return prefs.getBoolean(KEY_ONBOARDING, false);
    }

    public void setOnboardingDone() {
        prefs.edit().putBoolean(KEY_ONBOARDING, true).apply();
    }

    public boolean isAutoDispatchEnabled() {
        return prefs.getBoolean(KEY_AUTO_DISPATCH, true);
    }

    public void setAutoDispatchEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_DISPATCH, enabled).apply();
    }

    public Set<String> getAutoDispatchCargo() {
        return prefs.getStringSet(KEY_AUTO_DISPATCH_CARGO, new HashSet<>());
    }

    public void setAutoDispatchCargo(Set<String> cargoNames) {
        prefs.edit().putStringSet(KEY_AUTO_DISPATCH_CARGO, cargoNames).apply();
    }
}
