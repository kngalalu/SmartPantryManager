package com.example.smartpantry.util;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {
    public enum MatchResult {
        COMPLETE_MATCH,
        ALMOST_MATCH,
        NO_MATCH
    }

    public static List<Recipe> findCompleteMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> completeMatches = new ArrayList<>();
        if (recipes == null || pantry == null) {
            return completeMatches;
        }

        for (Recipe recipe : recipes) {
            if (evaluate(recipe, pantry) == MatchResult.COMPLETE_MATCH) {
                completeMatches.add(recipe);
            }
        }
        return completeMatches;
    }

    public static List<Recipe> findAlmostMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> almostMatches = new ArrayList<>();
        if (recipes == null || pantry == null) {
            return almostMatches;
        }

        for (Recipe recipe : recipes) {
            if (evaluate(recipe, pantry) == MatchResult.ALMOST_MATCH) {
                almostMatches.add(recipe);
            }
        }
        return almostMatches;
    }

    public static MatchResult evaluate(Recipe recipe, List<PantryItem> pantry) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return MatchResult.NO_MATCH;
        }

        List<Recipe.RequiredIngredient> requirements = recipe.getIngredients();
        int matchedResultCount = 0;

        for (Recipe.RequiredIngredient req : requirements) {
            boolean happyResult = false;
            for (PantryItem item: pantry) {
                if (normalize(item.getName()).equals(normalize(req.getName()))) {
                    double convertedPantryQty = convertQuantity(item.getQuantity(), item.getUnit(), req.getUnit());
                    if (convertedPantryQty >= req.getQuantity()) {
                        happyResult = true;
                        break;
                    }
                }
            }
            if (happyResult) {
                matchedResultCount++;
            }
        }

        if (matchedResultCount == requirements.size()) {
            return MatchResult.COMPLETE_MATCH;
        } else if (matchedResultCount == requirements.size() - 1 && requirements.size() > 1) {
            return MatchResult.ALMOST_MATCH;
        } else {
            return MatchResult.NO_MATCH;
        }
    }

    private static double convertQuantity(double qty, String pantryUnit, String reqUnit) {
        if (pantryUnit == null || reqUnit == null) {
            return qty;
        }

        String pU = pantryUnit.trim().toLowerCase();
        String rU = reqUnit.trim().toLowerCase();

        if (pU.equals("kg") && rU.equals("g")) {
            return qty * 1000;
        }
        if (pU.equals("g") && rU.equals("kg")) {
            return qty / 1000;
        }
        if (pU.equals("l") && rU.equals("ml")) {
            return qty * 1000;
        }
        if (pU.equals("ml") && rU.equals("l")) {
            return qty / 1000;
        }

        return qty;
    }

    private static String normalize(String name) {
        if (name == null) {
            return "";
        }

        String s = name.trim().toLowerCase();
        if (s.endsWith("es")) {
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("s")) {
            return s.substring(0, s.length() - 1);
        }

        return s;
    }
}
