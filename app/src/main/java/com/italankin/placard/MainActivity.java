package com.italankin.placard;

import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.italankin.placard.colorpicker.ColorPickerDialogFragment;
import com.italankin.placard.util.SharedPrefs;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogFragment.Listener {

    private static final String TAG_TEXT_COLOR = "text_color";
    private static final String TAG_BACKGROUND_COLOR = "background_color";

    private SharedPrefs prefs;

    private EditText editText;

    private TextView previewText;
    private View previewBox;

    private int selectedTextColor;
    private int selectedBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new SharedPrefs(this);

        editText = findViewById(R.id.edit_text);
        previewText = findViewById(R.id.preview_text);
        previewBox = findViewById(R.id.preview_box);
        View backgroundColor = findViewById(R.id.background_color);
        View textColor = findViewById(R.id.text_color);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                int indexOf = str.indexOf('\n');
                if (indexOf != -1) {
                    str = str.substring(0, indexOf);
                }
                previewText.setText(str);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textColor.setOnClickListener(v -> showColorPicker(selectedTextColor, TAG_TEXT_COLOR));
        backgroundColor.setOnClickListener(v -> showColorPicker(selectedBackgroundColor, TAG_BACKGROUND_COLOR));

        setSelectedTextColor(prefs.getTextColor(getDefaultTextColor()));
        setSelectedBackgroundColor(prefs.getBackgroundColor(getDefaultBackgroundColor()));

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show) {
            display();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onColorPicked(String tag, int newColor) {
        switch (tag) {
            case TAG_TEXT_COLOR:
                setSelectedTextColor(newColor);
                break;
            case TAG_BACKGROUND_COLOR:
                setSelectedBackgroundColor(newColor);
                break;
        }
    }

    @Override
    public void onColorReset(String tag) {
        switch (tag) {
            case TAG_TEXT_COLOR:
                setSelectedTextColor(getDefaultTextColor());
                break;
            case TAG_BACKGROUND_COLOR:
                setSelectedBackgroundColor(getDefaultBackgroundColor());
                break;
        }
    }

    private void handleIntent(Intent intent) {
        if (intent.getType() != null) {
            String type = intent.getType();
            if (ClipDescription.MIMETYPE_TEXT_PLAIN.equals(type)) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    text = text.trim();
                }
                editText.setText(text);
            }
        }
    }

    private void display() {
        String text = editText.getText().toString().trim();
        ArrayList<String> data = new ArrayList<>();
        for (String line : text.split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) {
                data.add(line);
            }
        }
        if (data.isEmpty()) {
            editText.setText("");
            editText.requestFocus();
            Toast.makeText(this, R.string.error_empty, Toast.LENGTH_SHORT).show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
            return;
        }
        Intent intent = PlacardActivity.getStartIntent(this, data);
        startActivity(intent);
    }

    private void setSelectedTextColor(@ColorInt int color) {
        prefs.setTextColor(color);
        selectedTextColor = color;
        previewText.setTextColor(color);
    }

    private void setSelectedBackgroundColor(@ColorInt int color) {
        prefs.setBackgroundColor(color);
        selectedBackgroundColor = color;
        previewBox.setBackgroundColor(color);
    }

    private int getDefaultBackgroundColor() {
        return ContextCompat.getColor(this, R.color.defaultBackgroundColor);
    }

    private int getDefaultTextColor() {
        return ContextCompat.getColor(this, R.color.defaultTextColor);
    }

    private void showColorPicker(int selectedBackgroundColor, String tagBackgroundColor) {
        new ColorPickerDialogFragment.Builder()
                .setHexVisible(true)
                .setPreviewVisible(true)
                .showResetButton(true)
                .setSelectedColor(selectedBackgroundColor)
                .build()
                .show(getSupportFragmentManager(), tagBackgroundColor);
    }
}
