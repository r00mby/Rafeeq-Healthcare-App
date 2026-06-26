package com.example.healthcareproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class MyAppointmentsActivity extends AppCompatActivity {
    private static final String TAG = "MyAppointmentsActivity";
    private LinearLayout appointmentContainer;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        appointmentContainer = findViewById(R.id.appointmentContainer);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadMyAppointments();
    }

    private void loadMyAppointments() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String caregiverId = mAuth.getCurrentUser().getUid();

        mDatabase.child("bookings").orderByChild("caregiverId").equalTo(caregiverId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentContainer.removeAllViews();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String clientId = data.child("clientId").getValue(String.class);
                        String date = data.child("date").getValue(String.class);
                        String time = data.child("time").getValue(String.class);
                        String service = data.child("service").getValue(String.class);
                        String status = data.child("status").getValue(String.class);
                        String bookingId = data.getKey();
                        addAppointmentCard(clientId, date, time, service, status, bookingId);
                    }
                } else {
                    Toast.makeText(MyAppointmentsActivity.this, "No appointments found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading appointments", error.toException());
                Toast.makeText(MyAppointmentsActivity.this, "Failed to load appointments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAppointmentCard(String clientId, String date, String time, String service, String status, String bookingId) {
        LinearLayout cardLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        cardLayout.setLayoutParams(params);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(32, 32, 32, 32);
        cardLayout.setBackgroundResource(R.drawable.custom_card_bg);

        TextView tvClientName = new TextView(this);
        tvClientName.setText("Client: Loading...");
        tvClientName.setTextSize(16);
        tvClientName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvClientName.setTextColor(getResources().getColor(R.color.text_black));
        cardLayout.addView(tvClientName);
        
        // Fetch client name
        if (clientId != null) {
            mDatabase.child("client_users").child(clientId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue(String.class);
                        if (name != null) {
                            tvClientName.setText("Client: " + name);
                        } else {
                            tvClientName.setText("Client: " + clientId);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvClientName.setText("Client: " + clientId);
                    }
                });
        }

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

        TextView tvService = new TextView(this);
        tvService.setText("Service: " + (service != null ? service : "N/A"));
        tvService.setTextSize(14);
        tvService.setPadding(0, 8, 0, 0);
        tvService.setTextColor(getResources().getColor(R.color.dark_text));
        cardLayout.addView(tvService);

        TextView tvStatus = new TextView(this);
        tvStatus.setText("Status: " + (status != null ? status : "Pending"));
        tvStatus.setTextSize(14);
        tvStatus.setPadding(0, 8, 0, 0);
        tvStatus.setTextColor(getResources().getColor(R.color.button_blue));
        cardLayout.addView(tvStatus);

        if ("Pending".equals(status)) {
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setPadding(0, 16, 0, 0);

            Button btnAccept = new Button(this);
            btnAccept.setText("Accept");
            btnAccept.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.button_blue)));
            btnAccept.setTextColor(getResources().getColor(R.color.white));
            btnAccept.setOnClickListener(v -> updateBookingStatus(bookingId, "Confirmed"));
            buttonLayout.addView(btnAccept);

            Button btnReject = new Button(this);
            btnReject.setText("Reject");
            btnReject.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.button_red)));
            btnReject.setTextColor(getResources().getColor(R.color.white));
            LinearLayout.LayoutParams rejectParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rejectParams.setMargins(16, 0, 0, 0);
            btnReject.setLayoutParams(rejectParams);
            btnReject.setOnClickListener(v -> updateBookingStatus(bookingId, "Cancelled"));
            buttonLayout.addView(btnReject);

            cardLayout.addView(buttonLayout);
        }

        appointmentContainer.addView(cardLayout);
    }

    private void updateBookingStatus(String bookingId, String newStatus) {
        mDatabase.child("bookings").child(bookingId).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> Toast.makeText(MyAppointmentsActivity.this, "Appointment " + newStatus, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MyAppointmentsActivity.this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
