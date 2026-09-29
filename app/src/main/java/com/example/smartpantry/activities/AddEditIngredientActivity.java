package com.example.smartpantry.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.entities.Ingredient;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiryDate;
    private DatabaseHelper dbHelper;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = DatabaseHelper.getInstance(this);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        Button btnSave = findViewById(R.id.btnSave);

        if (getIntent().hasExtra("EXTRA_ID")) {
            ingredientId = getIntent().getIntExtra("EXTRA_ID", -1);
            etName.setText(getIntent().getStringExtra("EXTRA_NAME"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("EXTRA_QTY", 0.0)));
            etUnit.setText(getIntent().getStringExtra("EXTRA_UNIT"));
            etExpiryDate.setText(getIntent().getStringExtra("EXTRA_EXPIRY"));
        }

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiryDate.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(qtyStr) || TextUtils.isEmpty(unit)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double qty = Double.parseDouble(qtyStr);

        if (ingredientId == -1) {
            Ingredient newIng = new Ingredient(name, qty, unit, expiry);
            dbHelper.addIngredient(newIng);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            Ingredient updateIng = new Ingredient(ingredientId, name, qty, unit, expiry);
            dbHelper.updateIngredient(updateIng);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}