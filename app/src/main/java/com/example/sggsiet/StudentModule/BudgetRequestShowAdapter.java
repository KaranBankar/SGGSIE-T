package com.example.sggsiet.StudentModule;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BudgetRequestShowAdapter extends RecyclerView.Adapter<BudgetRequestShowAdapter.ViewHolder> {
    private Context context;
    private List<BudgetRequest> budgetRequestList;
    private DatabaseReference databaseReference;

    public BudgetRequestShowAdapter(Context context, List<BudgetRequest> budgetRequestList) {
        this.context = context;
        this.budgetRequestList = budgetRequestList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("BudgetRequests");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BudgetRequest request = budgetRequestList.get(position);

        // Bind data to views
        holder.tvRequestTitle.setText(request.getTitle());
        holder.tvStudentName.setText("Requested by: " + request.getStudentName());
        holder.tvEnrollmentNo.setText("Enrollment No: " + request.getEnrollmentNo());
        holder.tvRequestDescription.setText(request.getDescription());
        holder.tvFundsAmount.setText("Funds Requested: ₹" + request.getFunds());
        holder.tvRequestStatus.setText("Status: " + request.getStatus());

        // Change status color based on approval status
        if ("Approved".equals(request.getStatus())) {
            holder.tvRequestStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else if ("Denied".equals(request.getStatus())) {
            holder.tvRequestStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.tvRequestStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
        }

        // Handle View Image button
        holder.btnViewImage.setOnClickListener(v -> {
            if (request.getAttachmentUrl() != null && !request.getAttachmentUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getAttachmentUrl()));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No image available", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Approve button click
        holder.btnApproveRequest.setOnClickListener(v -> updateRequestStatus(request.getRequestId(), "Approved"));

        // Handle Deny button click
        holder.btnDenyRequest.setOnClickListener(v -> updateRequestStatus(request.getRequestId(), "Denied"));
    }

    @Override
    public int getItemCount() {
        return budgetRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestTitle, tvStudentName, tvEnrollmentNo, tvRequestDescription, tvFundsAmount, tvRequestStatus;
        MaterialButton btnViewImage, btnApproveRequest, btnDenyRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestTitle = itemView.findViewById(R.id.tvRequestTitle);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvEnrollmentNo = itemView.findViewById(R.id.tvEnrollmentNo);
            tvRequestDescription = itemView.findViewById(R.id.tvRequestDescription);
            tvFundsAmount = itemView.findViewById(R.id.tvFundsAmount);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus);
            btnViewImage = itemView.findViewById(R.id.btnViewImage);
            btnApproveRequest = itemView.findViewById(R.id.btnApproveRequest);
            btnDenyRequest = itemView.findViewById(R.id.btnDenyRequest);
        }
    }

    private void updateRequestStatus(String requestId, String status) {
        databaseReference.child(requestId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Request " + status, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update request", Toast.LENGTH_SHORT).show());
    }
}
