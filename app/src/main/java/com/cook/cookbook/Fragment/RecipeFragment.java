package com.cook.cookbook.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cook.cookbook.Adapter.RecipeAdapter;
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

public class RecipeFragment extends Fragment {

    RecyclerView recipeRecycleView;
    ArrayList<Recipe> recipeList;
    RecipeAdapter adapter;

    ArrayList<Recipe> filterList;

    LinearLayout breakfast, lunch, dinner;

    ShimmerFrameLayout shimmerLayoutRecipe;

    EditText searchRecipeInput;

    ImageView voiceSearch;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    String voiceSearchValue = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        recipeRecycleView = view.findViewById(R.id.recipe_recycle_view);

        breakfast = view.findViewById(R.id.breakfast);
        lunch = view.findViewById(R.id.lunch);
        dinner = view.findViewById(R.id.dinner);

        shimmerLayoutRecipe = view.findViewById(R.id.shimmer_Layout_recipe);
        shimmerLayoutRecipe.startShimmer();

        searchRecipeInput = view.findViewById(R.id.search_recipe_input);
        voiceSearch = view.findViewById(R.id.voice_search_fragment);
        voiceSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Search Recipe");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e){
                Toast
                        .makeText(getContext(), " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        searchRecipeInput.setOnClickListener(view1 -> {
            String searchValue = searchRecipeInput.getText().toString().toLowerCase().trim();
            filterSearch(searchValue);
        });

        initRecipe();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(result != null && result.size() > 0){
                    voiceSearchValue = result.get(0);

                    searchRecipeInput.setText(voiceSearchValue);

                    // Optionally, show a message with the recognized text
//                    Toast.makeText(getContext(), "Showing Result : " + voiceSearchValue, Toast.LENGTH_SHORT).show();

                    // Trigger the Search with the recognized voice input
                    filterSearch(voiceSearchValue);
                }
            }
        }
    }

    void initRecipe(){
        recipeList = new ArrayList<>();
        filterList = new ArrayList<>(); // Initialize the filtered list
        adapter = new RecipeAdapter(getContext(), recipeList);
        setupCategoryClickListeners();
        getRecipe(null);  // Load all recipes initially
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recipeRecycleView.setLayoutManager(layoutManager);
        recipeRecycleView.setAdapter(adapter);
    }

    void filterSearch(String searchValue){
        filterList.clear();
        for(Recipe recipe : recipeList){
            if(recipe.getName().toLowerCase().contains(searchValue)){
                filterList.add(recipe);
            }
        }

        if(filterList.isEmpty()){
            Toast.makeText(getContext(), "No Recipe Found with Provided Name", Toast.LENGTH_LONG).show();
        }
        adapter.updateRecipeList(filterList);
    }

    // Setting up click listeners for each category
    void setupCategoryClickListeners() {
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecipe("breakfast");
            }
        });

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecipe("lunch");
            }
        });

        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecipe("dinner");
            }
        });
    }

    void getRecipe(final String foodType){
        recipeList.clear(); // Clear the list before adding the new itsms
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.ALL_RECIPE;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECIPE", response);

                shimmerLayoutRecipe.stopShimmer();
                shimmerLayoutRecipe.setVisibility(View.GONE);

                recipeRecycleView.setVisibility(View.VISIBLE);

                try {
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("success").equals("true")){
                        JSONArray recipeArray =  mainObj.getJSONArray("data");
                        for(int i = 0; i<recipeArray.length(); i++){

                            JSONObject object = recipeArray.getJSONObject(i);
                            JSONObject categoryObj = object.getJSONObject("categoryId");
                            String categoryName = categoryObj.getString("categoryName");

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

                            if(foodType == null || object.getString("foodType").equals(foodType)){
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
                                recipeList.add(recipe);
                            }
                        }
                        adapter.notifyDataSetChanged();
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
}