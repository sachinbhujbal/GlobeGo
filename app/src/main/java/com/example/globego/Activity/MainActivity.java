package com.example.globego.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.globego.Adapter.CartAdapter;
import com.example.globego.Adapter.CategoryAdapter;
import com.example.globego.Adapter.PopularAdapter;
import com.example.globego.Adapter.RecommendedAdapter;
import com.example.globego.Adapter.SliderAdapter;
import com.example.globego.Domain.CartItem;
import com.example.globego.Domain.Category;
import com.example.globego.Domain.ItemDomain;
import com.example.globego.Domain.Location;
import com.example.globego.Domain.SliderItems;
import com.example.globego.Manager.CartManager;
import com.example.globego.R;
import com.example.globego.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;

    FirebaseAuth mAuth;
    FirebaseUser user;

    //
    CartAdapter adapter;


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategory);
        List<CartItem> cartItems = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        if(user==null){
            Intent intent=new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            binding.name.setText(user.getEmail());
        }

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.cartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartManager.loadCart(MainActivity.this);
                if (CartManager.getCartList().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        initLocation();
        initBanner();
        initCategory();
        initRecommended();
        initPopular();


       binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                switch (id){
                    case R.id.profile:
                        Intent intent=new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.home:
                        Intent intent1=new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent1);
                        break;

                }
            }
        });

       binding.searchBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String query = binding.searchBar.getText().toString().trim();

               if (!query.isEmpty()) {
                   performSearch(query);
               } else {
                   Toast.makeText(MainActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    private void performSearch(String query) {
        if (adapter != null) {
            adapter.filter(query);
        } else {
            Toast.makeText(this, "Adapter is not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPopular () {
        DatabaseReference myRef = database.getReference ("Popular");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<ItemDomain> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue:snapshot.getChildren()) {
                        list.add(issue.getValue(ItemDomain.class));
                    }
                    if (!list.isEmpty()) {
                        binding.recyclerViewPopular.setLayoutManager(new LinearLayoutManager
                                (MainActivity.this,LinearLayoutManager.HORIZONTAL ,false));
                        RecyclerView.Adapter adapter = new PopularAdapter(list);
                        binding.recyclerViewPopular.setAdapter(adapter);
                    }
                    binding.progressBarPopular.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecommended () {
        DatabaseReference myRef = database.getReference ("Item");
        binding.progressBarRecommended.setVisibility(View.VISIBLE);
        ArrayList<ItemDomain> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue:snapshot.getChildren()) {
                        list.add(issue.getValue(ItemDomain.class));
                    }
                    if (!list.isEmpty()) {
                        binding.recyclerViewRecommended.setLayoutManager(new LinearLayoutManager
                                (MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                        RecyclerView.Adapter adapter = new RecommendedAdapter(list);
                        binding.recyclerViewRecommended.setAdapter(adapter);
                    }
                    binding.progressBarRecommended.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {

            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference ("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue: snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (!list.isEmpty()) {
                        binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                        RecyclerView.Adapter adapter = new CategoryAdapter(list);
                        binding.recyclerViewCategory.setAdapter(adapter);
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {
            }
        });
    }
    private void initLocation() {
        DatabaseReference myRef=database.getReference("Location");
        ArrayList<Location>list=new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot issue:snapshot.getChildren()){
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter=new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.locationSp.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banners(ArrayList<SliderItems> items){

        binding.viewPagerSlider.setAdapter(new SliderAdapter(items,binding.viewPagerSlider));
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode (RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer (new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer (compositePageTransformer);

    }

    private void initBanner(){
        DatabaseReference myRef=database.getReference("Banner");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems>items=new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot issue:snapshot.getChildren()){
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}