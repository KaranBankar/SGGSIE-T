package com.example.sggsiet.StudentModule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private List<Notice1> noticeList;

    public NoticeAdapter(List<Notice1> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice1 notice = noticeList.get(position);
        holder.noticeTextView.setText(notice.getNoticeText());

        // Convert timestamp to a readable date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.dateTextView.setText(sdf.format(notice.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView noticeTextView, dateTextView;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            noticeTextView = itemView.findViewById(R.id.noticeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
