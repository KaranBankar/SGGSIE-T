package com.example.sggsiet.AdminModule;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<AdmissionModule.Student> studentList;

    public StudentAdapter(List<AdmissionModule.Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showstudent_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        AdmissionModule.Student student = studentList.get(position);
        holder.studentName.setText(student.getName());
        holder.studentEmail.setText(student.getEmail());
        holder.studentDepartment.setText(student.getDepartment());
        holder.studentYear.setText(student.getYear());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentEmail, studentDepartment, studentYear;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentEmail = itemView.findViewById(R.id.studentEmail);
            studentDepartment = itemView.findViewById(R.id.studentDepartment);
            studentYear = itemView.findViewById(R.id.studentYear);
        }
    }
}