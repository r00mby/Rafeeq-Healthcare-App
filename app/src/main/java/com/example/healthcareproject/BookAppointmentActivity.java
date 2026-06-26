package com.example.healthcareproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {
    private static final String TAG = "BookAppointment";
    private ImageView btnBack;
    private Button btnConfirmBooking;
    private EditText etService;
    private EditText etDate;
    private EditText etTime;
    private TextView tvCaregiverName;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String caregiverId;
    private String caregiverName;
    
    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        etService = findViewById(R.id.etService);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        tvCaregiverName = findViewById(R.id.tvCaregiverName);

        // Date Selection Logic (Professional Popup)
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        etDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Time Selection Logic (Professional Popup)
        etTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(BookAppointmentActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                String amPm = (selectedHour < 12) ? "AM" : "PM";
                int displayHour = (selectedHour == 0 || selectedHour == 12) ? 12 : selectedHour % 12;
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, selectedMinute, amPm);
                etTime.setText(selectedTime);
            }, hour, minute, false); // 'false' for 12h format
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        // Get caregiver info from intent
        Intent intent = getIntent();
        caregiverId = intent.getStringExtra("caregiverId");
        caregiverName = intent.getStringExtra("caregiverName");

        if (caregiverName != null) {
            tvCaregiverName.setText("Caregiver: " + caregiverName);
        }

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnConfirmBooking.setOnClickListener(v -> {
            saveBooking();
        });
    }

    private void saveBooking() {
        String service = etService.getText().toString().trim();
        
        if (service.isEmpty()) {
            Toast.makeText(this, "Please describe your request", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime == null || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (caregiverId == null || caregiverId.isEmpty()) {
            Toast.makeText(this, "Caregiver not selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String bookingId = mDatabase.child("bookings").push().getKey();
        
        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingId", bookingId);
        booking.put("clientId", userId);
        booking.put("caregiverId", caregiverId);
        booking.put("caregiverName", caregiverName);
        booking.put("service", service);
        booking.put("date", selectedDate);
        booking.put("time", selectedTime);
        booking.put("status", "Pending");
        booking.put("paymentStatus", "Pending");
        booking.put("timestamp", System.currentTimeMillis());

        if (bookingId != null) {
            mDatabase.child("bookings").child(bookingId).setValue(booking)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BookAppointmentActivity.this, "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent = new Intent(BookAppointmentActivity.this, Class.forName("com.example.healthcareproject.MyBookingsActivity"));
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "MyBookingsActivity not found", e);
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding booking", e);
                        Toast.makeText(BookAppointmentActivity.this, "Failed to book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
