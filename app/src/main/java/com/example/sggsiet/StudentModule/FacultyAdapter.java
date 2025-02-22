package com.example.sggsiet.StudentModule;


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

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {

    private List<FacultyModel> facultyList;
    private Context context;

    public FacultyAdapter(List<FacultyModel> facultyList, Context context) {
        this.facultyList = facultyList;
        this.context = context;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faculty_item_layout, parent, false);
        return new FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        FacultyModel faculty = facultyList.get(position);

        holder.name.setText(faculty.getName());
        holder.email.setText("Email: " + faculty.getEmail());
        holder.mobile.setText("Mobile: " + faculty.getMobile());
        holder.department.setText("Department: " + faculty.getDepartment());
        holder.position.setText("Position: " + faculty.getPosition());

        holder.askDoubtBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("faculty_name", faculty.getName());
            intent.putExtra("faculty_email", faculty.getEmail());
            intent.putExtra("faculty_mobile", faculty.getMobile());
            intent.putExtra("faculty_department", faculty.getDepartment());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    public static class FacultyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, mobile, department, position;
        Button askDoubtBtn;

        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.facultyName);
            email = itemView.findViewById(R.id.facultyEmail);
            mobile = itemView.findViewById(R.id.facultyMobile);
            department = itemView.findViewById(R.id.facultyDepartment);
            position = itemView.findViewById(R.id.facultyPosition);
            askDoubtBtn = itemView.findViewById(R.id.askDoubtBtn);
        }
    }
}
