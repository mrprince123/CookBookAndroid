package com.cook.cookbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cook.cookbook.Activity.FullRecipeActivity;
import com.cook.cookbook.Models.Ingredient;
import com.cook.cookbook.Models.Recipe;
import com.cook.cookbook.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeAdapterHome extends RecyclerView.Adapter<RecipeAdapterHome.RecipeViewHolder> {
    private final Context context;
    private final ArrayList<Recipe> recipes;

    public RecipeAdapterHome(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeAdapterHome.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeAdapterHome.RecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recipe_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapterHome.RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());
        holder.recipeTime.setText(recipe.getCooking_time());
        Glide.with(context).load(recipe.getImage()).into(holder.recipeImage);

        holder.itemView.setOnClickListener(view -> {
            // Create an intent to open FullRecipeActivity
            Intent intent = new Intent(context, FullRecipeActivity.class);

            // Pass the recipe details via intent
            intent.putExtra("name", recipe.getName());
            intent.putExtra("image", recipe.getImage());
            intent.putExtra("cooking_time", recipe.getCooking_time());
            intent.putExtra("chef", recipe.getChef());
            intent.putExtra("foodType", recipe.getFoodType());
            intent.putExtra("videoUrl", recipe.getVideoUrl());
            intent.putExtra("description", recipe.getDescription());
            intent.putExtra("categoryName", recipe.getCategoryName());

            // Prepare and pass the ingredients list
            ArrayList<HashMap<String, String>> ingredientsList = new ArrayList<>();
            for (Ingredient ingredient : recipe.getIngredients()) {
                HashMap<String, String> ingredientMap = new HashMap<>();
                ingredientMap.put("name", ingredient.getName());
                ingredientMap.put("quantity", ingredient.getQuantity());
                ingredientsList.add(ingredientMap);
            }

            // Passing the ingredients list
            intent.putExtra("ingredients", ingredientsList);

            // Start the FullRecipeActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{

        ImageView recipeImage;
        TextView recipeName, recipeTime;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_home_name);
            recipeImage = itemView.findViewById(R.id.recipe_home_image);
            recipeTime = itemView.findViewById(R.id.recipe_home_time);
        }
    }
}

