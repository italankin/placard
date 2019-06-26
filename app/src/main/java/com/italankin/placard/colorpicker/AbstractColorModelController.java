package com.italankin.placard.colorpicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.italankin.placard.R;

abstract class AbstractColorModelController implements ColorModelController {
    @Nullable
    OnColorChangedListener listener;

    @CallSuper
    @Override
    public void destroy() {
        this.listener = null;
    }

    @Override
    public void setListener(@Nullable OnColorChangedListener listener) {
        this.listener = listener;
    }

    Row addRow(ViewGroup root, LayoutInflater inflater, CharSequence label,
            @ColorInt int textColor, int max) {
        View rowView = inflater.inflate(R.layout.partial_color_picker_row, root, false);
        TextView labelView = rowView.findViewById(R.id.label);
        labelView.setTextColor(textColor);
        labelView.setText(label);
        SeekBar seekBarView = rowView.findViewById(R.id.seekbar);
        seekBarView.setMax(max);
        root.addView(rowView);
        TextView valueView = rowView.findViewById(R.id.value);
        Row row = new Row(rowView, labelView, seekBarView, valueView);
        seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                row.update();
                if (listener != null) {
                    listener.onColorChanged(getColor());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return row;
    }

    static class Row {
        final View root;
        final TextView label;
        final SeekBar seekbar;
        final TextView value;

        Row(View root, TextView label, SeekBar seekbar, TextView value) {
            this.root = root;
            this.label = label;
            this.seekbar = seekbar;
            this.value = value;
        }

        void setValue(int newValue) {
            seekbar.setProgress(newValue);
            update();
        }

        int getValue() {
            return seekbar.getProgress();
        }

        void update() {
            value.setText(String.valueOf(getValue()));
        }
    }
}
