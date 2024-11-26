package com.example.globego.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.globego.R;
import com.example.globego.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DOB = "dob";
    private static final String KEY_PROFILE_IMAGE = "profileImage";

    ActivityProfileBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private Uri profileImageUri;    // For selecting the profile picture

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.GONE);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");

        progressDialog = new ProgressDialog(this);

        // Load user profile if available in SharedPreferences
        loadUserProfile();

        // Set profile image click listener to select image
        binding.imgProfile.setOnClickListener(v -> selectProfileImage());

        // Save button listener
        binding.button.setOnClickListener(v -> saveProfile());

        binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i){
                    case R.id.home:
                        Intent intent=new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void selectProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getData();
            binding.imgProfile.setImageURI(profileImageUri); // Show selected image
        }
    }

    private void saveProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String userName = binding.name.getText().toString().trim();
        String userAddress = binding.address.getText().toString().trim();
        String userEmail = binding.email.getText().toString().trim();
        String userDOB = binding.DOB.getText().toString().trim();

        if (userName.isEmpty() || userAddress.isEmpty() || userEmail.isEmpty() || userDOB.isEmpty() || profileImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select a profile image", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        progressDialog.setMessage("Saving profile...");
        progressDialog.show();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        String userId = currentUser.getUid(); // Get Firebase UID

        // Upload profile image to Firebase Storage
        StorageReference fileRef = storageReference.child(userId + ".jpg");
        fileRef.putFile(profileImageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                    if (urlTask.isSuccessful()) {
                        String profileImageUrl = urlTask.getResult().toString();

                        // Save data to Firebase Database
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", userName);
                        userMap.put("address", userAddress);
                        userMap.put("email", userEmail);
                        userMap.put("dob", userDOB);
                        userMap.put("profileImage", profileImageUrl);

                        databaseReference.child(userId).setValue(userMap).addOnCompleteListener(dbTask -> {
                            progressDialog.dismiss();
                            if (dbTask.isSuccessful()) {

                                saveToSharedPreferences(userName, userAddress, userEmail, userDOB, profileImageUrl);
                                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to save profile!", Toast.LENGTH_SHORT).show();
                            }
                            binding.progressBar.setVisibility(View.GONE);
                        });
                    } else {
                        Toast.makeText(this, "Failed to get profile image URL!", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(this, "Failed to upload profile image!", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveToSharedPreferences(String name, String address, String email, String dob, String profileImageUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_PROFILE_IMAGE, profileImageUrl);
        editor.apply();
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_NAME, "");
        String address = sharedPreferences.getString(KEY_ADDRESS, "");
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String dob = sharedPreferences.getString(KEY_DOB, "");
        String profileImageUrl = sharedPreferences.getString(KEY_PROFILE_IMAGE, "");

        binding.name.setText(name);
        binding.address.setText(address);
        binding.email.setText(email);
        binding.DOB.setText(dob);

        if (!profileImageUrl.isEmpty()) {
            Glide.with(this).load(profileImageUrl).into(binding.imgProfile);
        }
    }
}
