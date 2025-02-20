package com.example.sggsiet.AdminModule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class LeaveManagementAdapter extends RecyclerView.Adapter<LeaveManagementAdapter.ViewHolder> {

    private List<LeaveApplication> leaveList;
    private OnLeaveActionListener listener; // Callback to update Firebase

    public LeaveManagementAdapter(List<LeaveApplication> leaveList, OnLeaveActionListener listener) {
        this.leaveList = leaveList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_leave_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveApplication leave = leaveList.get(position);

        holder.textViewName.setText("Name: " + leave.getName());
        holder.textViewEmail.setText("Email: " + leave.getEmail());
        holder.textViewReason.setText("Reason: " + leave.getReason());
        holder.textViewStatus.setText("Status: " + leave.getStatus());
        holder.textViewDate.setText("Date: " + leave.getApplicationDate());

        // Handle Approve button click
        holder.buttonApprove.setOnClickListener(v -> {
            listener.onLeaveAction(leave, "Approved");
        });

        // Handle Reject button click
        holder.buttonReject.setOnClickListener(v -> {
            listener.onLeaveAction(leave, "Rejected");
        });
    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewEmail, textViewReason, textViewStatus, textViewDate;
        MaterialButton buttonApprove, buttonReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewReason = itemView.findViewById(R.id.textViewReason);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }
    }

    // Interface for handling leave approval/rejection
    public interface OnLeaveActionListener {
        void onLeaveAction(LeaveApplication leave, String newStatus);
    }
}
