package com.cook.cookbook.Fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cook.cookbook.Adapter.CategoryAdapter;
import com.cook.cookbook.Adapter.PopularRecipeAdapter;
import com.cook.cookbook.Adapter.RecipeAdapterHome;
import com.cook.cookbook.Models.Category;
import com.cook.cookbook.Models.Ingredient;
import com.cook.cookbook.Models.Recipe;
import com.cook.cookbook.R;
import com.cook.cookbook.Utils.Constants;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView categoryRecycleView;

    ArrayList<Category> categoryList;
    CategoryAdapter adapter;

    ImageCarousel imageCarousel;

    RecyclerView popularRecipeRecyle;
    ArrayList<Recipe> recipePopularList;
    PopularRecipeAdapter popularRecipeAdapter;

    ImageView languageChange;

    RecyclerView homeRecipeRecycle;
    ArrayList<Recipe> recipeList;
    RecipeAdapterHome recipeAdapterHome;

    TextView allRecipe;


    LinearLayout  loadingAllRecipe;

    ShimmerFrameLayout categoryLayoutShimmer, popularRecipeShimmer, allRecipeShimmer, shimmerCarousel;

    CardView carouselCard;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecycleView = view.findViewById(R.id.category_recycle_view);
        imageCarousel = view.findViewById(R.id.carousel_image);
        popularRecipeRecyle = view.findViewById(R.id.popular_recipe_recycle_view);
        homeRecipeRecycle = view.findViewById(R.id.all_recipe_recycle_view_home);
        languageChange = view.findViewById(R.id.language_change_logo);
        categoryLayoutShimmer = view.findViewById(R.id.category_layout_shimmer);
        popularRecipeShimmer =view.findViewById(R.id.popular_recipe_shimmer);
        allRecipeShimmer = view.findViewById(R.id.all_recipe_shimmer);
        shimmerCarousel = view.findViewById(R.id.carousel_shimmer);
        carouselCard = view.findViewById(R.id.carousel_card);

        languageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Welcome to Cook Help : Your Recipe Master", Toast.LENGTH_SHORT).show();
            }
        });

        allRecipe = view.findViewById(R.id.view_all_recipe);

        setCarouselImage();
        initCategory();
        initPopularRecipe();
        initRecipe();
        return view;
    }

    // To set Carousel
    void setCarouselImage(){

        List<CarouselItem> list = new ArrayList<>();
        // Get data from backend
        imageCarousel.registerLifecycle(getLifecycle());

        RequestQueue carouselQueue = Volley.newRequestQueue(getContext());
        String url = Constants.ALL_CAROUSEL;
        shimmerCarousel.startShimmer();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    try {
                        JSONObject mainObj = new JSONObject(response);
                        Log.e("CAROUSEL", response);
                        shimmerCarousel.stopShimmer();
                        shimmerCarousel.setVisibility(View.GONE);

                        carouselCard.setVisibility(View.VISIBLE);

                        if(mainObj.getString("success").equals("true")){
                            JSONArray carouselData = mainObj.getJSONArray("data");

                            for(int i = 0; i<carouselData.length(); i++){
                                JSONObject object = carouselData.getJSONObject(i);
                                String carouseImageUrl = object.getString("imageUrl");
                                String carouseCaption = object.getString("caption");
                                list.add(new CarouselItem(carouseImageUrl, carouseCaption));
                            }
                            imageCarousel.setData(list);
                        }
                    } catch (JSONException e){
                            e.printStackTrace();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log or handle the error (e.g., show a Toast or retry mechanism)
                error.printStackTrace();
            }
        });
        carouselQueue.add(stringRequest);
    }

    // To Set the category Data to the Layout
    void initCategory(){
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categoryList);

        getAllCategory();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        categoryRecycleView.setLayoutManager(layoutManager);
        categoryRecycleView.setAdapter(adapter);
    }

    // To Get all the Category
    void getAllCategory() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.ALL_CATEGORY;

        categoryLayoutShimmer.startShimmer();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE", response);

                categoryRecycleView.setVisibility(View.VISIBLE);
                categoryLayoutShimmer.stopShimmer();
                categoryLayoutShimmer.setVisibility(View.GONE);
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("success").equals("true")){
                        JSONArray categoryArray = mainObj.getJSONArray("data");
                        for(int i = 0; i< categoryArray.length(); i++){
                            JSONObject object = categoryArray.getJSONObject(i);
                            Category category = new Category(
                                    object.getString("categoryName"),
                                    object.getString("categoryImage")
                            );
                            categoryList.add(category);
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
                // Do Nothing
                Log.e("Error", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    // To set the Recipe to the Popular Recipe
    void initPopularRecipe(){
        recipePopularList = new ArrayList<>();
        popularRecipeAdapter = new PopularRecipeAdapter(getContext(), recipePopularList);

        getPopularRecipe();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        popularRecipeRecyle.setLayoutManager(layoutManager);
        popularRecipeRecyle.setAdapter(popularRecipeAdapter);
    }

    // To get all the Popular
    void getPopularRecipe(){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.POPULAR_RECIPE;

        popularRecipeShimmer.startShimmer();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECIPE HOME", response);
                popularRecipeShimmer.stopShimmer();
                popularRecipeShimmer.setVisibility(View.GONE);
                popularRecipeRecyle.setVisibility(View.VISIBLE);
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
                            recipePopularList.add(recipe);
                        }
                        popularRecipeAdapter.notifyDataSetChanged();
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

    // To get all the Recipe
    void initRecipe(){
        recipeList = new ArrayList<>();
        recipeAdapterHome = new RecipeAdapterHome(getContext(), recipeList);

        getRecipe();

        LinearLayoutManager layoutManagerrecipe = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        homeRecipeRecycle.setLayoutManager(layoutManagerrecipe);
        homeRecipeRecycle.setAdapter(recipeAdapterHome);
    }

    // To get all the Recipe
    void getRecipe(){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.ALL_RECIPE;

        allRecipeShimmer.startShimmer();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECIPE HOME", response);

                allRecipeShimmer.stopShimmer();
                allRecipeShimmer.setVisibility(View.GONE);
                homeRecipeRecycle.setVisibility(View.VISIBLE);
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("success").equals("true")){
                        JSONArray recipeArray =  mainObj.getJSONArray("data");
                        for(int i = 0; i<10; i++){

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
                        recipeAdapterHome.notifyDataSetChanged();
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