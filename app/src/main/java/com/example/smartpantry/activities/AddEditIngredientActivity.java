package com.example.smartpantry.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.MainActivity;
import com.example.smartpantry.R;
import com.google.android.material.textfield.TextInputEditText;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;

import java.util.regex.Pattern;


public class AddEditIngredientActivity extends AppCompatActivity {

    private static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "tsp", "tbsp"};
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private DatabaseHelper dbHelper;
    private PantryItem editingItem;

    private TextInputEditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, UNITS);
        spinnerUnit.setAdapter(unitAdapter);

        long editingItemId = getIntent().getLongExtra(MainActivity.EXTRA_ITEM_ID, -1);

        if (editingItemId != -1) {
            editingItem = dbHelper.getPantryItem(editingItemId);
            if (editingItem != null) {
                toolbar.setTitle(R.string.edit_ingredient);
                populateForEdit(editingItem);
                findViewById(R.id.btnDelete).setVisibility(android.view.View.VISIBLE);
            }
        } else {
            toolbar.setTitle(R.string.add_ingredient);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveItem());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteItem());
    }

    private void populateForEdit(PantryItem item) {
        editName.setText(item.getName());
        editQuantity.setText(trimTrailingZero(item.getQuantity()));
        editExpiry.setText(item.getExpiryDate());
        int unitIndex = indexOf(item.getUnit());
        if (unitIndex >= 0) spinnerUnit.setSelection(unitIndex);
    }

    private void saveItem() {
        String name = textOf(editName).trim();
        String quantityStr = textOf(editQuantity).trim();
        String expiry = textOf(editExpiry).trim();
        String unit = (String) spinnerUnit.getSelectedItem();

        if (name.isEmpty()) {
            editName.setError(getString(R.string.error_name_required));
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            editQuantity.setError(getString(R.string.error_quantity_invalid));
            return;
        }
        if (!expiry.isEmpty() && !DATE_PATTERN.matcher(expiry).matches()) {
            editExpiry.setError(getString(R.string.error_expiry_format));
            return;
        }

        PantryItem item = (editingItem != null) ? editingItem : new PantryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setExpiryDate(expiry.isEmpty() ? null : expiry);

        if (editingItem != null) {
            dbHelper.updatePantryItem(item);
            Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addPantryItem(item);
            Toast.makeText(this, name + " added to pantry", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteItem() {
        if (editingItem != null) {
            dbHelper.deletePantryItem(editingItem.getId());
            Toast.makeText(this, editingItem.getName() + " removed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    private int indexOf(String value) {
        if (value == null) return -1;
        for (int i = 0; i < AddEditIngredientActivity.UNITS.length; i++) {
            if (AddEditIngredientActivity.UNITS[i].equalsIgnoreCase(value)) return i;
        }
        return -1;
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
