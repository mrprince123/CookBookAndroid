package com.cook.cookbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cook.cookbook.Adapter.RecipeSpecificAdapter;
import com.cook.cookbook.Models.Ingredient;
import com.cook.cookbook.Models.Recipe;
import com.cook.cookbook.R;
import com.cook.cookbook.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    RecipeSpecificAdapter recipeSpecificAdapter;
    ArrayList<Recipe> recipes;
    String catName;
    RecyclerView recipeSpecificRecycle;
    TextView catNameText, noRecipeText;

    LinearLayout loadingRecipeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        recipeSpecificRecycle = findViewById(R.id.recipe_list_recycle_view);

        Intent intent = getIntent();
        catName = intent.getStringExtra("categoryName");
        catNameText = findViewById(R.id.cat_name_text);
        catNameText.setText(catName);
        noRecipeText = findViewById(R.id.no_recipe_text);
        noRecipeText.setVisibility(View.GONE);
        loadingRecipeData = findViewById(R.id.loading_recipe_data);

        initRecipe();
    }

    void initRecipe(){
        recipes = new ArrayList<>();
        recipeSpecificAdapter = new RecipeSpecificAdapter(this, recipes);

        getRecipe();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recipeSpecificRecycle.setLayoutManager(gridLayoutManager);
        recipeSpecificRecycle.setAdapter(recipeSpecificAdapter);
    }

    void getRecipe(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.ALL_RECIPE;
        loadingRecipeData.setVisibility(View.VISIBLE); // Show the Loading Screen

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECIPE HOME", response);
                loadingRecipeData.setVisibility(View.GONE);
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("success").equals("true")){
                        JSONArray recipeArray =  mainObj.getJSONArray("data");

                        // Clear the Recipes List in case of multiple API calls
                        recipes.clear();

                        for(int i = 0; i<recipeArray.length(); i++){
                            JSONObject object = recipeArray.getJSONObject(i);
                            JSONObject categoryObj = object.getJSONObject("categoryId");

                            String categoryName = categoryObj.getString("categoryName");

                            // Write the Logic
                            // Only show the data whose categoryName match with catName

                            if(categoryName.equalsIgnoreCase(catName)){
                                // Extracting ingredients array
                                JSONArray ingredientsArray = object.getJSONArray("ingredients");
                                List<Ingredient> ingredientList = new ArrayList<>();
                                for(int j = 0; j < ingredientsArray.length(); j++){
                                    JSONObject ingredientObj = ingredientsArray.getJSONObject(j);
                                    Ingredient ingredient = new Ingredient(
                                            ingredientObj.getString("name"),
                                            ingredientObj.getString("quantity")
                                    );
                                    ingredientList.add(ingredient);
                                }

                                Recipe recipe = new Recipe(
                                        object.getString("name"),
                                        object.getString("image"),
                                        object.getString("videoUrl"),
                                        object.getString("cooking_time"),
                                        object.getString("foodType"),
                                        object.getString("description"),
                                        object.getString("chef"),
                                        ingredientList,
                                        categoryName
                                );
                                recipes.add(recipe);
                            }
                        }

                        // Check if the recipes list is empty after filtering
                        if (recipes.isEmpty()) {
                            // No recipes found for the selected category
                            noRecipeText.setVisibility(View.VISIBLE);
                            recipeSpecificRecycle.setVisibility(View.GONE);
                        } else {
                            // Recipes are found, show the list
                            noRecipeText.setVisibility(View.GONE);
                            recipeSpecificRecycle.setVisibility(View.VISIBLE);
                        }
                        recipeSpecificAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                loadingRecipeData.setVisibility(View.VISIBLE); // Show the Loading Screen
            }
        });
        queue.add(stringRequest);
    }

}