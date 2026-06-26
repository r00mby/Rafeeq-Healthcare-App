package com.example.healthcareproject;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private EditText etEmailReset;
    private Button btnUpdatePassword;
    private ImageView btnBack;
    private ProgressBar progressBar;
    
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        etEmailReset = findViewById(R.id.etEmailReset);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        
        btnBack.setOnClickListener(v -> finish());

        btnUpdatePassword.setOnClickListener(v -> {
            String email = etEmailReset.getText().toString().trim();

            if (email.isEmpty()) {
                etEmailReset.setError("Email is required");
                etEmailReset.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmailReset.setError("Please enter a valid email address");
                etEmailReset.requestFocus();
                return;
            }

            sendPasswordResetEmail(email);
        });
    }

    private void sendPasswordResetEmail(String email) {
        btnUpdatePassword.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnUpdatePassword.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "Reset link sent! Please check your email inbox and spam folder.", 
                            Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? 
                            task.getException().getMessage() : "Unknown error occurred";
                        
                        // Common Firebase Auth errors
                        if (errorMessage.contains("user-not-found")) {
                            errorMessage = "No account found with this email address.";
                        } else if (errorMessage.contains("network-request-failed")) {
                            errorMessage = "Network error. Please check your internet connection.";
                        }
                        
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "Error: " + errorMessage, 
                            Toast.LENGTH_LONG).show();
                    }
                });
    }
}
