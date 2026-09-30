package com.example.smartpantry.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Recipe;


public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        Recipe recipe = dbHelper.getRecipe(recipeId);

        TextView txtTitle = findViewById(R.id.txtRecipeTitle);
        TextView txtIngredients = findViewById(R.id.txtIngredientsList);
        TextView txtSteps = findViewById(R.id.txtSteps);

        if (recipe == null) {
            txtTitle.setText("Recipe not found");
            return;
        }

        toolbar.setTitle(recipe.getName());
        txtTitle.setText(recipe.getName());

        StringBuilder ingredientsText = new StringBuilder();
        for (Recipe.RequiredIngredient ri : recipe.getIngredients()) {
            ingredientsText.append("• ")
                    .append(trimTrailingZero(ri.getQuantity()))
                    .append(" ")
                    .append(ri.getUnit())
                    .append(" ")
                    .append(ri.getName())
                    .append("\n");
        }
        txtIngredients.setText(ingredientsText.toString().trim());
        txtSteps.setText(recipe.getSteps());
    }

    private String trimTrailingZero(double value) {
        if (value == Math.floor(value)) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
