package com.example.myapplication;
import android.widget.ImageView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_booking);
    
    ImageView btnBack = findViewById(R.id.btnBack);
    btnBack.setOnClickListener(v -> {
        finish();
    });
    Button btnPay = findViewById(R.id.btnPay);
    Button btnCancel = findViewById(R.id.btnCancel);
    
    btnPay.setOnClickListener(v -> {
        Toast.makeText(this, "Proceeding to payment...", Toast.LENGTH_SHORT).show();
    });
    
    btnCancel.setOnClickListener(v -> {
        Toast.makeText(this, "Your reservation has been cancelled", Toast.LENGTH_SHORT).show();
    });
}
}