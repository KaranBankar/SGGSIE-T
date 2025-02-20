package com.example.sggsiet.StudentModule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        recyclerView.setAdapter(bookingAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");

        // 🔹 Retrieve logged-in user's mobile number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInMobile = sharedPreferences.getString("studentMobile", "9322067937");  // Default if not found

        Log.d("MyBookingsActivity", "Logged-in Mobile: " + loggedInMobile);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null) {
                        Log.d("FirebaseData", "Fetched Booking: " + booking.getStudentMobile());
                        if (booking.getStudentMobile().equals(loggedInMobile)) {
                            bookingList.add(booking);
                        }
                    }
                }
                Log.d("MyBookingsActivity", "Filtered Bookings Count: " + bookingList.size());
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
