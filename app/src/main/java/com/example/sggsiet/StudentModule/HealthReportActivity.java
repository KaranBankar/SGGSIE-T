package com.example.sggsiet.StudentModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HealthReportActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private String studentName, studentEmail;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_report);

        // Retrieve Student Info
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        studentName = prefs.getString("studentName", "");
        studentEmail = prefs.getString("studentEmail", "");

        recyclerView = findViewById(R.id.recycler_health_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("SGGSIE&T").child("appointments");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Book Appointment Card Click
        MaterialCardView cardBookAppointment = findViewById(R.id.card_book_appointment);
        cardBookAppointment.setOnClickListener(v -> openAppointmentDialog());

        // Emergency Service Card Click
        MaterialCardView cardEmergency = findViewById(R.id.card_emergency_service);
        cardEmergency.setOnClickListener(v -> handleEmergencyService());
    }

    private void handleEmergencyService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        if (!isGPSEnabled()) {
            showEnableGPSDialog();
        } else {
            callDoctor();
            requestLiveLocation();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestLiveLocation() {
        String provider = getBestProvider();

        if (provider == null) {
            Toast.makeText(this, "No location provider available!", Toast.LENGTH_SHORT).show();
            return;
        }

        locationManager.requestLocationUpdates(provider, 2000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                sendLocationSMS(location.getLatitude(), location.getLongitude());
                locationManager.removeUpdates(this);
            }
        });
    }

    private String getBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        return locationManager.getBestProvider(criteria, true);
    }

    private void sendLocationSMS(double latitude, double longitude) {
        // Google Maps link for direct navigation
        String googleMapsLink = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude;
        String message = "Emergency! Need help at: " + googleMapsLink;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("9145136767", null, message, null, null);

        Toast.makeText(this, "Live Location Sent to Doctor!", Toast.LENGTH_SHORT).show();
    }


    private boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showEnableGPSDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Enable GPS")
                .setMessage("GPS is required for sending live location. Enable it?")
                .setPositiveButton("Yes", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, which) -> Toast.makeText(this, "GPS not enabled!", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void callDoctor() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:9145136767"));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleEmergencyService();
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openAppointmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_book_appointment, null);
        builder.setView(dialogView);

        final TextView selectedDate = dialogView.findViewById(R.id.selected_date);
        final TextView selectedTime = dialogView.findViewById(R.id.selected_time);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        selectedDate.setOnClickListener(v -> showDatePicker(selectedDate));
        selectedTime.setOnClickListener(v -> showTimePicker(selectedTime));

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            if (selectedDate.getText().toString().isEmpty() || selectedTime.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select date and time!", Toast.LENGTH_SHORT).show();
                return;
            }
            storeAppointment(selectedDate.getText().toString(), selectedTime.getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void storeAppointment(String date, String time) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SGGSIE&T").child("appointments");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String studentName = prefs.getString("studentName", "");
        String studentEmail = prefs.getString("studentEmail", "");

        HashMap<String, String> appointment = new HashMap<>();
        appointment.put("name", studentName);
        appointment.put("email", studentEmail);
        appointment.put("date", date);
        appointment.put("time", time);

        databaseReference.push().setValue(appointment)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Appointment Confirmed!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show());
    }

    private void showTimePicker(TextView timeText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, selectedMinute) -> {
            if (hourOfDay < 10 || hourOfDay > 20) {
                Toast.makeText(this, "Select time between 10 AM - 8 PM", Toast.LENGTH_SHORT).show();
                return;
            }
            timeText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, selectedMinute));
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void showDatePicker(TextView dateText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            dateText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }



}
