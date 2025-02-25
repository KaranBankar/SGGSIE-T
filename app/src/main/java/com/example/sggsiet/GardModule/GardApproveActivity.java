package com.example.sggsiet.GardModule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class GardApproveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacilityBookingAdapter adapter;
    private List<FacilityBooking> bookingList;
    private DatabaseReference databaseReference;
    private String guardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gard_approve);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new FacilityBookingAdapter(this, bookingList);
        recyclerView.setAdapter(adapter);

        // Get guard type from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GardLoginPrefs", MODE_PRIVATE);
        guardType = sharedPreferences.getString("gardType", "gymgard");

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("facilitybookings");

        fetchBookings();
    }

    private void fetchBookings() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FacilityBooking booking = snapshot.getValue(FacilityBooking.class);
                    if (booking != null) {
                        booking.setId(snapshot.getKey());

                        Log.d("FirebaseData", "Facility: " + booking.getFacilityName() + " | Status: " + booking.getStatus());

                        // Match facility name with guard type
                        if ((guardType.equals("gymgard") && booking.getFacilityName().equalsIgnoreCase("Gym")) ||
                                (guardType.equals("auditoriamgard") && booking.getFacilityName().equalsIgnoreCase("Auditorium")) ||
                                (guardType.equals("tenniscourtgard") && booking.getFacilityName().equalsIgnoreCase("Tennis Court"))) {

                            bookingList.add(booking);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d("RecyclerViewUpdate", "Bookings count: " + bookingList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
                Toast.makeText(GardApproveActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
