package com.example.healthcareproject;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
public class CaregiverHomeActivity extends AppCompatActivity {
    private Button btnAppointments, btnAvailability, btnReport, btnProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_home);
        btnAppointments = findViewById(R.id.btnAppointments);
        btnAvailability = findViewById(R.id.btnAvailability);
        btnReport = findViewById(R.id.btnReport);
        btnProfile = findViewById(R.id.btnProfile);
        btnAppointments.setOnClickListener(v -> startActivity(
                new Intent(CaregiverHomeActivity.this, MyAppointmentsActivity.class)));
        btnAvailability.setOnClickListener(v -> startActivity(
                new Intent(CaregiverHomeActivity.this, UpdateAvailabilityActivity.class)));
        btnReport.setOnClickListener(v -> startActivity(
                new Intent(CaregiverHomeActivity.this, ReportActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(
                new Intent(CaregiverHomeActivity.this, CaregiverProfileActivity.class)));
    }
}