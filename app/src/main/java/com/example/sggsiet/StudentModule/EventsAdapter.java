package com.example.sggsiet.StudentModule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;
    private SharedPreferences sharedPreferences;

    public EventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set event details
        holder.tvEventName.setText(event.name);
        holder.tvEventDescription.setText(event.description);
        holder.tvEventDate.setText(event.date);
        holder.tvEventTime.setText(event.time);
        holder.tvEventLocation.setText(event.location);

        // Load event image using Glide
        Glide.with(context).load(event.imageUrl).into(holder.ivEventImage);

        // Get available seats
        int bookings = Integer.parseInt(event.bookings != null ? event.bookings : "0");
        int totalSeats = Integer.parseInt(event.seats != null ? event.seats : "0");
        int seatsAvailable = totalSeats - bookings;

        holder.tvBookings.setText("Bookings: " + bookings);
        holder.tvSeatsAvailable.setText("Seats Available: " + seatsAvailable);

        // Handle "Register Now" button click
        holder.btnRegisterNow.setOnClickListener(view -> {
            if (seatsAvailable > 0) {
                showConfirmationDialog(event);
            } else {
                Toast.makeText(context, "No seats available for this event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDescription, tvEventDate, tvEventTime, tvEventLocation;
        TextView tvBookings, tvSeatsAvailable;
        ImageView ivEventImage;
        Button btnRegisterNow; // Button for event booking

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvBookings = itemView.findViewById(R.id.tvBookings);
            tvSeatsAvailable = itemView.findViewById(R.id.tvSeatsAvailable);
            btnRegisterNow = itemView.findViewById(R.id.btnRegister);
        }
    }

    private void showConfirmationDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Booking")
                .setMessage("Are you sure you want to book a seat for " + event.name + "?")
                .setPositiveButton("Confirm", (dialog, which) -> confirmBooking(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmBooking(Event event) {
        // Retrieve student details from SharedPreferences
        String studentName = sharedPreferences.getString("studentName", "Unknown");
        String studentEmail = sharedPreferences.getString("studentEmail", "Unknown");
        String studentMobile = sharedPreferences.getString("studentMobile", "Unknown");
        Log.d("DEBUG_SHARED_PREFS", "Student Name: " + studentName);
        Log.d("DEBUG_SHARED_PREFS", "Student Email: " + studentEmail);
        Log.d("DEBUG_SHARED_PREFS", "Student Mobile: " + studentMobile);

        if (studentName.equals("Unknown") || studentEmail.equals("Unknown") || studentMobile.equals("Unknown")) {
            Toast.makeText(context, "Student details not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a random seat number
        Random random = new Random();
        int seatNo = random.nextInt(100) + 1;

        // Prepare booking entry
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        String bookingId = bookingsRef.push().getKey();

        Booking booking = new Booking(studentName, studentEmail, studentMobile, event.time, event.date, event.name, seatNo);
        bookingsRef.child(bookingId).setValue(booking)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                    updateEventSeats(event);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Booking failed. Try again.", Toast.LENGTH_SHORT).show());
    }

    private void updateEventSeats(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.eventId);

        int updatedBookings = Integer.parseInt(event.bookings) + 1;
        int updatedSeatsAvailable = Integer.parseInt(event.seats) - updatedBookings;

        eventRef.child("bookings").setValue(String.valueOf(updatedBookings));
        eventRef.child("seatsAvailable").setValue(String.valueOf(updatedSeatsAvailable));

        Toast.makeText(context, "Event updated successfully", Toast.LENGTH_SHORT).show();
    }
}
