package com.example.healthcareproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class UpdateAvailabilityActivity extends AppCompatActivity {
    private static final String TAG = "UpdateAvailability";
    private ImageView btnBack;
    private RadioGroup radioGroupStatus;
    private RadioButton radioAvailable, radioNotAvailable;
    private Button btnSave;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_availability);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        radioAvailable = findViewById(R.id.radioAvailable);
        radioNotAvailable = findViewById(R.id.radioNotAvailable);
        btnSave = findViewById(R.id.btnSave);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            saveAvailability();
        });
    }

    private void saveAvailability() {
        String status;
        if (radioAvailable.isChecked()) {
            status = "Available";
        } else if (radioNotAvailable.isChecked()) {
            status = "Not Available";
        } else {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> availability = new HashMap<>();
        availability.put("availability", status);
        availability.put("updatedAt", System.currentTimeMillis());

        mDatabase.child("caregiver_users").child(userId).updateChildren(availability)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateAvailabilityActivity.this, "Status updated: " + status, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating availability", e);
                    Toast.makeText(UpdateAvailabilityActivity.this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
