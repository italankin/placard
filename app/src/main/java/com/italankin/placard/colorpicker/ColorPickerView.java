package com.italankin.placard.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.italankin.placard.R;

public class ColorPickerView extends LinearLayout {

    private static final int DEFAULT_SELECTED_COLOR = Color.BLACK;
    private static final ColorModel DEFAULT_COLOR_MODEL = ColorModel.RGB;
    private static final String HEX_FORMAT = "#%06x";

    private static final String STATE_BASE_STATE = "base_state";
    private static final String STATE_PREVIEW_VISIBLE = "preview_visible";
    private static final String STATE_SELECTED_COLOR = "selected_color";
    private static final String STATE_MODEL = "model";

    private final View preview;
    private final TextView hex;
    private final ViewGroup container;
    private ColorModelController colorModelController;
    @Nullable
    private ColorModelController.OnColorChangedListener colorChangedListener;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.widget_color_picker, this);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        preview = findViewById(R.id.preview);
        hex = findViewById(R.id.hex_value);
        container = findViewById(R.id.row_container);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        int model = a.getInt(R.styleable.ColorPickerView_cp_model, -1);
        setColorModel(model != -1 ? ColorModel.values()[model] : DEFAULT_COLOR_MODEL);
        int selectedColor = a.getColor(R.styleable.ColorPickerView_cp_selectedColor, Color.BLACK);
        setSelectedColor(selectedColor);
        boolean previewVisible = a.getBoolean(R.styleable.ColorPickerView_cp_previewVisible, true);
        if (!previewVisible) {
            setPreviewVisible(false);
        }
        boolean hexVisible = a.getBoolean(R.styleable.ColorPickerView_cp_hexVisible, false);
        if (hexVisible) {
            setHexVisible(true);
        }
        int previewHeight = a.getDimensionPixelSize(R.styleable.ColorPickerView_cp_previewHeight, -1);
        if (previewHeight != -1) {
            setPreviewHeight(previewHeight);
        }
        a.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (colorModelController != null) {
            colorModelController.destroy();
        }
    }

    public void setColorModel(ColorModel model) {
        int selectedColor = DEFAULT_SELECTED_COLOR;
        if (colorModelController != null) {
            selectedColor = colorModelController.getColor();
            colorModelController.destroy();
        }
        container.removeAllViews();
        colorModelController = getModelController(model);
        colorModelController.setListener(newColor -> {
            preview.setBackgroundColor(newColor);
            hex.setText(String.format(HEX_FORMAT, 0x00ffffff & newColor));
            if (colorChangedListener != null) {
                colorChangedListener.onColorChanged(newColor);
            }
        });
        colorModelController.init(container, LayoutInflater.from(getContext()));
        colorModelController.setColor(selectedColor);
    }

    public void setSelectedColor(@ColorInt int color) {
        colorModelController.setColor(color);
    }

    @ColorInt
    public int getSelectedColor() {
        return colorModelController.getColor();
    }

    public void setPreviewVisible(boolean visible) {
        preview.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setPreviewHeight(@Px int height) {
        ViewGroup.LayoutParams layoutParams = preview.getLayoutParams();
        layoutParams.height = height;
        preview.requestLayout();
        preview.invalidate();
    }

    public void setHexVisible(boolean visible) {
        hex.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setColorChangedListener(@Nullable ColorModelController.OnColorChangedListener listener) {
        colorChangedListener = listener;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable base = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable(STATE_BASE_STATE, base);
        state.putBoolean(STATE_PREVIEW_VISIBLE, preview.getVisibility() == View.VISIBLE);
        state.putInt(STATE_SELECTED_COLOR, colorModelController.getColor());
        ColorModel model = null;
        if (colorModelController instanceof RGB) {
            model = ColorModel.RGB;
        }
        state.putSerializable(STATE_MODEL, model);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle myState = (Bundle) state;
        super.onRestoreInstanceState(myState.getParcelable(STATE_BASE_STATE));
        boolean previewVisible = myState.getBoolean(STATE_PREVIEW_VISIBLE, true);
        setPreviewVisible(previewVisible);
        ColorModel model = (ColorModel) myState.getSerializable(STATE_MODEL);
        if (model != null) {
            setColorModel(model);
            int selectedColor = myState.getInt(STATE_SELECTED_COLOR, Color.BLACK);
            setSelectedColor(selectedColor);
        }
    }

    private ColorModelController getModelController(ColorModel model) {
        if (model == ColorModel.RGB) {
            return new RGB();
        }
        throw new IllegalArgumentException("Unknown ColorModel: " + model);
    }

    public enum ColorModel {
        RGB
    }
}

