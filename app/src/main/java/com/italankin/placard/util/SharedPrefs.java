package com.italankin.placard.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SharedPrefs {

    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_SAVED_ITEMS = "saved_items";

    private static final String DELIMITER = ";;;";

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

    public void setSavedItems(List<String> items) {
        if (items.isEmpty()) {
            prefs.edit().remove(KEY_SAVED_ITEMS).apply();
            return;
        }
        StringBuilder sb = new StringBuilder(items.get(0));
        for (int i = 1; i < items.size(); i++) {
            sb.append(DELIMITER);
            sb.append(items.get(i));
        }
        String value = sb.toString();
        prefs.edit().putString(KEY_SAVED_ITEMS, value).apply();
    }

    public List<String> getSavedItems() {
        String value = prefs.getString(KEY_SAVED_ITEMS, null);
        if (value == null) {
            return Collections.emptyList();
        }
        String[] strings = value.split(DELIMITER);
        return Arrays.asList(strings);
    }
}
