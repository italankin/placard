package com.italankin.placard.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import com.italankin.placard.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SharedPrefs {

    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_TEXT_FONT = "text_font";
    private static final String KEY_SAVED_ITEMS = "saved_items";
    private static final String KEY_ROTATION = "rotation";

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

    public void setTextFont(TextFont font) {
        prefs.edit().putString(KEY_TEXT_FONT, font.fontName).apply();
    }

    public TextFont getTextFont() {
        String font = prefs.getString(KEY_TEXT_FONT, null);
        if (font == null) {
            return TextFont.DEFAULT;
        }
        for (TextFont textFont : TextFont.values()) {
            if (textFont.fontName.equals(font)) {
                return textFont;
            }
        }
        return TextFont.DEFAULT;
    }

    public void setRotation(Rotation rotation) {
        prefs.edit().putString(KEY_ROTATION, rotation.key).apply();
    }

    public Rotation getRotation() {
        String key = prefs.getString(KEY_ROTATION, null);
        if (key == null) {
            return Rotation.AUTO;
        }
        for (Rotation textFont : Rotation.values()) {
            if (textFont.key.equals(key)) {
                return textFont;
            }
        }
        return Rotation.AUTO;
    }

    public enum TextFont {
        DEFAULT("default", Typeface.DEFAULT, R.string.title_font_default),
        MONOSPACE("monospace", Typeface.MONOSPACE, R.string.title_font_monospace),
        SANS_SERIF("sans_serif", Typeface.SANS_SERIF, R.string.title_font_sans_serif),
        SERIF("serif", Typeface.SERIF, R.string.title_font_serif);

        public final String fontName;
        public final Typeface typeface;
        public final int fontTitle;

        TextFont(String fontName, Typeface typeface, int fontTitle) {
            this.fontName = fontName;
            this.typeface = typeface;
            this.fontTitle = fontTitle;
        }
    }

    public enum Rotation {
        AUTO("auto", R.drawable.ic_rotation_auto, R.string.rotation_auto),
        PORTRAIT("portrait", R.drawable.ic_rotation_portrait, R.string.rotation_portrait),
        LANDSCAPE("landscape", R.drawable.ic_rotation_landscape, R.string.rotation_landscape);

        public final String key;
        public final int icon;
        public final int title;

        Rotation(String key, int icon, int title) {
            this.key = key;
            this.icon = icon;
            this.title = title;
        }
    }
}
