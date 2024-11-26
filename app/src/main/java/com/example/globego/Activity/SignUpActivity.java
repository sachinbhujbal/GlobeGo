package com.example.globego.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.globego.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;

import java.util.Objects;


public class SignUpActivity extends BaseActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            binding.progressBar.setVisibility(View.VISIBLE);
            String name = binding.editTextName.getText().toString().trim();
            String email = binding.editTextEmail.getText().toString().trim();
            String mobile = binding.editTextMb.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this,"Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this,"Enter email", Toast.LENGTH_SHORT).show();
                        return;
                }
                if (TextUtils.isEmpty(mobile)){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this,"Enter mobile", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this,"Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            binding.progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

                                Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(SignUpActivity.this, "Authentication Done.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        binding.bkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        String name = binding.editTextName.getText().toString().trim();
        String email = Objects.requireNonNull(binding.editTextEmail.getText()).toString().trim();
        String mobile = binding.editTextMb.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("mbNo", mobile);
        editor.apply();

    }
}