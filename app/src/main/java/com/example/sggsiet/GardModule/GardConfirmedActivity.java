package com.example.sggsiet.GardModule;

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
import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class GardConfirmedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacilityBookingAdapter adapter;
    private List<FacilityBooking> confirmedList;
    private DatabaseReference databaseReference;
    private String guardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gard_confirmed);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewConfirmed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmedList = new ArrayList<>();
        adapter = new FacilityBookingAdapter(this, confirmedList);
        recyclerView.setAdapter(adapter);

        // Get guard type from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GardLoginPrefs", MODE_PRIVATE);
        guardType = sharedPreferences.getString("gardType", "unknown");

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("facilitybookings");

        fetchConfirmedBookings();
    }

    private void fetchConfirmedBookings() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                confirmedList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FacilityBooking booking = snapshot.getValue(FacilityBooking.class);
                    if (booking != null) {
                        booking.setId(snapshot.getKey());

                        // Match facility name with guard type and filter only approved bookings
                        if (booking.getStatus().equals("APPROVED") &&
                                ((guardType.equals("gymgard") && booking.getFacilityName().equalsIgnoreCase("Gym")) ||
                                        (guardType.equals("auditoriamgard") && booking.getFacilityName().equalsIgnoreCase("Auditorium")) ||
                                        (guardType.equals("tenniscourtgard") && booking.getFacilityName().equalsIgnoreCase("Tennis Court")))) {

                            confirmedList.add(booking);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GardConfirmedActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }
}
