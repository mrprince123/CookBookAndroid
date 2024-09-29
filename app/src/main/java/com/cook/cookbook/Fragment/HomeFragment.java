package com.cook.cookbook.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.cook.cookbook.Adapter.RecipeAdapter;
import com.cook.cookbook.Models.Category;
import com.cook.cookbook.Models.Ingredient;
import com.cook.cookbook.Models.Recipe;
import com.cook.cookbook.R;
import com.cook.cookbook.Utils.Constants;
import com.cook.cookbook.databinding.FragmentHomeBinding;

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
    ArrayList<Recipe> recipeList;
    PopularRecipeAdapter popularRecipeAdapter;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecycleView = view.findViewById(R.id.category_recycle_view);
        imageCarousel = view.findViewById(R.id.carousel_image);
        popularRecipeRecyle = view.findViewById(R.id.popular_recipe_recycle_view);
        initCategory();
        initRecipe();
        setCarouselImage();
        return view;
    }

    void setCarouselImage(){

        imageCarousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem("https://miro.medium.com/v2/resize:fit:1400/1*eWbNRY_UnGFJC5YqcqBSwA.jpeg", "Carousel 1"));
        list.add(new CarouselItem("https://www.businessofapps.com/wp-content/uploads/2022/01/emizen_tech_food_deliver_img1.png", "Carousel 2"));
        list.add(new CarouselItem("https://i.pinimg.com/originals/c3/23/d5/c323d5d60144d9bb244221a9550e8b4a.png", "Carousel 3"));
        list.add(new CarouselItem("https://cdn.dribbble.com/users/8299015/screenshots/16963977/food_app_screen_1.jpg", "Carousel 4"));

        imageCarousel.setData(list);
    }

    void initCategory(){
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categoryList);

        getAllCategory();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        categoryRecycleView.setLayoutManager(layoutManager);
        categoryRecycleView.setAdapter(adapter);
    }

    void getAllCategory() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.ALL_CATEGORY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE", response);
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

    void initRecipe(){
        recipeList = new ArrayList<>();
        popularRecipeAdapter = new PopularRecipeAdapter(getContext(), recipeList);

        getRecipe();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        popularRecipeRecyle.setLayoutManager(layoutManager);
        popularRecipeRecyle.setAdapter(popularRecipeAdapter);
    }

    void getRecipe(){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Constants.ALL_RECIPE;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECIPE HOME", response);
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
                            recipeList.add(recipe);
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
}