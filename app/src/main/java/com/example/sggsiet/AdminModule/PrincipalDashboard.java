package com.example.sggsiet.AdminModule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sggsiet.R;
import com.example.sggsiet.SubmitComplaint;
import com.google.android.material.card.MaterialCardView;

public class PrincipalDashboard extends AppCompatActivity {
    private CardView addalldata,election,leavemodule,facility,budget,complaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal_dashboard);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




       initilizeallviews();
       setlisteners();



    }

    private void setlisteners() {
        addalldata.setOnClickListener(view -> {
            startActivity(new Intent(this,UploadData.class));
        });
        election.setOnClickListener(view -> {
            startActivity(new Intent(this,ElectionModule.class));
        });
        facility.setOnClickListener(view -> {
            startActivity(new Intent(this, SubmitComplaint.class));
        });

        leavemodule.setOnClickListener(view -> {
            startActivity(new Intent(this,LeavesManagement.class));
        });
        budget.setOnClickListener(v->{
            startActivity(new Intent(this,BudgetOverview.class));
        });
        complaints.setOnClickListener(v->{
            startActivity(new Intent(this,HomeComplaints.class));
        });
    }

    private void initilizeallviews() {
        addalldata=findViewById(R.id.addalldata);
        election=findViewById(R.id.electionscreen);
        complaints=findViewById(R.id.complainmodule);
        budget=findViewById(R.id.budgetmodule);
        leavemodule=findViewById(R.id.leavemodule);
        facility=findViewById(R.id.facilitymodule);
    }
}