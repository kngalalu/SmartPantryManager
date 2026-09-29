package com.example.smartpantry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapters.RecipeAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.entities.Ingredient;
import com.example.smartpantry.entities.Recipe;
import com.example.smartpantry.entities.RecipeIngredient;
import com.example.smartpantry.utils.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private TextView txtStatus;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = DatabaseHelper.getInstance(this);

        txtStatus = findViewById(R.id.txtStatus);
        recyclerView = findViewById(R.id.recyclerSuggestedRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        List<Ingredient> pantry = dbHelper.getAllIngredients();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();
        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> requirements = dbHelper.getIngredientsForRecipe(recipe.getId());
            if (RecipeMatcher.canMakeRecipe(pantry, requirements)) {
                matchingRecipes.add(recipe);
            }
        }

        if (matchingRecipes.isEmpty()) {
            txtStatus.setText("No recipes match your current pantry ingredients.");
        } else {
            txtStatus.setText("Recipes you can make right now (" + matchingRecipes.size() + "):");
        }

        RecipeAdapter adapter = new RecipeAdapter(matchingRecipes, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra("EXTRA_RECIPE_ID", recipe.getId());
        intent.putExtra("EXTRA_RECIPE_NAME", recipe.getName());
        intent.putExtra("EXTRA_RECIPE_INSTRUCTIONS", recipe.getInstructions());
        startActivity(intent);
    }
}