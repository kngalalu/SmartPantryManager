package com.example.smartpantry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapters.IngredientAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.entities.Ingredient;

import java.util.List;

public class PantryActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientActionListener {

    private RecyclerView recyclerView;
    private IngredientAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recyclerIngredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAdd = findViewById(R.id.btnAddIngredient);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(PantryActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<Ingredient> ingredients = dbHelper.getAllIngredients();
        if (adapter == null) {
            adapter = new IngredientAdapter(ingredients, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(ingredients);
        }
    }

    @Override
    public void onEdit(Ingredient ingredient) {
        Intent intent = new Intent(PantryActivity.this, AddEditIngredientActivity.class);
        intent.putExtra("EXTRA_ID", ingredient.getId());
        intent.putExtra("EXTRA_NAME", ingredient.getName());
        intent.putExtra("EXTRA_QTY", ingredient.getQuantity());
        intent.putExtra("EXTRA_UNIT", ingredient.getUnit());
        intent.putExtra("EXTRA_EXPIRY", ingredient.getExpiryDate());
        startActivity(intent);
    }

    @Override
    public void onDelete(Ingredient ingredient) {
        dbHelper.deleteIngredient(ingredient.getId());
        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        loadPantryItems();
    }
}