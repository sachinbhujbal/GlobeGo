package com.example.globego.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.globego.databinding.ActivityForgetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends BaseActivity {
    ActivityForgetBinding binding;
    private String email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();


        binding.btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.bkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgetActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void validateData() {

        email = binding.editTxtMail.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            binding.editTxtMail.setError("Enter Email");
        }
        else{
            ForgetPassword();
        }
    }

    private void ForgetPassword() {

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgetActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ForgetActivity.this,SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(ForgetActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }

                    }

                });
    }
}
