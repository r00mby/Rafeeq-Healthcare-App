package com.example.healthcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CaregiversActivity extends AppCompatActivity {
    private static final String TAG = "CaregiversActivity";
    private LinearLayout caregiverContainer;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregivers);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        caregiverContainer = findViewById(R.id.caregiverContainer);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        loadCaregivers();
    }

    private void loadCaregivers() {
        mDatabase.child("caregiver_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                caregiverContainer.removeAllViews();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String name = data.child("name").getValue(String.class);
                        String availability = data.child("availability").getValue(String.class);
                        String id = data.getKey();
                        
                        // Only show caregivers who are "Available" or have no status set yet (default)
                        if (availability == null || "Available".equals(availability)) {
                            addCaregiverCard(name, id);
                        }
                    }
                } else {
                    Toast.makeText(CaregiversActivity.this, "No caregivers available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading caregivers", error.toException());
                Toast.makeText(CaregiversActivity.this, "Failed to load caregivers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCaregiverCard(String name, String caregiverId) {
        LinearLayout cardLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        cardLayout.setLayoutParams(params);
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setPadding(32, 32, 32, 32);
        cardLayout.setBackgroundResource(R.drawable.custom_card_bg);

        // Caregiver name (Left side)
        TextView tvName = new TextView(this);
        tvName.setText(name != null ? name : "Unknown");
        tvName.setTextSize(18);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(getResources().getColor(R.color.text_black));
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tvName.setLayoutParams(nameParams);
        cardLayout.addView(tvName);

        // Book button (Right side)
        Button btnBook = new Button(this);
        btnBook.setText("Book");
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnBook.setLayoutParams(btnParams);
        btnBook.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));
        btnBook.setTextColor(getResources().getColor(R.color.white));
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(CaregiversActivity.this, BookAppointmentActivity.class);
            intent.putExtra("caregiverId", caregiverId);
            intent.putExtra("caregiverName", name);
            startActivity(intent);
        });
        cardLayout.addView(btnBook);

        caregiverContainer.addView(cardLayout);
    }
}
