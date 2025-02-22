package com.example.sggsiet.FacultyModule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sggsiet.R;

import java.util.List;

public class FacultyChatAdapter extends RecyclerView.Adapter<FacultyChatAdapter.ViewHolder> {
    private Context context;
    private List<ChatMessage> chatList;

    public FacultyChatAdapter(Context context, List<ChatMessage> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);

        if (message.getMessageText() != null && !message.getMessageText().isEmpty()) {
            holder.messageText.setText(message.getMessageText());
            holder.messageText.setVisibility(View.VISIBLE);
        } else {
            holder.messageText.setVisibility(View.GONE);
        }

        if (message.getImageUrl() != null) {
            Glide.with(context).load(message.getImageUrl()).into(holder.messageImage);
            holder.messageImage.setVisibility(View.VISIBLE);
        } else {
            holder.messageImage.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView messageImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tvMessage);
            messageImage = itemView.findViewById(R.id.chatImage);
        }
    }
}
