package com.cook.cookbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.cook.cookbook.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;

public class FullRecipeActivity extends AppCompatActivity {

    TextView recipeName, recipeCookingTime, recipeFoodType, recipeChef, recipeCategory, recipeDescription, youtubePlayer;
    ImageView recipeImage;
    LinearLayout ingredientsContainer;
    YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full_recipe);

        getSupportActionBar().hide();

        // Ensure that your layout is not cut off by the status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        recipeName = findViewById(R.id.full_recipe_name);
        recipeImage = findViewById(R.id.full_recipe_image);
        recipeCookingTime = findViewById(R.id.full_recipe_time);
        recipeFoodType = findViewById(R.id.full_recipe_foodType);
        recipeChef = findViewById(R.id.full_recipe_chef);
        recipeCategory = findViewById(R.id.full_recipe_category);
        recipeDescription = findViewById(R.id.full_recipe_description);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        youtubePlayerView = findViewById(R.id.full_recipe_videoPlay);

        Intent intent = getIntent();

        // Set data to views
        recipeName.setText(intent.getStringExtra("name"));
        recipeCookingTime.setText("Cooking Time :" + intent.getStringExtra("cooking_time"));
        recipeFoodType.setText("Food Type " + intent.getStringExtra("foodType"));
        recipeChef.setText("Chef:" + intent.getStringExtra("chef"));
        recipeCategory.setText("Category : " + intent.getStringExtra("categoryName"));
        recipeDescription.setText(intent.getStringExtra("description"));

        // Load the recipe image using Glide (or any other image loading library)
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .into(recipeImage);


        // Populate ingredients
        ArrayList<HashMap<String, String>> ingredientsList = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("ingredients");
        populateIngredients(ingredientsList);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer){
                String videoId = extractVideoId(intent.getStringExtra("videoUrl"));
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    // Extract the video ID from a YouTube URL
    private String extractVideoId(String videoUrl) {
        String videoId = "";
        if (videoUrl != null && videoUrl.contains("v=")) {
            videoId = videoUrl.substring(videoUrl.indexOf("v=") + 2);
            if (videoId.contains("&")) {
                videoId = videoId.substring(0, videoId.indexOf("&")); // Handle additional parameters
            }
        } else if (videoUrl != null && videoUrl.contains("youtu.be/")) {
            // Handle the shortened YouTube URL format
            videoId = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
        }
        return videoId;
    }

    // Method to populate ingredients dynamically
    private void populateIngredients(ArrayList<HashMap<String, String>> ingredientsList) {
        // Clear previous ingredients
        ingredientsContainer.removeAllViews();

        for (HashMap<String, String> ingredient : ingredientsList) {
            // Create TextView for the ingredient name
            TextView ingredientNameView = new TextView(this);
            ingredientNameView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ingredientNameView.setText(ingredient.get("name") + " : " +  ingredient.get("quantity"));

            // Add the ingredient views to the container
            ingredientsContainer.addView(ingredientNameView);
        }
    }
}