package com.italankin.placard.util;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {

    private final OnTextChanged onTextChanged;

    public SimpleTextWatcher(OnTextChanged onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged.onTextChanged(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public interface OnTextChanged {
        void onTextChanged(String text);
    }
}
