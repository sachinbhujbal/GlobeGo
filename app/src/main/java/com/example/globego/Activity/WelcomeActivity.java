package com.example.globego.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.globego.R;
import com.example.globego.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends BaseActivity {
    ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignIn.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this,SignInActivity.class)));
        binding.createAccount.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this,SignUpActivity.class)));

    }
}