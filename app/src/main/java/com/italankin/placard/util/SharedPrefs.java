package com.italankin.placard.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;

public class SharedPrefs {

    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BACKGROUND_COLOR = "background_color";

    private final SharedPreferences prefs;

    public SharedPrefs(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setTextColor(@ColorInt int color) {
        prefs.edit().putInt(KEY_TEXT_COLOR, color).apply();
    }

    public void setBackgroundColor(@ColorInt int color) {
        prefs.edit().putInt(KEY_BACKGROUND_COLOR, color).apply();
    }

    @ColorInt
    public int getTextColor(@ColorInt int defaultColor) {
        return prefs.getInt(KEY_TEXT_COLOR, defaultColor);
    }

    @ColorInt
    public int getBackgroundColor(@ColorInt int defaultColor) {
        return prefs.getInt(KEY_BACKGROUND_COLOR, defaultColor);
    }
}
