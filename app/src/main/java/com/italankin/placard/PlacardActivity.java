package com.italankin.placard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.italankin.placard.util.SharedPrefs;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

public class PlacardActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context, ArrayList<String> list) {
        Intent intent = new Intent(context, PlacardActivity.class);
        intent.putStringArrayListExtra(EXTRA_DATA, list);
        return intent;
    }

    private static final String EXTRA_DATA = "data";

    private static final int MASK_UNSELECTED_COLOR = 0x40ffffff;

    private static final float SYSTEM_BARS_VALUE_SHIFT = 0.2f;

    private static final int DEFAULT_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private View decorView;
    private int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        decorView = window.getDecorView();
        decorView.setSystemUiVisibility(DEFAULT_FLAGS);

        setContentView(R.layout.activity_placard);

        SharedPrefs prefs = new SharedPrefs(this);
        textColor = prefs.getTextColor(
                ContextCompat.getColor(this, R.color.defaultTextColor));
        int backgroundColor = prefs.getBackgroundColor(
                ContextCompat.getColor(this, R.color.defaultBackgroundColor));

        window.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        int systemBarsColor = makeSystemBarsColor(backgroundColor);
        window.setStatusBarColor(systemBarsColor);
        window.setNavigationBarColor(systemBarsColor);

        ViewPager pager = findViewById(R.id.pager);
        ArrayList<String> list = getIntent().getStringArrayListExtra(EXTRA_DATA);
        pager.setAdapter(new Adapter(list));
        pager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin));

        PageIndicatorView indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setSelectedColor(textColor);
        indicator.setUnselectedColor(textColor & MASK_UNSELECTED_COLOR);
    }

    private int makeSystemBarsColor(int backgroundColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(backgroundColor, hsv);
        float v = hsv[2];
        if (v > 0.5f) {
            hsv[2] = v - SYSTEM_BARS_VALUE_SHIFT;
        } else {
            hsv[2] = v + SYSTEM_BARS_VALUE_SHIFT;
        }
        return Color.HSVToColor(hsv);
    }

    private class Adapter extends PagerAdapter {
        private final ArrayList<String> data;

        private Adapter(ArrayList<String> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_text, container, false);
            TextView textView = view.findViewById(R.id.text);
            textView.setTextColor(textColor);
            textView.setText(data.get(position));
            textView.setOnClickListener(v -> {
                int visibility = decorView.getSystemUiVisibility();
                decorView.setSystemUiVisibility(visibility == 0 ? DEFAULT_FLAGS : 0);
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
