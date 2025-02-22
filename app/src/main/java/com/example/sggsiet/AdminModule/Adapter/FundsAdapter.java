package com.example.sggsiet.AdminModule.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.AdminModule.FundsSummary;
import com.example.sggsiet.R;

import java.util.List;

public class FundsAdapter extends RecyclerView.Adapter<FundsAdapter.ViewHolder> {
    private List<FundsSummary> fundsList;

    public FundsAdapter(List<FundsSummary> fundsList) {
        this.fundsList = fundsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_funds_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FundsSummary fundsSummary = fundsList.get(position);
        holder.tvRequestType.setText(fundsSummary.getRequestType());
        holder.tvTotalFunds.setText("₹" + fundsSummary.getTotalFunds());
    }

    @Override
    public int getItemCount() {
        return fundsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestType, tvTotalFunds;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Use the correct IDs from your custom layout
            tvRequestType = itemView.findViewById(R.id.text1);
            tvTotalFunds = itemView.findViewById(R.id.text2);
        }
    }
}