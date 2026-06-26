package com.example.healthcareproject;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText etName, etEmail, etPhone, etPassword;
    private RadioGroup radioGroup;
    private RadioButton rbClient, rbCaregiver;
    private Button btnSignUp;
    private ImageView btnBack;
    private boolean passwordVisible = false; // eye‑icon toggle
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etPhone     = findViewById(R.id.etPhone);
        etPassword  = findViewById(R.id.etPassword);
        radioGroup  = findViewById(R.id.radioGroup);
        rbClient    = findViewById(R.id.rbClient);
        rbCaregiver = findViewById(R.id.rbCaregiver);
        btnSignUp   = findViewById(R.id.btnSignUp);
        btnBack     = findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etPassword.getCompoundDrawables()[2] != null &&
                        event.getRawX() >= (etPassword.getRight() -
                                etPassword.getCompoundDrawables()[2].getBounds().width())) {
                    if (passwordVisible) {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    passwordVisible = !passwordVisible;
                    etPassword.setSelection(etPassword.getText().length());
                    return true;
                }
            }
            return false;
        });
        btnSignUp.setOnClickListener(v -> registerUser());
    }
    private void registerUser() {
        final String name     = etName.getText().toString().trim();
        final String email    = etEmail.getText().toString().trim();
        final String phone    = etPhone.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String accountType;
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selected = findViewById(selectedId);
            accountType = selected.getText().toString().trim();
        } else {
            accountType = "";
        }
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }
        if (accountType.isEmpty()) {
            showToast("Select account type");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d(TAG, "Auth successful, saving to Realtime Database for UID: " + firebaseUser.getUid());
                            saveUserDataToDatabase(firebaseUser.getUid(),
                                    name, email, phone, accountType, password);
                        } else {
                            showToast("Registration succeeded but user is null.");
                            Log.e(TAG, "FirebaseUser is null after successful registration.");
                        }
                    } else {
                        String err = task.getException() != null ?
                                task.getException().getMessage() :
                                "Registration failed.";
                        showToast("Registration failed: " + err);
                        Log.e(TAG, "Registration failed: " + err, task.getException());
                    }
                });
    }

    private void saveUserDataToDatabase(@NonNull String uid,
                                         @NonNull String name,
                                         @NonNull String email,
                                         @NonNull String phone,
                                         @NonNull String accountType,
                                         @NonNull String password) {

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("accountType", accountType);
        userMap.put("password", password);
        userMap.put("createdAt", System.currentTimeMillis());

        String nodeName = accountType.equalsIgnoreCase("caregiver") ? "caregiver_users" : "client_users";
        mDatabase.child(nodeName).child(uid).setValue(userMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Database save successful for UID: " + uid);
                    showToast("Registration successful!");
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to save user data: " + e.getMessage());
                    Log.e(TAG, "Failed to save user data for UID: " + uid + ". Error: " + e.getMessage(), e);
                });
    }
    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
