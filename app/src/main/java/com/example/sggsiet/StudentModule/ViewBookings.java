package com.example.sggsiet.StudentModule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.GardModule.FacilityBooking;
import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewBookings extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConfirmedBookingsAdapter adapter;
    private List<FacilityBooking1> bookingList;
    private DatabaseReference databaseReference;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_bookings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new ConfirmedBookingsAdapter(this, bookingList);
        recyclerView.setAdapter(adapter);

        // Get logged-in user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("studentEmail", "unknown");

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("facilitybookings");

        fetchBookings();
    }

    private void fetchBookings() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FacilityBooking1 booking = snapshot.getValue(FacilityBooking1.class);
                    if (booking != null) {
                        booking.setId(snapshot.getKey());

                        // Check if booking belongs to current user
                        if (booking.getStudentEmail().equalsIgnoreCase(userEmail)) {
                            bookingList.add(booking);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewBookings.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }
}
