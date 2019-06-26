package com.italankin.placard.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.italankin.placard.R;

public class ColorPickerDialogFragment extends DialogFragment {

    private static final String ARG_SELECTED_COLOR = "selected_color";
    private static final String ARG_HEX_VISIBLE = "hex_visible";
    private static final String ARG_PREVIEW_VISIBLE = "preview_visible";
    private static final String ARG_COLOR_MODEL = "color_model";
    private static final String ARG_SHOW_RESET = "show_reset";

    private Listener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Listener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new NullPointerException();
        }
        ColorPickerDialog.Builder builder = ColorPickerDialog.builder(requireContext());
        if (arguments.containsKey(ARG_COLOR_MODEL)) {
            builder.setColorModel((ColorPickerView.ColorModel) arguments.getSerializable(ARG_COLOR_MODEL));
        }
        builder.setHexVisible(arguments.getBoolean(ARG_HEX_VISIBLE, false))
                .setPreviewVisible(arguments.getBoolean(ARG_PREVIEW_VISIBLE, true))
                .setSelectedColor(arguments.getInt(ARG_SELECTED_COLOR))
                .setOnColorPickedListener(color -> {
                    if (listener != null) {
                        listener.onColorPicked(getTag(), color);
                    }
                });
        if (arguments.getBoolean(ARG_SHOW_RESET, false)) {
            builder.setResetButton(getString(R.string.reset), (dialog, which) -> {
                if (listener != null) {
                    listener.onColorReset(getTag());
                }
            });
        }
        builder.colorPicker.setColorChangedListener(newColor -> {
            getArguments().putInt(ARG_SELECTED_COLOR, newColor);
        });
        return builder.build();
    }

    public static class Builder {
        private Bundle arguments = new Bundle();

        public Builder setSelectedColor(@ColorInt int color) {
            arguments.putInt(ARG_SELECTED_COLOR, color);
            return this;
        }

        public Builder setHexVisible(boolean visible) {
            arguments.putBoolean(ARG_HEX_VISIBLE, visible);
            return this;
        }

        public Builder setPreviewVisible(boolean visible) {
            arguments.putBoolean(ARG_PREVIEW_VISIBLE, visible);
            return this;
        }

        public Builder setColorModel(ColorPickerView.ColorModel model) {
            arguments.putSerializable(ARG_COLOR_MODEL, model);
            return this;
        }

        public Builder showResetButton(boolean show) {
            arguments.putBoolean(ARG_SHOW_RESET, show);
            return this;
        }

        public ColorPickerDialogFragment build() {
            ColorPickerDialogFragment fragment = new ColorPickerDialogFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
    }

    public interface Listener {
        void onColorPicked(String tag, @ColorInt int newColor);

        default void onColorReset(String tag) {
        }
    }
}
