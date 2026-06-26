package com.example.healthcareproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MyBookingsActivity extends AppCompatActivity {
    private static final String TAG = "MyBookingsActivity";
    private LinearLayout bookingContainer;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        bookingContainer = findViewById(R.id.bookingContainer);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadMyBookings();
    }

    private void loadMyBookings() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("bookings").orderByChild("clientId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingContainer.removeAllViews();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String caregiverName = data.child("caregiverName").getValue(String.class);
                        String service = data.child("service").getValue(String.class);
                        String date = data.child("date").getValue(String.class);
                        String time = data.child("time").getValue(String.class);
                        String status = data.child("status").getValue(String.class);
                        addBookingCard(caregiverName, service, date, time, status);
                    }
                } else {
                    Toast.makeText(MyBookingsActivity.this, "No bookings found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading bookings", error.toException());
                Toast.makeText(MyBookingsActivity.this, "Failed to load bookings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBookingCard(String name, String service, String date, String time, String status) {
        LinearLayout cardLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        cardLayout.setLayoutParams(params);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(32, 32, 32, 32);
        cardLayout.setBackgroundResource(R.drawable.custom_card_bg);

        TextView tvCaregiverName = new TextView(this);
        tvCaregiverName.setText("Caregiver: " + (name != null ? name : "Unknown"));
        tvCaregiverName.setTextSize(16);
        tvCaregiverName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvCaregiverName.setTextColor(getResources().getColor(R.color.text_black));
        cardLayout.addView(tvCaregiverName);

        TextView tvService = new TextView(this);
        tvService.setText("Service: " + (service != null ? service : "N/A"));
        tvService.setTextSize(14);
        tvService.setPadding(0, 8, 0, 0);
        tvService.setTextColor(getResources().getColor(R.color.dark_text));
        cardLayout.addView(tvService);

        TextView tvDate = new TextView(this);
        tvDate.setText("Date: " + (date != null ? date : "N/A"));
        tvDate.setTextSize(14);
        tvDate.setPadding(0, 8, 0, 0);
        tvDate.setTextColor(getResources().getColor(R.color.dark_text));
        cardLayout.addView(tvDate);

        TextView tvTime = new TextView(this);
        tvTime.setText("Time: " + (time != null ? time : "N/A"));
        tvTime.setTextSize(14);
        tvTime.setPadding(0, 8, 0, 0);
        tvTime.setTextColor(getResources().getColor(R.color.dark_text));
        cardLayout.addView(tvTime);

        TextView tvStatus = new TextView(this);
        tvStatus.setText("Status: " + (status != null ? status : "Pending"));
        tvStatus.setTextSize(14);
        tvStatus.setPadding(0, 8, 0, 0);
        tvStatus.setTextColor(getResources().getColor(R.color.button_blue));
        cardLayout.addView(tvStatus);

        bookingContainer.addView(cardLayout);
    }
}
