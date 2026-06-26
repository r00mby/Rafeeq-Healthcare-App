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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationsActivity";
    private LinearLayout notificationContainer;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        notificationContainer = findViewById(R.id.notificationContainer);

        btnBack.setOnClickListener(v -> finish());

        loadNotifications();
    }

    private void loadNotifications() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("notifications").orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationContainer.removeAllViews();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        addNotificationCard(data);
                    }
                } else {
                    // No notifications found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading notifications", error.toException());
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNotificationCard(DataSnapshot snapshot) {
        LinearLayout cardLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        cardLayout.setLayoutParams(params);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(32, 32, 32, 32);
        cardLayout.setBackgroundResource(R.drawable.custom_card_bg);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(snapshot.child("title").getValue(String.class));
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(getResources().getColor(R.color.button_blue));
        cardLayout.addView(tvTitle);

        TextView tvMessage = new TextView(this);
        tvMessage.setText(snapshot.child("message").getValue(String.class));
        tvMessage.setTextSize(14);
        tvMessage.setPadding(0, 8, 0, 0);
        tvMessage.setTextColor(getResources().getColor(R.color.text_black));
        cardLayout.addView(tvMessage);

        TextView tvTimestamp = new TextView(this);
        Long timestamp = snapshot.child("timestamp").getValue(Long.class);
        if (timestamp != null) {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            tvTimestamp.setText(sdf.format(date));
        } else {
            tvTimestamp.setText("Just now");
        }
        tvTimestamp.setTextSize(12);
        tvTimestamp.setPadding(0, 8, 0, 0);
        tvTimestamp.setTextColor(getResources().getColor(R.color.dark_text));
        cardLayout.addView(tvTimestamp);

        notificationContainer.addView(cardLayout);
    }
}
