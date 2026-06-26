package com.example.healthcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CaregiverProfileActivity extends AppCompatActivity {

    private static final String TAG = "CaregiverProfile";

    private ImageView btnBack;
    private TextView tvFullName;

    private EditText etName, etPhone, etAccountType;

    private Button btnEdit, btnLogout;

    private boolean isEditing = false;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        tvFullName = findViewById(R.id.tvFullName);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAccountType = findViewById(R.id.etAccountType);

        btnEdit = findViewById(R.id.btnEdit);
        btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> finish());

        loadProfile();

        btnEdit.setOnClickListener(v -> {
            if (!isEditing) {
                enableEditing(true);
                btnEdit.setText("Save");
                isEditing = true;
            } else {
                saveProfile();
            }
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(CaregiverProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {

        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("caregiver_users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) return;

                        String name = snapshot.child("name").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String accountType = snapshot.child("accountType").getValue(String.class);

                        tvFullName.setText(name);

                        etName.setText(name);
                        etPhone.setText(phone);
                        etAccountType.setText(accountType);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "loadProfile failed", error.toException());
                    }
                });
    }

    private void saveProfile() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String accountType = etAccountType.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and Phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("accountType", accountType);

        mDatabase.child("caregiver_users").child(userId)
                .updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();

                    tvFullName.setText(name);

                    enableEditing(false);
                    btnEdit.setText("Edit");
                    isEditing = false;
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "update failed", e);
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void enableEditing(boolean enable) {
        etName.setEnabled(enable);
        etPhone.setEnabled(enable);
        etAccountType.setEnabled(enable);
    }
}