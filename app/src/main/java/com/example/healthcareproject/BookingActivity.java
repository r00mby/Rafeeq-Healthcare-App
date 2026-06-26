package com.example.healthcareproject;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class BookingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking);
        ImageButton btnBack   = findViewById(R.id.btnBack);
        TextView tvDoctorName = findViewById(R.id.tvDoctorName);
        TextView tvDate       = findViewById(R.id.tvDate);
        TextView tvTime       = findViewById(R.id.tvTime);
        Booking currentBooking = new Booking(
                "Dr. Sara Ahmed",
                "20 Feb 2026",
                "5:00 PM",
                "Confirmed",
                "Paid",
                "150 SR"
        );
        tvDoctorName.setText(currentBooking.getDoctorName());
        tvDate.setText(currentBooking.getDate());
        tvTime.setText(currentBooking.getTime());
        btnBack.setOnClickListener(v -> finish());
    }
    public static class Booking {
        private final String doctorName, date, time,
                status, paymentStatus, amount;

        public Booking(String doctorName, String date, String time,
                       String status, String paymentStatus, String amount) {
            this.doctorName = doctorName;
            this.date = date;
            this.time = time;
            this.status = status;
            this.paymentStatus = paymentStatus;
            this.amount = amount;
        }
        public String getDoctorName() { return doctorName; }
        public String getDate()       { return date; }
        public String getTime()       { return time; }
    }
}