package com.italankin.placard.colorpicker;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.italankin.placard.R;

class ColorPickerDialog {

    static Builder builder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        final ColorPickerView colorPicker;
        final MaterialAlertDialogBuilder alertDialogBuilder;
        OnColorPickedListener onColorPickedListener;

        private Builder(Context context) {
            colorPicker = new ColorPickerView(context);
            alertDialogBuilder = new MaterialAlertDialogBuilder(context)
                    .setView(colorPicker)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        if (onColorPickedListener != null) {
                            onColorPickedListener.onColorPicked(colorPicker.getSelectedColor());
                        }
                    });
        }

        Builder setSelectedColor(@ColorInt int color) {
            colorPicker.setSelectedColor(color);
            return this;
        }

        Builder setHexVisible(boolean visible) {
            colorPicker.setHexVisible(visible);
            return this;
        }

        Builder setPreviewVisible(boolean visible) {
            colorPicker.setPreviewVisible(visible);
            return this;
        }

        Builder setColorModel(ColorPickerView.ColorModel model) {
            colorPicker.setColorModel(model);
            return this;
        }

        Builder setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
            this.onColorPickedListener = onColorPickedListener;
            return this;
        }

        Builder setResetButton(CharSequence text, DialogInterface.OnClickListener listener) {
            alertDialogBuilder.setNeutralButton(text, listener);
            return this;
        }

        public Builder setCancellable(boolean cancellable) {
            alertDialogBuilder.setCancelable(cancellable);
            return this;
        }

        AlertDialog build() {
            return alertDialogBuilder.create();
        }

        public AlertDialog show() {
            AlertDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }

    public interface OnColorPickedListener {
        void onColorPicked(@ColorInt int color);
    }
}
