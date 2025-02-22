package com.example.sggsiet.StudentModule;


public class Message {
    private String messageText;
    private String imageUrl;
    private String sender;
    private long timestamp;

    public Message() {}  // Required for Firebase

    public Message(String messageText, String imageUrl, String sender, long timestamp) {
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getMessageText() { return messageText; }
    public String getImageUrl() { return imageUrl; }
    public String getSender() { return sender; }
    public long getTimestamp() { return timestamp; }
}
