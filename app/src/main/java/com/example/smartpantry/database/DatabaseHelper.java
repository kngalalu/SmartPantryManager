package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.entities.Ingredient;
import com.example.smartpantry.entities.Recipe;
import com.example.smartpantry.entities.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COL_ING_ID = "id";
    public static final String COL_ING_NAME = "name";
    public static final String COL_ING_QTY = "quantity";
    public static final String COL_ING_UNIT = "unit";
    public static final String COL_ING_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "quantity_required";
    public static final String COL_RI_UNIT = "unit";

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
        String createIngredientsTable = "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                COL_ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ING_NAME + " TEXT NOT NULL, " +
                COL_ING_QTY + " REAL NOT NULL, " +
                COL_ING_UNIT + " TEXT NOT NULL, " +
                COL_ING_EXPIRY + " TEXT)";

        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)";

        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE)";

        db.execSQL(createIngredientsTable);
        db.execSQL(createRecipesTable);
        db.execSQL(createRecipeIngredientsTable);

        seedInitialRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }

    // CRUD For Ingredients in Pantry
    public boolean addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ING_NAME, ingredient.getName().trim().toLowerCase());
        cv.put(COL_ING_QTY, ingredient.getQuantity());
        cv.put(COL_ING_UNIT, ingredient.getUnit());
        cv.put(COL_ING_EXPIRY, ingredient.getExpiryDate());

        long result = db.insert(TABLE_INGREDIENTS, null, cv);
        return result != -1;
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INGREDIENTS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ING_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ING_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_EXPIRY));

                list.add(new Ingredient(id, name, qty, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ING_NAME, ingredient.getName().trim().toLowerCase());
        cv.put(COL_ING_QTY, ingredient.getQuantity());
        cv.put(COL_ING_UNIT, ingredient.getUnit());
        cv.put(COL_ING_EXPIRY, ingredient.getExpiryDate());

        int rows = db.update(TABLE_INGREDIENTS, cv, COL_ING_ID + "=?", new String[]{String.valueOf(ingredient.getId())});
        return rows > 0;
    }

    public boolean deleteIngredient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_INGREDIENTS, COL_ING_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // RECIPE SELECTION
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS));

                recipes.add(new Recipe(id, name, instructions));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT));

                list.add(new RecipeIngredient(id, recipeId, name, qty, unit));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // SEED DATABASE WITH 15 RECIPES
    private void seedInitialRecipes(SQLiteDatabase db) {
        insertRecipeWithIngredients(db, "Scrambled Eggs", "Whisk eggs with milk, melt butter on pan, scramble until cooked.",
                new String[]{"egg", "milk", "butter"}, new double[]{2, 50, 10}, new String[]{"pcs", "ml", "g"});

        insertRecipeWithIngredients(db, "French Toast", "Whisk eggs and milk, dip bread, fry on pan until golden.",
                new String[]{"bread", "egg", "milk", "butter"}, new double[]{2, 1, 50, 10}, new String[]{"slices", "pcs", "ml", "g"});

        insertRecipeWithIngredients(db, "Pancakes", "Mix flour, milk, egg, sugar. Pour batter on hot griddle.",
                new String[]{"flour", "milk", "egg", "sugar"}, new double[]{100, 150, 1, 20}, new String[]{"g", "ml", "pcs", "g"});

        insertRecipeWithIngredients(db, "Omelette", "Beat eggs, pour into hot pan, add cheese, fold over.",
                new String[]{"egg", "cheese", "butter"}, new double[]{3, 50, 10}, new String[]{"pcs", "g", "g"});

        insertRecipeWithIngredients(db, "Grilled Cheese", "Butter bread, place cheese in middle, grill until melted.",
                new String[]{"bread", "cheese", "butter"}, new double[]{2, 2, 10}, new String[]{"slices", "slices", "g"});

        insertRecipeWithIngredients(db, "Tuna Sandwich", "Mix tuna with mayo, spread on bread.",
                new String[]{"tuna", "mayonnaise", "bread"}, new double[]{1, 20, 2}, new String[]{"can", "g", "slices"});

        insertRecipeWithIngredients(db, "Tomato Soup", "Simmer tomatoes and garlic, blend until smooth.",
                new String[]{"tomato", "garlic", "water"}, new double[]{3, 2, 500}, new String[]{"pcs", "cloves", "ml"});

        insertRecipeWithIngredients(db, "Fried Rice", "Sauté cold cooked rice with egg, soy sauce, and oil.",
                new String[]{"rice", "egg", "soy sauce", "oil"}, new double[]{200, 2, 15, 10}, new String[]{"g", "pcs", "ml", "ml"});

        insertRecipeWithIngredients(db, "Garlic Bread", "Spread garlic butter on bread, bake until crispy.",
                new String[]{"bread", "butter", "garlic"}, new double[]{4, 30, 2}, new String[]{"slices", "g", "cloves"});

        insertRecipeWithIngredients(db, "Mac and Cheese", "Boil pasta, stir in cheese and milk until creamed.",
                new String[]{"pasta", "cheese", "milk"}, new double[]{150, 100, 100}, new String[]{"g", "g", "ml"});

        insertRecipeWithIngredients(db, "Chicken Salad", "Dice cooked chicken, toss with mayo and seasoning.",
                new String[]{"chicken", "mayonnaise", "salt"}, new double[]{200, 30, 5}, new String[]{"g", "g", "g"});

        insertRecipeWithIngredients(db, "Mashed Potatoes", "Boil potatoes, mash with butter and milk.",
                new String[]{"potato", "butter", "milk"}, new double[]{3, 20, 50}, new String[]{"pcs", "g", "ml"});

        insertRecipeWithIngredients(db, "Boiled Eggs", "Place eggs in boiling water for 8 minutes.",
                new String[]{"egg", "water"}, new double[]{2, 500}, new String[]{"pcs", "ml"});

        insertRecipeWithIngredients(db, "Garlic Pasta", "Boil pasta, toss in olive oil and minced garlic.",
                new String[]{"pasta", "garlic", "oil"}, new double[]{150, 3, 20}, new String[]{"g", "cloves", "ml"});

        insertRecipeWithIngredients(db, "Rice and Beans", "Cook rice, warm canned beans, mix together.",
                new String[]{"rice", "beans"}, new double[]{150, 1}, new String[]{"g", "can"});
    }

    private void insertRecipeWithIngredients(SQLiteDatabase db, String name, String instructions, String[] ingNames, double[] qtys, String[] units) {
        ContentValues cvRecipe = new ContentValues();
        cvRecipe.put(COL_RECIPE_NAME, name);
        cvRecipe.put(COL_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, cvRecipe);

        for (int i = 0; i < ingNames.length; i++) {
            ContentValues cvIng = new ContentValues();
            cvIng.put(COL_RI_RECIPE_ID, recipeId);
            cvIng.put(COL_RI_NAME, ingNames[i]);
            cvIng.put(COL_RI_QTY, qtys[i]);
            cvIng.put(COL_RI_UNIT, units[i]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, cvIng);
        }
    }
}
