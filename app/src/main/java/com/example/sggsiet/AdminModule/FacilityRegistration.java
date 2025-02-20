package com.example.sggsiet.AdminModule;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class FacilityRegistration extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacilityAdapter facilityAdapter;
    private List<String> facilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facility_registration);

        recyclerView = findViewById(R.id.recyclerViewFacilities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(facilityList);
        recyclerView.setAdapter(facilityAdapter);

        fetchFacilityData();
    }

    private void fetchFacilityData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SGGSIE&T/ElectionPositions");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                facilityList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String facilityName = snapshot.getValue(String.class);
                    facilityList.add(facilityName);
                }
                facilityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
    }
}
