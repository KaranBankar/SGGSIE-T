package com.example.sggsiet.AdminModule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.AdminModule.Adapter.ExpenseAdapter;
import com.example.sggsiet.R;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetOverview extends AppCompatActivity {

    // Views declaration
    private FloatingActionButton addExpenseFab;
    private RecyclerView expenseRecyclerView;
    private PieChart budgetUsageChart;
    private TextView tvTotalBudget, tvRemainingBudget, tvTotalExpenses;
    private ProgressBar progressEducation, progressInfrastructure, progressResearch;

    private ExpenseAdapter adapter;
    private List<Expense> expenseList = new ArrayList<>();
    private double totalBudget = 50000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_overview);

        initializeViews();
        setupRecyclerView();
        setupFabClickListener();
        updateBudgetDisplay();
    }

    private void initializeViews() {
        addExpenseFab = findViewById(R.id.addExpenseFab);
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        budgetUsageChart = findViewById(R.id.budgetUsageChart);

        // TextViews
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);

        // ProgressBars
        progressEducation = findViewById(R.id.progressEducation);
        progressInfrastructure = findViewById(R.id.progressInfrastructure);
        progressResearch = findViewById(R.id.progressResearch);
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(expenseList);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseRecyclerView.setAdapter(adapter);
    }

    private void setupFabClickListener() {
        addExpenseFab.setOnClickListener(v -> showAddExpenseDialog());
    }

    private void showAddExpenseDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null);

        TextInputEditText etCategory = dialogView.findViewById(R.id.etCategory);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        TextInputEditText etDate = dialogView.findViewById(R.id.etDate);

        setupDatePicker(etDate);

        dialogBuilder.setView(dialogView)
                .setTitle("Add New Expense")
                .setPositiveButton("Save", (dialog, which) -> {
                    String category = etCategory.getText().toString().trim();
                    String amountStr = etAmount.getText().toString().trim();
                    String date = etDate.getText().toString().trim();

                    if (validateInput(category, amountStr, date)) {
                        try {
                            double amount = Double.parseDouble(amountStr);
                            addNewExpense(new Expense(category, amount, date));
                        } catch (NumberFormatException e) {
                            showToast("Invalid amount format");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDatePicker(TextInputEditText etDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etDate.setText(sdf.format(new Date(selection)));
        });
        etDate.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
    }

    private boolean validateInput(String category, String amount, String date) {
        if (category.isEmpty()) return showToast("Please enter category");
        if (amount.isEmpty()) return showToast("Please enter amount");
        if (date.isEmpty()) return showToast("Please select date");
        return true;
    }

    private boolean showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void addNewExpense(Expense expense) {
        expenseList.add(expense);
        adapter.notifyDataSetChanged();
        updateBudgetDisplay();
    }

    private void updateBudgetDisplay() {
        double totalSpent = calculateTotalSpent();
        double remaining = totalBudget - totalSpent;

        tvTotalBudget.setText(String.format("₹%.2f", totalBudget));
        tvTotalExpenses.setText(String.format("₹%.2f", totalSpent));
        tvRemainingBudget.setText(String.format("₹%.2f", remaining));

        // Update progress bars (example static values)
        progressEducation.setProgress(70);
        progressInfrastructure.setProgress(50);
        progressResearch.setProgress(60);
    }

    private double calculateTotalSpent() {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }
        return total;
    }
}