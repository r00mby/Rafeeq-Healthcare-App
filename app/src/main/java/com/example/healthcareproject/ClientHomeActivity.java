package com.example.healthcareproject;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
public class ClientHomeActivity extends AppCompatActivity {
    private Button btnFindCaregiver, btnMyBookings, btnNotifications, btnProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        btnFindCaregiver = findViewById(R.id.btnFindCaregiver);
        btnMyBookings    = findViewById(R.id.btnMyBookings);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnProfile       = findViewById(R.id.btnProfile);
        btnFindCaregiver.setOnClickListener(v -> startActivity(
                new Intent(ClientHomeActivity.this, CaregiversActivity.class)));
        btnMyBookings.setOnClickListener(v -> startActivity(
                new Intent(ClientHomeActivity.this, MyBookingsActivity.class)));
        btnNotifications.setOnClickListener(v -> startActivity(
                new Intent(ClientHomeActivity.this, NotificationsActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(
                new Intent(ClientHomeActivity.this, UserProfile.class)));
    }
}