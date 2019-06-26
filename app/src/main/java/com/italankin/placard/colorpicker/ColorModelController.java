package com.italankin.placard.colorpicker;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

interface ColorModelController {

    void init(ViewGroup root, LayoutInflater inflater);

    void destroy();

    void setColor(@ColorInt int color);

    @ColorInt
    int getColor();

    void setListener(@Nullable OnColorChangedListener listener);

    interface OnColorChangedListener {
        void onColorChanged(@ColorInt int newColor);
    }
}
