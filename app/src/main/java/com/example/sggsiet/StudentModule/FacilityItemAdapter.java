package com.example.sggsiet.StudentModule;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FacilityItemAdapter extends RecyclerView.Adapter<FacilityItemAdapter.FacilityViewHolder> {
    private Context context;
    private List<FacilityItem> facilityList;

    public FacilityItemAdapter(Context context, List<FacilityItem> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        FacilityItem facility = facilityList.get(position);
        holder.name.setText(facility.getName());
        holder.timeSlot.setText("Available: " + facility.getTimeSlot());
        holder.image.setImageResource(facility.getImageResId());

        holder.bookButton.setOnClickListener(v -> showBookingDialog(facility));
    }

    private void showBookingDialog(FacilityItem facility) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Time Slot");

        // Splitting available time slots
        String[] timeSlots = facility.getTimeSlot().split(", ");

        builder.setItems(timeSlots, (dialog, which) -> {
            String selectedSlot = timeSlots[which];
            confirmBooking(facility.getName(), selectedSlot);
        });

        builder.show();
    }

    private void confirmBooking(String facilityName, String timeSlot) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("studentEmail", "unknown");
        String bookingDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("facilitybookings");

        String bookingId = ref.push().getKey();
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("facilityName", facilityName);
        bookingData.put("timeSlot", timeSlot);
        bookingData.put("status", "Pending");
        bookingData.put("studentEmail", userEmail);
        bookingData.put("bookingDate", bookingDate);

        ref.child(bookingId).setValue(bookingData)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Booking Requested", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Booking Failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView name, timeSlot;
        ImageView image;
        Button bookButton;

        FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.facility_name);
            timeSlot = itemView.findViewById(R.id.facility_time);
            image = itemView.findViewById(R.id.facility_image);
            bookButton = itemView.findViewById(R.id.book_button);
        }
    }
}
