package com.example.smartpantry.utils;

import com.example.smartpantry.entities.Ingredient;
import com.example.smartpantry.entities.RecipeIngredient;

import java.util.List;

public class RecipeMatcher {

    public static boolean canMakeRecipe(List<Ingredient> pantry, List<RecipeIngredient> recipeRequirements) {
        if (recipeRequirements == null || recipeRequirements.isEmpty()) {
            return false;
        }

        for (RecipeIngredient required : recipeRequirements) {
            boolean ingredientFoundAndSufficient = false;

            for (Ingredient pantryItem : pantry) {
                if (isNameMatch(pantryItem.getName(), required.getIngredientName())
                        && pantryItem.getQuantity() >= required.getQuantityRequired()) {
                    ingredientFoundAndSufficient = true;
                    break;
                }
            }

            if (!ingredientFoundAndSufficient) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNameMatch(String pantryName, String requiredName) {
        if (pantryName == null || requiredName == null) return false;

        String p = pantryName.trim().toLowerCase();
        String r = requiredName.trim().toLowerCase();

        if (p.endsWith("es")) p = p.substring(0, p.length() - 2);
        else if (p.endsWith("s")) p = p.substring(0, p.length() - 1);

        if (r.endsWith("es")) r = r.substring(0, r.length() - 2);
        else if (r.endsWith("s")) r = r.substring(0, r.length() - 1);

        return p.equals(r);
    }
}