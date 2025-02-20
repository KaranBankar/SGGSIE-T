package com.example.sggsiet.AdminModule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.ViewHolder> {

    private List<String> facilityList;

    public FacilityAdapter(List<String> facilityList) {
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.facilityName.setText(facilityList.get(position));
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.textViewFacility);
        }
    }
}
