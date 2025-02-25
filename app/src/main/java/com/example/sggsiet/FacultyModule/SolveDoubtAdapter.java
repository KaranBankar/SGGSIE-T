package com.example.sggsiet.FacultyModule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;

import java.util.List;

public class SolveDoubtAdapter extends RecyclerView.Adapter<SolveDoubtAdapter.ViewHolder> {

    private Context context;
    private List<StudentChat> studentList;

    public SolveDoubtAdapter(Context context, List<StudentChat> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solve_doubt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentChat student = studentList.get(position);
        holder.studentName.setText(student.getName());
        holder.studentEmail.setText(student.getEmail());

        holder.solveDoubtBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, FacultyChatView.class);
            intent.putExtra("studentName", student.getName());
            intent.putExtra("studentEmail", student.getEmail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentEmail;
        Button solveDoubtBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentEmail = itemView.findViewById(R.id.studentEmail);
            solveDoubtBtn = itemView.findViewById(R.id.solveDoubtBtn);
        }
    }
}
