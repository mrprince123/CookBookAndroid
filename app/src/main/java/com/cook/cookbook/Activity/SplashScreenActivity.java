package com.cook.cookbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cook.cookbook.MainActivity;
import com.cook.cookbook.R;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView splashLogo;
    TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        splashLogo = findViewById(R.id.splash_logo);
        splashText = findViewById(R.id.splash_text);

        splashAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    void splashAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.side_slide);
        splashLogo.startAnimation(animation);

        Animation animationText = AnimationUtils.loadAnimation(this, R.anim.up_slide);
        splashText.setAnimation(animationText);
    }
}


//These are the steps
//1. show the activty for the 3 seconds
//2. then navigate to the home page
//3. close the activity and never come back
