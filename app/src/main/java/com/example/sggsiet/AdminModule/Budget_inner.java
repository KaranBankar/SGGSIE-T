package com.example.sggsiet.AdminModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sggsiet.R;
import com.example.sggsiet.StudentModule.BudgetActivity;
import com.example.sggsiet.StudentModule.BudgetRequest;
import com.example.sggsiet.StudentModule.BudgetRequestTracking;
import com.google.android.material.card.MaterialCardView;

public class Budget_inner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget_inner);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find CardViews
        MaterialCardView cardApproveBudget = findViewById(R.id.cardApproveBudget);
        MaterialCardView cardBudgetOverview = findViewById(R.id.cardBudgetOverview);

        // Set onClickListeners
        cardApproveBudget.setOnClickListener(view -> {
            Intent intent = new Intent(Budget_inner.this, BudgetRequestTracking.class);
            startActivity(intent);
        });

        cardBudgetOverview.setOnClickListener(view -> {
            Intent intent = new Intent(Budget_inner.this, BudgetOverview.class);
            startActivity(intent);
        });
    }
}
