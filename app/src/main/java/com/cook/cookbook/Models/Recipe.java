package com.cook.cookbook.Models;

import java.util.List;

public class Recipe {

    private String name;
    private String image;
    private String videoUrl;
    private String cooking_time;
    private String foodType;
    private String description;
    private String chef;
    private List<Ingredient> ingredients;  // Change to List of Ingredient
    private String categoryName;

    // Updated constructor to accept List<Ingredient>
    public Recipe(String name, String image, String videoUrl, String cooking_time, String foodType, String description, String chef, List<Ingredient> ingredients, String categoryName) {
        this.name = name;
        this.image = image;
        this.videoUrl = videoUrl;
        this.cooking_time = cooking_time;
        this.foodType = foodType;
        this.description = description;
        this.chef = chef;
        this.ingredients = ingredients;  // Store ingredients as a list
        this.categoryName = categoryName;
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCooking_time() {
        return cooking_time;
    }

    public void setCooking_time(String cooking_time) {
        this.cooking_time = cooking_time;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChef() {
        return chef;
    }

    public void setChef(String chef) {
        this.chef = chef;
    }

    public List<Ingredient> getIngredients() {  // Updated getter to return List<Ingredient>
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {  // Updated setter
        this.ingredients = ingredients;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
