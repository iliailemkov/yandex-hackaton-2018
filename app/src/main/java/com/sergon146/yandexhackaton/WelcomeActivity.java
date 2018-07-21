package com.sergon146.yandexhackaton;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    public static final String LEVEL_EXTRA = "LEVEL_EXTRA";
    public static final String LEVEL_EASY = "LEVEL_EASY";
    public static final String LEVEL_INVERT = "LEVEL_INVERT";
    public static final String LEVEL_RANDOM = "LEVEL_RANDOM";
    public static final String LEVEL_HARDCORE = "LEVEL_HARDCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.easy).setOnClickListener(view -> startLevel(LEVEL_EASY));
        findViewById(R.id.invert).setOnClickListener(view -> startLevel(LEVEL_INVERT));
        findViewById(R.id.random).setOnClickListener(view -> startLevel(LEVEL_RANDOM));
        findViewById(R.id.hardcore).setOnClickListener(view -> startLevel(LEVEL_HARDCORE));
    }

    private void startLevel(String level) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LEVEL_EXTRA, level);
        startActivity(intent);
    }
}
