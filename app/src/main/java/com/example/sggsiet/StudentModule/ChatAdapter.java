package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Message> messageList;

    public ChatAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getMessageText());
        holder.senderName.setText(message.getSender());

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a | dd MMM", Locale.getDefault());
        holder.timestamp.setText(sdf.format(message.getTimestamp()));

        if (message.getImageUrl() != null) {
            holder.messageImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(message.getImageUrl()).into(holder.messageImage);

            // Open full image when clicked
            holder.messageImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullImageViewActivity.class);
                intent.putExtra("image_url", message.getImageUrl());
                context.startActivity(intent);
            });

        } else {
            holder.messageImage.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, timestamp;
        ImageView messageImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            messageImage = itemView.findViewById(R.id.messageImage);
        }
    }
}
