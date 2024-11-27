package com.example.globego.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.globego.R;
import com.example.globego.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;

public class SignInActivity extends BaseActivity {

    ActivitySignInBinding binding;
    FirebaseAuth mAuth;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private static final int RC_SIGN_IN = 123;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(SignInActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // From google-services.json
                .requestEmail()
                .build();

        // Initialize Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        binding.SignWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        binding.signInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                String email = binding.editTextEmail.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this,"Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this,"Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                binding.progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignInActivity.this, "User Login Successfully.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    Toast.makeText(SignInActivity.this, "Check mail of password again!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.forgetPwdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,ForgetActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.bkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                // Get the sign-in result
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("SignIn", "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                      //  updateUI(user);
                    } else {
                        // Sign-in failed
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                      //  updateUI(null);
                    }
                });
    }
}