package com.example.sggsiet.StudentModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.example.sggsiet.UploadEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnMyBookings;
    private FloatingActionButton fabUploadEvent;
    private EventsAdapter eventsAdapter;
    private List<Event> eventList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        recyclerView = findViewById(R.id.recyclerView);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        fabUploadEvent = findViewById(R.id.fabUploadEvent);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(this, eventList);
        recyclerView.setAdapter(eventsAdapter);

        btnMyBookings.setOnClickListener(v -> {
            startActivity(new Intent(EventsActivity.this, MyBookingsActivity.class));
        });

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Fetch only Approved Events from Firebase
        fetchApprovedEventsFromFirebase();

        // Floating Action Button Click
        fabUploadEvent.setOnClickListener(v -> startActivity(new Intent(EventsActivity.this, UploadEvent.class)));
    }

    private void fetchApprovedEventsFromFirebase() {
        databaseReference.orderByChild("status").equalTo("Approved")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        eventList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Event event = dataSnapshot.getValue(Event.class);
                            if (event != null) {
                                eventList.add(event);
                            }
                        }
                        eventsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                });
    }
}
