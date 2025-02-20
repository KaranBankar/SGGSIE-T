package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private Context context;
    private List<Complaint> complaintList;

    public ComplaintAdapter(Context context, List<Complaint> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.tvTitle.setText(complaint.getTitle());
        holder.tvDescription.setText(complaint.getDescription());
        holder.tvComplaintType.setText(complaint.getComplaintType());

        // Load image using Glide
        if (complaint.getImageUrl() != null && !complaint.getImageUrl().isEmpty()) {
            Glide.with(context).load(complaint.getImageUrl()).into(holder.ivComplaintImage);
        } else {
            holder.ivComplaintImage.setImageResource(R.drawable.user); // Default image
        }
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvTitle, tvDescription, tvComplaintType;
        ShapeableImageView ivComplaintImage;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvComplaintType = itemView.findViewById(R.id.tvComplaintType);
            ivComplaintImage = itemView.findViewById(R.id.ivComplaintImage);
        }
    }
}
