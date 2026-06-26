package com.example.healthcareproject;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin;
    private TextView tvGoRegister;
    private boolean passwordVisible = false; // eye‑icon toggle
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmailLogin    = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin        = findViewById(R.id.btnLogin);
        tvGoRegister    = findViewById(R.id.tvGoRegister);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        findViewById(R.id.tvForgetPassword).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
        etPasswordLogin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etPasswordLogin.getCompoundDrawables()[2] != null &&
                        event.getRawX() >= (etPasswordLogin.getRight() -
                                etPasswordLogin.getCompoundDrawables()[2].getBounds().width())) {
                    if (passwordVisible) {
                        etPasswordLogin.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        etPasswordLogin.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    passwordVisible = !passwordVisible;
                    etPasswordLogin.setSelection(etPasswordLogin.getText().length());
                    return true;
                }
            }
            return false;
        });
        btnLogin.setOnClickListener(v -> attemptLogin());
    }
    private void attemptLogin() {
        final String email    = etEmailLogin.getText().toString().trim();
        final String password = etPasswordLogin.getText().toString().trim();
        if (email.isEmpty()) {
            etEmailLogin.setError("Enter email");
            return;
        }
        if (password.isEmpty()) {
            etPasswordLogin.setError("Enter password");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d(TAG, "User logged in: " + firebaseUser.getUid());
                            checkUserTypeAndNavigate(firebaseUser.getUid());
                        } else {
                            showToast("Login succeeded but FirebaseUser is null.");
                            Log.e(TAG, "Login succeeded but FirebaseUser is null.");
                        }
                    } else {
                        String err = task.getException() != null ?
                                task.getException().getMessage() :
                                "Authentication failed.";
                        showToast("Login failed: " + err);
                    }
                });
    }
    private void checkUserTypeAndNavigate(@NonNull String uid) {
        // First check caregiver_users
        mDatabase.child("caregiver_users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Caregiver data found for UID: " + uid);
                    startActivity(new Intent(LoginActivity.this, CaregiverHomeActivity.class));
                    finish();
                } else {
                    // Not a caregiver, check client_users
                    mDatabase.child("client_users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d(TAG, "Client data found for UID: " + uid);
                                startActivity(new Intent(LoginActivity.this, ClientHomeActivity.class));
                                finish();
                            } else {
                                Log.e(TAG, "User data node does not exist for UID: " + uid);
                                showToast("User data not found.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to read client data", error.toException());
                            showToast("Failed to retrieve user info: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read caregiver data", error.toException());
                showToast("Failed to retrieve user info: " + error.getMessage());
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
