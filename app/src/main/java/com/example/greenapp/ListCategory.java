package com.example.greenapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ListCategory extends AppCompatActivity {

    ImageView veggieImage, fruitImage, grainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.listview);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://greenapplication-4f493.appspot.com").child("fruit.jpg");
        veggieImage = (ImageView) findViewById(R.id.veggies);
        Glide.with(getApplicationContext())
                .load(storageRef)
                .into(veggieImage);

        //Picasso.get().load(veggieUrl).into(veggieImage);
        veggieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Veggies", Toast.LENGTH_SHORT).show();
                Intent veggieIntent = new Intent(ListCategory.this, HomeActivity.class);
                startActivity(veggieIntent);
            }
        });
        fruitImage =  findViewById(R.id.fruits);
        //Picasso.get().load(fruitUrl).into(fruitImage);
        fruitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Fruits", Toast.LENGTH_SHORT).show();
                Intent fruitIntent = new Intent(ListCategory.this, HomeActivity.class);
                startActivity(fruitIntent);
            }
        });
        grainImage =  findViewById(R.id.grains);
       // Picasso.get().load(grainUrl).into(grainImage);
        grainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Grains", Toast.LENGTH_SHORT).show();
                Intent grainIntent = new Intent(ListCategory.this, HomeActivity.class);
                startActivity(grainIntent);
            }
        });
    }
}

