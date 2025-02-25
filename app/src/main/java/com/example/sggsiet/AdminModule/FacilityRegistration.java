package com.example.sggsiet.AdminModule;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.AdminModule.Adapter.BookingAdapter;
import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FacilityRegistration extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facility_registration);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList);
        recyclerView.setAdapter(bookingAdapter);

        fetchBookings();
    }

    private void fetchBookings() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("facilitybookings");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Booking booking = snapshot.getValue(Booking.class);
                    if (booking != null) {
                        bookingList.add(booking);
                    }
                }
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to read data", databaseError.toException());
            }
        });
    }

    public void setStatusColor(TextView statusTextView, String status) {
        if (status.equalsIgnoreCase("Approved")) {
            statusTextView.setBackgroundResource(R.drawable.status_approved);
        } else if (status.equalsIgnoreCase("Pending")) {
            statusTextView.setBackgroundResource(R.drawable.status_pending);
        } else {
            statusTextView.setBackgroundResource(R.drawable.status_rejected);
        }
    }
}
