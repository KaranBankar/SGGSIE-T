package com.example.sggsiet.AdminModule.Adapter;

// BookingAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.AdminModule.Booking;
import com.example.sggsiet.R;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bookingDate.setText(booking.getBookingDate());
        holder.facilityName.setText(booking.getFacilityName());
        holder.status.setText(booking.getStatus());
        holder.studentEmail.setText(booking.getStudentEmail());
        holder.timeSlot.setText(booking.getTimeSlot());


        if (booking.getStatus().equalsIgnoreCase("Approved")) {
            holder.status.setBackgroundResource(R.drawable.status_approved);
        } else if (booking.getStatus().equalsIgnoreCase("Pending")) {
            holder.status.setBackgroundResource(R.drawable.status_pending);
        } else {
            holder.status.setBackgroundResource(R.drawable.status_rejected);
        }


}

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDate, facilityName, status, studentEmail, timeSlot;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            facilityName = itemView.findViewById(R.id.facilityName);
            status = itemView.findViewById(R.id.status);
            studentEmail = itemView.findViewById(R.id.studentEmail);
            timeSlot = itemView.findViewById(R.id.timeSlot);
        }
    }
}