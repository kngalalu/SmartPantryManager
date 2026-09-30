package com.example.smartpantry.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.MainActivity;
import com.example.smartpantry.R;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiryAlerts);
        SwitchCompat switchUnits = findViewById(R.id.switchUnits);

        switchExpiry.setChecked(prefs.getBoolean(MainActivity.PREF_EXPIRY_ALERTS, true));
        switchUnits.setChecked(prefs.getBoolean(MainActivity.PREF_METRIC_UNITS, true));

        switchExpiry.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(MainActivity.PREF_EXPIRY_ALERTS, isChecked).apply());

        switchUnits.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(MainActivity.PREF_METRIC_UNITS, isChecked).apply());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
