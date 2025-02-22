package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminComplaintShowAdapter extends RecyclerView.Adapter<AdminComplaintShowAdapter.ComplaintViewHolder> {

    private Context context;
    private List<Complaint> complaintList;

    public AdminComplaintShowAdapter(Context context, List<Complaint> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.userName.setText(complaint.getTitle()); // Assuming you have a method getUser Name()
        holder.complaintText.setText(complaint.getDescription()); // Assuming you have a method getDescription()

        // Load image using Glide
        if (complaint.getImageUrl() != null && !complaint.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(complaint.getImageUrl()) // Assuming you have a method getImageUrl()
                    .into(holder.avatarImage);
        } else {
            holder.avatarImage.setImageResource(R.drawable.user); // Default image
        }

        // Set an OnClickListener for the "Take Action" button
        holder.takeActionButton.setOnClickListener(v -> {
            // Create a Material Alert Dialog
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_resolve_complaint, null);

            // Initialize dialog views
            TextInputEditText titleInput = dialogView.findViewById(R.id.titleInput);
            TextInputEditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);

            builder.setTitle("Resolve Complaint")
                    .setView(dialogView)
                    .setPositiveButton("Send", (dialog, which) -> {
                        String resolveTitle = titleInput.getText().toString().trim();
                        String resolveDescription = descriptionInput.getText().toString().trim();

                        if (!resolveTitle.isEmpty() && !resolveDescription.isEmpty()) {
                            // Store the resolved complaint in Firebase
                            storeResolvedComplaint(complaint, resolveTitle, resolveDescription);

                            // Delete the complaint using its position in the list
                            deleteComplaint(complaint);
                        } else {
                            Toast.makeText(context, "Please enter both title and description", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    private void deleteComplaint(Complaint complaint) {
        DatabaseReference complaintsRef = FirebaseDatabase.getInstance().getReference("complaints");

        complaintsRef.orderByChild("title").equalTo(complaint.getTitle()) // Match by complaint text
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot complaintSnapshot : snapshot.getChildren()) {
                            // Delete the complaint from Firebase
                            complaintSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Complaint resolved and removed", Toast.LENGTH_SHORT).show();
                                        // Remove from list and refresh RecyclerView
                                        complaintList.remove(complaint);
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to delete complaint", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error fetching complaints", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void storeResolvedComplaint(Complaint complaint, String resolveTitle, String resolveDescription) {
        DatabaseReference resolvedComplaintsRef = FirebaseDatabase.getInstance().getReference("resolved_complaints");

        // Create a new resolved complaint object
        Complaint resolvedComplaint = new Complaint(
                complaint.getTitle(),
                complaint.getDescription(),
                resolveTitle,
                resolveDescription
        );

        // Push the resolved complaint to Firebase
        resolvedComplaintsRef.push().setValue(resolvedComplaint)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Complaint resolved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to resolve complaint: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        MaterialTextView userName;
        MaterialTextView complaintText;
        MaterialButton takeActionButton;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            userName = itemView.findViewById(R.id.userName);
            complaintText = itemView.findViewById(R.id.complaintText);
            takeActionButton = itemView.findViewById(R.id.takeActionButton);
        }
    }
}