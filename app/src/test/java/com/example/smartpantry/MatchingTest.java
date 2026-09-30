package com.example.smartpantry;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.MatchingEngine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MatchingTest {

    private Recipe recipeNeeding(String... ingredientSpecs) {
        Recipe recipe = new Recipe(1, "Test Recipe", "instructions");
        for (String spec : ingredientSpecs) {
            String[] parts = spec.split(":");
            recipe.addIngredient(parts[0], Double.parseDouble(parts[1]), parts[2]);
        }
        return recipe;
    }

    private List<PantryItem> pantryOf(String... itemSpecs) {
        List<PantryItem> items = new ArrayList<>();
        int id = 1;
        for (String spec : itemSpecs) {
            String[] parts = spec.split(":");
            items.add(new PantryItem(id++, parts[0], Double.parseDouble(parts[1]), parts[2], null));
        }
        return items;
    }

    @Test
    public void completePantry_yieldsCompleteMatch() {
        Recipe recipe = recipeNeeding("egg:2:pcs", "flour:200:g", "milk:100:ml");
        List<PantryItem> pantry = pantryOf("egg:3:pcs", "flour:500:g", "milk:250:ml");
        assertEquals(MatchingEngine.MatchResult.COMPLETE_MATCH, MatchingEngine.evaluate(recipe, pantry));
    }

    @Test
    public void missingOneIngredient_isAlmostThereNotFullMatch() {
        Recipe recipe = recipeNeeding("egg:2:pcs", "flour:200:g", "milk:100:ml", "sugar:50:g", "butter:20:g");
        List<PantryItem> pantry = pantryOf("egg:3:pcs", "flour:500:g", "milk:250:ml", "sugar:100:g");
        assertEquals(MatchingEngine.MatchResult.ALMOST_MATCH, MatchingEngine.evaluate(recipe, pantry));
    }

    @Test
    public void missingTwoIngredients_isNoMatch() {
        Recipe recipe = recipeNeeding("egg:2:pcs", "flour:200:g", "milk:100:ml");
        List<PantryItem> pantry = pantryOf("egg:3:pcs");
        assertEquals(MatchingEngine.MatchResult.NO_MATCH, MatchingEngine.evaluate(recipe, pantry));
    }

    @Test
    public void pluralVsSingular_stillMatches() {
        Recipe recipe = recipeNeeding("tomato:2:pcs", "onion:1:pcs");
        List<PantryItem> pantry = pantryOf("Tomatoes:3:pcs", "Onions:2:pcs");
        assertEquals(MatchingEngine.MatchResult.COMPLETE_MATCH, MatchingEngine.evaluate(recipe, pantry));
    }

    @Test
    public void insufficientQuantity_isNotSatisfied() {
        Recipe recipe = recipeNeeding("flour:500:g");
        List<PantryItem> pantry = pantryOf("flour:200:g");
        assertEquals(MatchingEngine.MatchResult.NO_MATCH, MatchingEngine.evaluate(recipe, pantry));
    }

    @Test
    public void unitConversion_kgSatisfiesGramRequirement() {
        Recipe recipe = recipeNeeding("flour:500:g");
        List<PantryItem> pantry = pantryOf("flour:1:kg");
        assertEquals(MatchingEngine.MatchResult.COMPLETE_MATCH, MatchingEngine.evaluate(recipe, pantry));
    }
}