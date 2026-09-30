package com.example.smartpantry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapters.RecipeAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.MatchingEngine;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerSuggested, recyclerAlmostThere;
    private View txtNoMatches, txtAlmostThereHeader;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_suggested);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerSuggested = findViewById(R.id.recyclerSuggested);
        recyclerAlmostThere = findViewById(R.id.recyclerAlmostThere);
        txtNoMatches = findViewById(R.id.txtNoMatches);
        txtAlmostThereHeader = findViewById(R.id.txtAlmostThereHeader);

        recyclerSuggested.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlmostThere.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        runMatchingAndDisplay();
    }

    private void runMatchingAndDisplay() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        List<Recipe> completeMatches = MatchingEngine.findCompleteMatches(allRecipes, pantry);
        List<Recipe> almostMatches = MatchingEngine.findAlmostMatches(allRecipes, pantry);

        RecipeAdapter.OnRecipeClickListener clickListener = recipe -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        };

        if (completeMatches.isEmpty()) {
            recyclerSuggested.setVisibility(View.GONE);
            txtNoMatches.setVisibility(View.VISIBLE);
        } else {
            recyclerSuggested.setVisibility(View.VISIBLE);
            txtNoMatches.setVisibility(View.GONE);
            recyclerSuggested.setAdapter(new RecipeAdapter(completeMatches, clickListener));
        }

        if (almostMatches.isEmpty()) {
            recyclerAlmostThere.setVisibility(View.GONE);
            txtAlmostThereHeader.setVisibility(View.GONE);
        } else {
            recyclerAlmostThere.setVisibility(View.VISIBLE);
            txtAlmostThereHeader.setVisibility(View.VISIBLE);
            recyclerAlmostThere.setAdapter(new RecipeAdapter(almostMatches, clickListener));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
