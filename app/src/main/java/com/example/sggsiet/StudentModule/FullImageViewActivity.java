package com.example.sggsiet.StudentModule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;

public class FullImageViewActivity extends AppCompatActivity {
    private ImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        fullImageView = findViewById(R.id.fullImageView);

        // Get image URL from intent
        String imageUrl = getIntent().getStringExtra("image_url");

        // Load image in full screen using Glide
        Glide.with(this).load(imageUrl).into(fullImageView);
    }
}
