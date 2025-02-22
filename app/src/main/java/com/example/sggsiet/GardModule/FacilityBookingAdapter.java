package com.example.sggsiet.GardModule;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class FacilityBookingAdapter extends RecyclerView.Adapter<FacilityBookingAdapter.ViewHolder> {

    private Context context;
    private List<FacilityBooking> bookingList;
    private DatabaseReference databaseReference;

    public FacilityBookingAdapter(Context context, List<FacilityBooking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        databaseReference = FirebaseDatabase.getInstance().getReference("facilitybookings");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facility_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacilityBooking booking = bookingList.get(position);

        holder.tvFacilityName.setText(booking.getFacilityName());
        holder.tvStudentEmail.setText("Student: " + booking.getStudentEmail());
        holder.tvBookingDate.setText("Date: " + booking.getBookingDate());
        holder.tvTimeSlot.setText("Time Slot: " + booking.getTimeSlot());

        if (context instanceof GardConfirmedActivity) {
            // Hide buttons in confirmed bookings activity
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
        // Set button colors based on status
        if (booking.getStatus().equals("Pending")) {
            holder.btnApprove.setEnabled(true);
            holder.btnReject.setEnabled(true);
        } else {
            holder.btnApprove.setEnabled(false);
            holder.btnReject.setEnabled(false);
            holder.btnApprove.setText(booking.getStatus());
            holder.btnApprove.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));

        }

        // Approve Click
        holder.btnApprove.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Confirm Approval")
                .setMessage("Are you sure you want to approve this booking?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    databaseReference.child(booking.getId()).child("status").setValue("APPROVED");
                    booking.setStatus("APPROVED");
                    notifyDataSetChanged();
                    Toast.makeText(context, "Booking Approved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show());

        // Reject Click
        holder.btnReject.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Reject Booking");

            final EditText input = new EditText(context);
            input.setHint("Enter reason...");
            builder.setView(input);

            builder.setPositiveButton("Confirm", (dialog, which) -> {
                String reason = input.getText().toString().trim();
                if (!reason.isEmpty()) {
                    databaseReference.child(booking.getId()).child("status").setValue("REJECTED");
                    databaseReference.child(booking.getId()).child("reason").setValue(reason);
                    booking.setStatus("REJECTED");
                    booking.setReason(reason);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Booking Rejected", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName, tvStudentEmail, tvBookingDate, tvTimeSlot;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
