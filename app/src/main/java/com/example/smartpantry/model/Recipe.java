package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private long id;
    private String name;
    private String instructions;
    private List<RequiredIngredient> ingredients = new ArrayList<>();

    public Recipe() {}

    public Recipe(long id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSteps() {
        return instructions;
    }
    public void setSteps(String instructions) {
        this.instructions = instructions;
    }

    public List<RequiredIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RequiredIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(String name, double quantity, String unit) {
        this.ingredients.add(new RequiredIngredient(name, quantity, unit));
    }


    public static List<RequiredIngredient> deserializeIngredients(String rawData) {
        List<RequiredIngredient> list = new ArrayList<>();
        if (rawData == null || rawData.trim().isEmpty()) return list;

        String[] parts = rawData.split(";");
        for (String part : parts) {
            String[] details = part.split(",");
            if (details.length >= 3) {
                try {
                    String name = details[0].trim();
                    double qty = Double.parseDouble(details[1].trim());
                    String unit = details[2].trim();
                    list.add(new RequiredIngredient(name, qty, unit));
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    public static class RequiredIngredient {
        private final String name;
        private final double quantity;
        private final String unit;

        public RequiredIngredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public double getQuantity() {
            return quantity;
        }

        public String getUnit() {
            return unit;
        }
    }
}