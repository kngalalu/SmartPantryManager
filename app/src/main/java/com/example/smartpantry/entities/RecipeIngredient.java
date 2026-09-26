package com.example.smartpantry.entities;

public class RecipeIngredient {
    private int id;
    private int recipeId;
    private String ingredientName;
    private double quantityRequired;
    private String unit;

    public RecipeIngredient(int id, int recipeId, String ingredientName, double quantityRequired, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }
    public int getRecipeId() {
        return recipeId;
    }
    public String getIngredientName() {
        return ingredientName;
    }
    public double getQuantityRequired() {
        return quantityRequired;
    }
    public String getUnit() {
        return unit;
    }
}
