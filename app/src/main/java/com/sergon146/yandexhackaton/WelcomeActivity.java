package com.sergon146.yandexhackaton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    public static final String LEVEL_EXTRA = "LEVEL_EXTRA";
    public static final String LEVEL_EASY = "LEVEL_EASY";
    public static final String LEVEL_INVERT = "LEVEL_INVERT";
    public static final String LEVEL_RANDOM = "LEVEL_RANDOM";
    public static final String LEVEL_HARDCORE = "LEVEL_HARDCORE";

    public static final String SENSOR_EXTRA = "SENSOR_EXTRA";

    public Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        switch1 = findViewById(R.id.switch1);
        switch1.setChecked(true);
        findViewById(R.id.easy).setOnClickListener(view -> startLevel(LEVEL_EASY));
        findViewById(R.id.invert).setOnClickListener(view -> startLevel(LEVEL_INVERT));
        findViewById(R.id.random).setOnClickListener(view -> startLevel(LEVEL_RANDOM));
        findViewById(R.id.hardcore).setOnClickListener(view -> startLevel(LEVEL_HARDCORE));

    }

    private void startLevel(String level) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LEVEL_EXTRA, level);
        intent.putExtra(SENSOR_EXTRA, switch1.isChecked());
        startActivity(intent);
    }
}
