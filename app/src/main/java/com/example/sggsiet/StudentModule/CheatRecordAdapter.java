package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CheatRecordAdapter extends RecyclerView.Adapter<CheatRecordAdapter.ViewHolder> {
    private Context context;
    private List<CheatRecord> cheatRecords;

    public CheatRecordAdapter(Context context, List<CheatRecord> cheatRecords) {
        this.context = context;
        this.cheatRecords = cheatRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cheat_records_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheatRecord record = cheatRecords.get(position);
        holder.tvStudentName.setText(record.getStudentName());
        holder.tvEnrollmentNo.setText("Enrollment: " + record.getEnrollmentNo());
        holder.tvSubjectName.setText("Subject: " + record.getSubjectName());
        holder.tvDate.setText("Date: " + record.getDate());

        // ✅ Fix: Check if imageUrl is null before loading
        if (record.getImageUrl() != null && !record.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(record.getImageUrl())
                    .placeholder(R.drawable.secure) // ✅ Add placeholder image
                    .into(holder.ivCheatImage);
        } else {
            holder.ivCheatImage.setImageResource(R.drawable.secure); // Default image if null
        }
    }

    @Override
    public int getItemCount() {
        return cheatRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvEnrollmentNo, tvSubjectName, tvDate;
        ShapeableImageView ivCheatImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvEnrollmentNo = itemView.findViewById(R.id.tvEnrollmentNo);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivCheatImage = itemView.findViewById(R.id.ivCheatImage);
        }
    }
}
