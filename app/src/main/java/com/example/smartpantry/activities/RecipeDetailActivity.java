package com.example.smartpantry.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.entities.RecipeIngredient;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

        TextView txtTitle = findViewById(R.id.txtRecipeTitle);
        TextView txtIngredients = findViewById(R.id.txtIngredientsList);
        TextView txtInstructions = findViewById(R.id.txtInstructions);

        int recipeId = getIntent().getIntExtra("EXTRA_RECIPE_ID", -1);
        String name = getIntent().getStringExtra("EXTRA_RECIPE_NAME");
        String instructions = getIntent().getStringExtra("EXTRA_RECIPE_INSTRUCTIONS");

        txtTitle.setText(name);
        txtInstructions.setText(instructions);

        if (recipeId != -1) {
            List<RecipeIngredient> ingredients = dbHelper.getIngredientsForRecipe(recipeId);
            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ri : ingredients) {
                sb.append("• ").append(ri.getIngredientName())
                        .append(" - ").append(ri.getQuantityRequired())
                        .append(" ").append(ri.getUnit()).append("\n");
            }
            txtIngredients.setText(sb.toString().trim());
        }
    }
}