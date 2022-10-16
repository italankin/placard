package com.italankin.placard.favorites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.italankin.placard.R;
import com.italankin.placard.util.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements FavoritesAdapter.OnItemClickListener {

    private static final String EXTRA_RESULT = "result";

    private SharedPrefs prefs;
    private List<String> items;
    private FavoritesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new SharedPrefs(this);
        items = new ArrayList<>(prefs.getSavedItems());

        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.list);
        adapter = new FavoritesAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position, String item) {
        Intent result = new Intent().putExtra(EXTRA_RESULT, item);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onItemDeleteClick(int position) {
        items.remove(position);
        adapter.notifyItemRemoved(position);
        prefs.setSavedItems(items);
    }

    public static class SelectFavoriteContract extends ActivityResultContract<Void, String> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void unused) {
            return new Intent(context, FavoritesActivity.class);
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            return (resultCode == RESULT_OK && intent != null) ? intent.getStringExtra(EXTRA_RESULT) : null;
        }
    }
}
