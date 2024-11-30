package com.example.globego.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.globego.Domain.CartItem;
import com.example.globego.Domain.ItemDomain;
import com.example.globego.Manager.CartManager;
import com.example.globego.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private ItemDomain object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CartManager.loadCart(this,userId);

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    CartItem newItem = new CartItem(
                            object.getTitle(),
                            object.getAddress(),
                            object.getPrice(),
                            String.valueOf(object.getScore()),
                            object.getPic(),
                            object.getDuration(),
                            object.getTimeTour(),
                            object.getDateTour(),
                            object.getTourGuideName(),
                            object.getTourGuidePic(),
                            object.getBed()
                    );

                    if (!CartManager.isItemInCart(newItem)) {
                        CartManager.addToCart(newItem,DetailActivity.this,userId);
                        Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(DetailActivity.this, "Item already added to cart", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(DetailActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        getIntentExtra();
        setVariable();
        findDistance();
    }
    private void findDistance() {
        binding.distanceConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get the destination address from addressTxt
                    String destinationAddress = binding.addressTxt.getText().toString().trim();

                    // Create a URI for the intent
                    String uri = "http://maps.google.com/maps?saddr=my location&daddr=" + destinationAddress;

                    // Create an intent with ACTION_VIEW and the URI
                    Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));

                    // Set the package to ensure Google Maps is opened
                    intent.setPackage("com.google.android.apps.maps");

                    // Check if there's an app to handle the intent (Google Maps)
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(DetailActivity.this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DetailActivity.this, "Failed to open Google Maps.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setVariable() {

        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$"+object.getPrice());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.distanceTxt.setText(object.getDistance());
        binding.bedTxt.setText(""+object.getBed());
        binding.durationTxt.setText(object.getDuration());
        binding.descriptionTxt.setText(object.getDescription());
        binding.addressTxt.setText(object.getAddress());
        binding.ratingTxt.setText(object.getScore()+" Rating");
        binding.ratingBar.setRating ((float) object.getScore());

        Glide.with(DetailActivity.this)
                .load(object.getPic())
                .into(binding.pic);
    }

    private void getIntentExtra() {
        object= (ItemDomain) getIntent().getSerializableExtra("object");
    }


}