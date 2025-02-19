package com.example.sggsiet.DocterModule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import java.util.List;

public class StudentReportAdapter extends RecyclerView.Adapter<StudentReportAdapter.ViewHolder> {

    private List<StudentReport> reportList;

    public StudentReportAdapter(List<StudentReport> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentReport report = reportList.get(position);

        holder.tvStudentName.setText(report.getName());
        holder.tvStudentEmail.setText(report.getEmail());
        holder.tvContact.setText("Contact: " + report.getMobile());
        holder.tvDateTime.setText(report.getCurrentdate() + " | " + report.getCurrenttime());
        holder.tvHealthIssue.setText("Health Issue: " + report.getHealthissue());
        holder.tvTreatment.setText("Treatment: " + report.getTreatment());
        holder.tvDescription.setText("Description: " + report.getDescription());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentEmail, tvContact, tvDateTime, tvHealthIssue, tvTreatment, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvHealthIssue = itemView.findViewById(R.id.tvHealthIssue);
            tvTreatment = itemView.findViewById(R.id.tvTreatment);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
