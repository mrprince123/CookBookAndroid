package com.cook.cookbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
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
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeActivity extends AppCompatActivity {

    RecipeSpecificAdapter recipeSpecificAdapter;
    ArrayList<Recipe> recipes;
    String catName;
    RecyclerView recipeSpecificRecycle;
    TextView catNameText, noRecipeText;

    EditText searchRecipeInputCategory;

    ArrayList<Recipe> filterList;

    ShimmerFrameLayout shimmerCategoryRecipe;

    ImageView voiceSearch;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    String voiceSearchValue;

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
        shimmerCategoryRecipe = findViewById(R.id.shimmer_category_recipe);

        searchRecipeInputCategory = findViewById(R.id.search_recipe_input_category);

        searchRecipeInputCategory.setOnClickListener(view -> {
            String SearchRecipeValue = searchRecipeInputCategory.getText().toString();
            filterRecipe(SearchRecipeValue);
        });

        voiceSearch = findViewById(R.id.voice_search);
        voiceSearch.setOnClickListener(view -> {
            Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent2.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Search Recipe");

            try {
                startActivityForResult(intent2, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e){
                Toast
                        .makeText(this, " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        initRecipe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(result != null && result.size() > 0){
                    voiceSearchValue = result.get(0);

                    searchRecipeInputCategory.setText(voiceSearchValue);

                    // Trigger the Search with the recognized voice input
                    filterRecipe(voiceSearchValue);
                }
            }
        }
    }

    void initRecipe(){
        recipes = new ArrayList<>();
        recipeSpecificAdapter = new RecipeSpecificAdapter(this, recipes);
        filterList = new ArrayList<>();

        getRecipe();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recipeSpecificRecycle.setLayoutManager(gridLayoutManager);
        recipeSpecificRecycle.setAdapter(recipeSpecificAdapter);
    }

    void getRecipe(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.ALL_RECIPE;

        shimmerCategoryRecipe.startShimmer();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECIPE HOME", response);

                shimmerCategoryRecipe.stopShimmer();
                shimmerCategoryRecipe.setVisibility(View.GONE);
                catNameText.setVisibility(View.VISIBLE);
                recipeSpecificRecycle.setVisibility(View.VISIBLE);
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

            }
        });
        queue.add(stringRequest);
    }

    void filterRecipe(String reciepValue){
        filterList.clear();
        for(Recipe recipe : recipes){
            if(recipe.getName().toLowerCase().contains(reciepValue)){
                filterList.add(recipe);
            }
        }

        if(filterList.isEmpty()){
            Toast.makeText(this, "No Recipe Found with Provided Name", Toast.LENGTH_LONG).show();
        }

        recipeSpecificAdapter.updateRecipe(filterList);
    }

}