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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class VotecardAdapter extends RecyclerView.Adapter<VotecardAdapter.ViewHolder> {

    private Context context;
    private List<String> facilityList;
    private OnApplyClickListener onApplyClickListener;
    private boolean isApplied; // To track if the user has already applied

    public interface OnApplyClickListener {
        void onApply(String positionName);
    }

    public VotecardAdapter(Context context, List<String> facilityList, OnApplyClickListener listener) {
        this.context = context;
        this.facilityList = facilityList;
        this.onApplyClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voter_positions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String positionName = facilityList.get(position);
        holder.facilityName.setText(positionName);

        if (isApplied) {
            holder.applyButton.setEnabled(false);
            holder.applyButton.setText("Already Applied");
        } else {
            holder.applyButton.setOnClickListener(v -> showApplyDialog(positionName));
        }
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName;
        MaterialButton applyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.textViewFacility);
            applyButton = itemView.findViewById(R.id.buttonApply);
        }
    }

    /**
     * Displays the apply dialog where the user confirms their application.
     */
    private void showApplyDialog(String positionName) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_apply_position, null);

        TextInputEditText editTextName = dialogView.findViewById(R.id.editTextName);
        TextInputEditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        TextInputEditText editTextRollNo = dialogView.findViewById(R.id.editTextRollNo);

        // Fetch user details from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEnrollmentNo = sharedPreferences.getString("enrollmentNo", "");
        String userName = sharedPreferences.getString("studentName", "");
        String userEmail = sharedPreferences.getString("studentEmail", "");

        // Pre-fill the fields
        editTextName.setText(userName);
        editTextEmail.setText(userEmail);
        editTextRollNo.setText(userEnrollmentNo);

        new MaterialAlertDialogBuilder(context)
                .setTitle("Apply for " + positionName)
                .setView(dialogView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    if (userName.isEmpty() || userEmail.isEmpty() || userEnrollmentNo.isEmpty()) {
                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save to Firebase under the selected position
                    DatabaseReference appliedRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedCandidates/" + userEnrollmentNo);
                    appliedRef.setValue(positionName);

                    DatabaseReference positionRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedPositions/" + positionName + "/" + userEnrollmentNo);
                    positionRef.child("Name").setValue(userName);
                    positionRef.child("Email").setValue(userEmail);
                    positionRef.child("EnrollmentNo").setValue(userEnrollmentNo);

                    isApplied = true; // Prevents multiple applications
                    notifyDataSetChanged(); // Refresh RecyclerView
                    onApplyClickListener.onApply(positionName);

                    Toast.makeText(context, "Applied successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
