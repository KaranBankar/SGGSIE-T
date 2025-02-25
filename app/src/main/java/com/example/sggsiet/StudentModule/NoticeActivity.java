package com.example.sggsiet.StudentModule;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    private RecyclerView noticeRecyclerView;
    private NoticeAdapter noticeAdapter;
    private List<Notice1> noticeList;
    private DatabaseReference noticeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notice2);

        noticeRecyclerView = findViewById(R.id.noticeRecyclerView);
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList);
        noticeRecyclerView.setAdapter(noticeAdapter);

        // Get Firebase reference
        noticeRef = FirebaseDatabase.getInstance().getReference("adminnotices");

        // Fetch notices from Firebase
        loadNotices();
    }

    private void loadNotices() {
        noticeRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notice1 notice = data.getValue(Notice1.class);
                    if (notice != null) {
                        noticeList.add(0, notice); // Add at top for latest first
                    }
                }
                noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NoticeActivity.this, "Failed to load notices", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
