package com.example.sggsiet.FacultyModule;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_approve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventDescription.setText(event.getDescription());
        holder.tvEventDateTime.setText("Date: " + event.getDate() + " | Time: " + event.getTime());
        holder.tvEventLocation.setText("Location: " + event.getLocation());

        Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.user)
                .into(holder.ivEventImage);

        // If event is already approved, disable the approve button
        if (event.getStatus().equals("Approved")) {
            holder.btnApprove.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.btnApprove.setEnabled(false);
        } else {
            // If not approved, allow approval action
            holder.btnApprove.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Approve Event")
                        .setMessage("Are you sure you want to approve this event?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference eventRef = FirebaseDatabase.getInstance()
                                    .getReference("events").child(event.getEventId());
                            eventRef.child("status").setValue("Approved");
                            holder.btnApprove.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                            holder.btnApprove.setEnabled(false);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            // Allow rejecting only for pending events
            holder.btnReject.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Reject Event")
                        .setMessage("Are you sure you want to reject this event?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference eventRef = FirebaseDatabase.getInstance()
                                    .getReference("events").child(event.getEventId());
                            eventRef.removeValue();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDescription, tvEventDateTime, tvEventLocation;
        ShapeableImageView ivEventImage;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventDateTime = itemView.findViewById(R.id.tvEventDateTime);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
