package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.activities.AddEditIngredientActivity;
import com.example.smartpantry.activities.SettingsActivity;
import com.example.smartpantry.activities.SuggestedRecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.smartpantry.adapters.PantryAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String PREF_EXPIRY_ALERTS = "expiry_alerts_enabled";
    public static final String PREF_METRIC_UNITS = "metric_units_enabled";

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private RecyclerView recyclerView;
    private View emptyView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DatabaseHelper.getInstance(this);
        recyclerView = findViewById(R.id.recyclerPantry);
        emptyView = findViewById(R.id.txtEmptyPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_suggested) {
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();

        if (items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        boolean expiryAlertsEnabled = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(PREF_EXPIRY_ALERTS, true);

        PantryAdapter adapter = new PantryAdapter(items, item -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            intent.putExtra(EXTRA_ITEM_ID, item.getId());
            startActivity(intent);
        }, expiryAlertsEnabled);

        recyclerView.setAdapter(adapter);
    }
}
