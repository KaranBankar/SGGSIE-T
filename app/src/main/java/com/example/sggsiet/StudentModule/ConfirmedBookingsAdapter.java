package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;

import java.util.List;

public class ConfirmedBookingsAdapter extends RecyclerView.Adapter<ConfirmedBookingsAdapter.ViewHolder> {
    private Context context;
    private List<FacilityBooking1> bookingList;
    private String loggedInUserEmail;

    public ConfirmedBookingsAdapter(Context context, List<FacilityBooking1> bookingList) {
        this.context = context;
        this.bookingList = bookingList;

        // Get the logged-in student's email from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        loggedInUserEmail = sharedPreferences.getString("studentEmail", "unknown");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_facility_booking1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacilityBooking1 booking = bookingList.get(position);

        // Show only bookings that are "APPROVED" and belong to the logged-in user
        if (booking.getStatus().equals("APPROVED") && booking.getStudentEmail().equals(loggedInUserEmail)) {
            holder.facilityName.setText(booking.getFacilityName());
            holder.bookingDate.setText("Date: " + booking.getBookingDate());
            holder.timeSlot.setText("Time Slot: " + booking.getTimeSlot());
            holder.status.setText("Status: " + booking.getStatus());
        } else {
            // Hide non-approved or non-matching records
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName, bookingDate, timeSlot, status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.tvFacilityName);
            bookingDate = itemView.findViewById(R.id.tvBookingDate);
            timeSlot = itemView.findViewById(R.id.tvTimeSlot);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}
