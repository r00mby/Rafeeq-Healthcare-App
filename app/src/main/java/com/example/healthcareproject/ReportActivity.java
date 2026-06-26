package com.example.healthcareproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";
    private ImageView btnBack;
    private Spinner spinnerBooking;
    private TextView tvClient, tvDate, tvTime;
    private EditText etReportDetails;
    private Button btnSubmit;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<DataSnapshot> bookingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        spinnerBooking = findViewById(R.id.spinnerBooking);
        tvClient = findViewById(R.id.tvClient);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etReportDetails = findViewById(R.id.etReportDetails);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnBack.setOnClickListener(v -> finish());

        spinnerBooking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= bookingsList.size()) {
                    DataSnapshot booking = bookingsList.get(position - 1);
                    String clientId = booking.child("clientId").getValue(String.class);
                    
                    // Fetch client name instead of showing ID
                    fetchClientName(clientId);
                    
                    tvDate.setText("Date: " + booking.child("date").getValue(String.class));
                    tvTime.setText("Time: " + booking.child("time").getValue(String.class));
                } else {
                    tvClient.setText("Client: ");
                    tvDate.setText("Date: ");
                    tvTime.setText("Time: ");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSubmit.setOnClickListener(v -> submitReport());

        loadBookings();
    }

    private void fetchClientName(String clientId) {
        if (clientId == null) return;
        mDatabase.child("client_users").child(clientId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                if (name != null) {
                    tvClient.setText("Client: " + name);
                } else {
                    tvClient.setText("Client: " + clientId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvClient.setText("Client: " + clientId);
            }
        });
    }

    private void loadBookings() {
        if (mAuth.getCurrentUser() == null) return;
        String caregiverId = mAuth.getCurrentUser().getUid();

        mDatabase.child("bookings").orderByChild("caregiverId").equalTo(caregiverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingsList.clear();
                List<String> spinnerItems = new ArrayList<>();
                spinnerItems.add("Select a booking");

                for (DataSnapshot data : snapshot.getChildren()) {
                    String status = data.child("status").getValue(String.class);
                    if ("Confirmed".equals(status)) {
                        bookingsList.add(data);
                        spinnerItems.add("Booking - " + data.child("date").getValue(String.class));
                    }
                }

                if (bookingsList.isEmpty()) {
                    Toast.makeText(ReportActivity.this, "No confirmed bookings found", Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ReportActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, spinnerItems);
                spinnerBooking.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading bookings", error.toException());
            }
        });
    }

    private void submitReport() {
        String reportText = etReportDetails.getText().toString().trim();
        int selectedPosition = spinnerBooking.getSelectedItemPosition();

        if (selectedPosition <= 0) {
            Toast.makeText(this, "Please select a booking", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reportText.isEmpty()) {
            Toast.makeText(this, "Please write the report", Toast.LENGTH_SHORT).show();
            return;
        }

        DataSnapshot booking = bookingsList.get(selectedPosition - 1);
        String caregiverId = mAuth.getCurrentUser().getUid();
        String clientId = booking.child("clientId").getValue(String.class);

        Map<String, Object> report = new HashMap<>();
        report.put("caregiverId", caregiverId);
        report.put("bookingId", booking.getKey());
        report.put("clientId", clientId);
        report.put("date", booking.child("date").getValue(String.class));
        report.put("time", booking.child("time").getValue(String.class));
        report.put("reportDetails", reportText);
        report.put("submittedAt", System.currentTimeMillis());

        mDatabase.child("reports").push().setValue(report)
                .addOnSuccessListener(aVoid -> {
                    // Send notification to client
                    sendNotificationToClient(clientId, reportText);
                    Toast.makeText(ReportActivity.this, "Report submitted and sent to client", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error submitting report", e);
                    Toast.makeText(ReportActivity.this, "Failed to submit report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotificationToClient(String clientId, String reportDetails) {
        if (clientId == null) return;
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", clientId);
        notification.put("title", "New Health Report");
        notification.put("message", "A new report has been submitted: " + reportDetails);
        notification.put("timestamp", System.currentTimeMillis());
        
        mDatabase.child("notifications").push().setValue(notification);
    }
}
