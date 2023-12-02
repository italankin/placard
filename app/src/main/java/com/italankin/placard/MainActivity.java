package com.italankin.placard;

import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.italankin.placard.colorpicker.ColorPickerDialogFragment;
import com.italankin.placard.favorites.FavoritesActivity;
import com.italankin.placard.util.SharedPrefs;
import com.italankin.placard.util.SimpleTextWatcher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ColorPickerDialogFragment.Listener,
        ActivityResultCallback<String> {

    private static final String TAG_TEXT_COLOR = "text_color";
    private static final String TAG_BACKGROUND_COLOR = "background_color";

    private SharedPrefs prefs;

    private EditText editText;

    private TextView previewText;
    private View previewBox;
    private View play;

    private MenuItem menuFavoriteAdd;

    private int selectedTextColor;
    private int selectedBackgroundColor;

    private final ActivityResultLauncher<Void> favoritesLauncher = registerForActivityResult(
            new FavoritesActivity.SelectFavoriteContract(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new SharedPrefs(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = findViewById(R.id.edit_text);
        previewText = findViewById(R.id.preview_text);
        previewBox = findViewById(R.id.preview_box);
        play = findViewById(R.id.play);
        View backgroundColor = findViewById(R.id.background_color);
        View textColor = findViewById(R.id.text_color);
        View textFont = findViewById(R.id.text_font);

        play.setOnClickListener(v -> display());
        play.setEnabled(!getTrimmed().isEmpty());

        editText.addTextChangedListener(new SimpleTextWatcher(text -> {
            int indexOf = text.indexOf('\n');
            if (indexOf != -1) {
                text = text.substring(0, indexOf);
            }
            previewText.setText(text);
            boolean enabled = !text.isEmpty();
            play.setEnabled(enabled);
            if (menuFavoriteAdd != null) {
                menuFavoriteAdd.setVisible(enabled);
            }
        }));
        textColor.setOnClickListener(v -> showColorPicker(selectedTextColor, TAG_TEXT_COLOR));
        backgroundColor.setOnClickListener(v -> showColorPicker(selectedBackgroundColor, TAG_BACKGROUND_COLOR));
        textFont.setOnClickListener(v -> showFontList());
        SharedPrefs.TextFont font = prefs.getTextFont();
        previewText.setTypeface(font.typeface);

        setSelectedTextColor(prefs.getTextColor(getDefaultTextColor()));
        setSelectedBackgroundColor(prefs.getBackgroundColor(getDefaultBackgroundColor()));

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuFavoriteAdd = menu.findItem(R.id.action_favorite_add);
        menuFavoriteAdd.setVisible(!getTrimmed().isEmpty());
        MenuItem menuRotation = menu.findItem(R.id.action_rotation);
        SharedPrefs.Rotation rotation = prefs.getRotation();
        menuRotation.setIcon(rotation.icon);
        menuRotation.setTitle(rotation.title);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_favorites) {
            favoritesLauncher.launch(null);
            return true;
        } else if (item.getItemId() == R.id.action_favorite_add) {
            addFavorite();
            return true;
        } else if (item.getItemId() == R.id.action_rotation) {
            SharedPrefs.Rotation rotation = prefs.getRotation();
            SharedPrefs.Rotation newRotation = SharedPrefs.Rotation.values()[(rotation.ordinal() + 1) % SharedPrefs.Rotation.values().length];
            prefs.setRotation(newRotation);
            item.setTitle(newRotation.title);
            item.setIcon(newRotation.icon);
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
    public void onActivityResult(String result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        editText.setText(result);
        editText.selectAll();
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

    private void showColorPicker(int selectedColor, String tag) {
        new ColorPickerDialogFragment.Builder()
                .setHexVisible(true)
                .setPreviewVisible(true)
                .showResetButton(true)
                .setSelectedColor(selectedColor)
                .build()
                .show(getSupportFragmentManager(), tag);
    }

    private void showFontList() {
        CharSequence[] items = new CharSequence[SharedPrefs.TextFont.values().length];
        SharedPrefs.TextFont[] values = SharedPrefs.TextFont.values();
        for (int i = 0; i < values.length; i++) {
            SharedPrefs.TextFont font = values[i];
            items[i] = getString(font.fontTitle);
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_font)
                .setItems(items, (dialog, which) -> {
                    SharedPrefs.TextFont selected = SharedPrefs.TextFont.values()[which];
                    prefs.setTextFont(selected);
                    previewText.setTypeface(selected.typeface);
                })
                .show();
    }

    private void addFavorite() {
        String text = getTrimmed();
        if (text.isEmpty()) {
            return;
        }
        List<String> savedItems = new ArrayList<>(prefs.getSavedItems());
        savedItems.add(text);
        prefs.setSavedItems(savedItems);
        Toast.makeText(this, R.string.toast_favorite_added, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private String getTrimmed() {
        return editText.getText().toString().trim();
    }
}
