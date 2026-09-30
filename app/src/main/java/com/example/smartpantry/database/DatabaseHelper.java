package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "_id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INGREDIENTS = "ingredients";
    public static final String COL_RECIPE_STEPS = "steps";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

//   CRUD

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = contentValuesFor(item);
        return db.insert(TABLE_PANTRY, null, values);
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = contentValuesFor(item);
        return db.update(TABLE_PANTRY, values, COL_PANTRY_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_PANTRY_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                items.add(pantryItemFromCursor(cursor));
            }
            cursor.close();
        }
        return items;
    }

    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = pantryItemFromCursor(cursor);
        }
        cursor.close();
        return item;
    }

    private ContentValues contentValuesFor(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QTY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        return values;
    }

    private PantryItem pantryItemFromCursor(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)));
        item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)));
        item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)));
        item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)));
        return item;
    }

    // ---------------------------------------------------------------
    // Recipes for function
    // ---------------------------------------------------------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null,
                COL_RECIPE_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
                recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
                recipe.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS)));
                String raw = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INGREDIENTS));
                recipe.setIngredients(Recipe.deserializeIngredients(raw));
                recipes.add(recipe);
            }
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        for (Recipe r : getAllRecipes()) {
            if (r.getId() == id) return r;
        }
        return null;
    }

//    Pre-loaded Recipes
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Egg Stir-Fry",
                "egg:3:pcs|tomato:2:pcs|onion:1:pcs|salt:1:tsp",
                "1. Beat eggs with a pinch of salt.\n2. Fry eggs until just set, remove.\n" +
                        "3. Saute chopped onion, then tomato, until soft.\n4. Return eggs, toss together, serve.");

        addRecipe(db, "Garlic Butter Pasta",
                "pasta:200:g|butter:30:g|garlic:3:pcs|salt:1:tsp",
                "1. Boil pasta until al dente.\n2. Melt butter, add crushed garlic, cook 1 min.\n" +
                        "3. Toss pasta through the garlic butter with a splash of pasta water. Season and serve.");

        addRecipe(db, "Classic Omelette",
                "egg:3:pcs|milk:30:ml|salt:1:tsp|butter:10:g",
                "1. Whisk eggs with milk and salt.\n2. Melt butter in a pan.\n" +
                        "3. Pour in egg mixture, cook until set, fold and serve.");

        addRecipe(db, "Chicken Fried Rice",
                "rice:300:g|chicken:200:g|egg:2:pcs|onion:1:pcs|soy sauce:30:ml",
                "1. Cook diced chicken until done, set aside.\n2. Scramble eggs, set aside.\n" +
                        "3. Stir-fry onion, add rice, chicken, egg and soy sauce. Toss and serve hot.");

        addRecipe(db, "Simple Tomato Soup",
                "tomato:6:pcs|onion:1:pcs|garlic:2:pcs|vegetable stock:500:ml",
                "1. Saute onion and garlic until soft.\n2. Add chopped tomato and stock, simmer 20 min.\n" +
                        "3. Blend until smooth and serve warm.");

        addRecipe(db, "Cheesy Baked Potato",
                "potato:2:pcs|cheese:60:g|butter:15:g|salt:1:tsp",
                "1. Bake potatoes until tender.\n2. Slice open, add butter and salt.\n" +
                        "3. Top with cheese and return to oven until melted.");

        addRecipe(db, "Veggie Fried Noodles",
                "noodles:200:g|carrot:1:pcs|cabbage:100:g|soy sauce:30:ml",
                "1. Cook noodles, drain.\n2. Stir-fry shredded carrot and cabbage.\n" +
                        "3. Add noodles and soy sauce, toss well and serve.");

        addRecipe(db, "Banana Pancakes",
                "flour:200:g|banana:2:pcs|egg:1:pcs|milk:150:ml",
                "1. Mash banana, whisk with egg and milk.\n2. Fold in flour to form a batter.\n" +
                        "3. Cook spoonfuls on a hot, lightly-oiled pan until golden on both sides.");

        addRecipe(db, "Lentil and Carrot Soup",
                "lentil:200:g|carrot:2:pcs|onion:1:pcs|vegetable stock:600:ml",
                "1. Saute onion and diced carrot.\n2. Add lentils and stock, simmer 25 min until soft.\n" +
                        "3. Blend partially for a hearty texture and serve.");

        addRecipe(db, "Chicken Rice Bowl",
                "rice:250:g|chicken:200:g|carrot:1:pcs|soy sauce:20:ml",
                "1. Cook rice.\n2. Pan-fry diced chicken with soy sauce until cooked through.\n" +
                        "3. Serve chicken and grated carrot over the rice.");

        addRecipe(db, "Margherita Toast",
                "bread:2:pcs|tomato:1:pcs|cheese:40:g|butter:10:g",
                "1. Butter bread and toast lightly.\n2. Top with sliced tomato and cheese.\n" +
                        "3. Grill until cheese melts and serve.");

        addRecipe(db, "Egg Fried Rice",
                "rice:300:g|egg:2:pcs|onion:1:pcs|soy sauce:20:ml",
                "1. Scramble eggs, set aside.\n2. Stir-fry onion, add rice and soy sauce.\n" +
                        "3. Fold in eggs, toss well and serve hot.");

        addRecipe(db, "Potato and Onion Hash",
                "potato:3:pcs|onion:1:pcs|butter:20:g|salt:1:tsp",
                "1. Dice potato and onion.\n2. Fry in butter over medium heat until golden and tender.\n" +
                        "3. Season with salt and serve.");

        addRecipe(db, "Milk and Honey Oats",
                "oats:100:g|milk:250:ml|honey:20:ml",
                "1. Combine oats and milk in a pot.\n2. Simmer 5 minutes, stirring occasionally.\n" +
                        "3. Stir through honey and serve warm.");

        addRecipe(db, "Cabbage Carrot Slaw",
                "cabbage:150:g|carrot:2:pcs|onion:0.5:pcs|salt:1:tsp",
                "1. Finely shred cabbage and carrot.\n2. Thinly slice onion.\n" +
                        "3. Toss all together with salt and a splash of vinegar if available.");

        addRecipe(db, "Cheese and Onion Omelette",
                "egg:3:pcs|cheese:40:g|onion:1:pcs|salt:1:tsp",
                "1. Whisk eggs with salt.\n2. Saute diced onion until soft.\n" +
                        "3. Pour eggs over onion, sprinkle cheese, fold once set and serve.");

        addRecipe(db, "Simple Chicken Soup",
                "chicken:200:g|carrot:1:pcs|onion:1:pcs|vegetable stock:600:ml",
                "1. Saute onion and carrot.\n2. Add chicken and stock, simmer 25 min until cooked through.\n" +
                        "3. Shred chicken back into the pot and serve.");

        addRecipe(db, "Garlic Bread",
                "bread:4:pcs|butter:40:g|garlic:2:pcs|salt:1:tsp",
                "1. Mash butter with crushed garlic and salt.\n2. Spread generously on bread slices.\n" +
                        "3. Grill or toast until golden and serve.");
    }

    private void addRecipe(SQLiteDatabase db, String name, String ingredients, String steps) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_RECIPE_INGREDIENTS, ingredients);
        values.put(COL_RECIPE_STEPS, steps);
        db.insert(TABLE_RECIPES, null, values);
    }
}
