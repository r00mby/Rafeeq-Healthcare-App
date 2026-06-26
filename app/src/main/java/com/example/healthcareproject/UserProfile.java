package com.example.healthcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    TextView profileName, profileEmail;
    EditText profilePhone, profileType;

    ImageView btnBack;
    Button btnLogout, btnEdit;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profilePhone = findViewById(R.id.profile_phone);
        profileType = findViewById(R.id.profile_type);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEdit);

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnEdit.setOnClickListener(v -> {

            if (!isEditing) {

                profilePhone.setEnabled(true);
                profileType.setEnabled(true);

                btnEdit.setText("Save");
                isEditing = true;

            } else {

                saveData();

                profilePhone.setEnabled(false);
                profileType.setEnabled(false);

                btnEdit.setText("Edit");
                isEditing = false;
            }
        });

        loadData();
    }

    private void loadData() {

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        mDatabase.child("client_users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            Toast.makeText(UserProfile.this, "No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String type = snapshot.child("accountType").getValue(String.class);

                        profileName.setText(name);
                        profileEmail.setText(email);
                        profilePhone.setText(phone);
                        profileType.setText(type);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserProfile.this, "Load failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveData() {

        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();
        map.put("phone", profilePhone.getText().toString());
        map.put("accountType", profileType.getText().toString());

        mDatabase.child("client_users").child(uid)
                .updateChildren(map)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                );
    }
}