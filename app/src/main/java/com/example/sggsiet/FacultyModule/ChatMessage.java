package com.example.sggsiet.FacultyModule;

public class ChatMessage {
    private String sender;
    private String email; // Email of the sender (student)
    private String messageText;
    private String imageUrl;
    private long timestamp; // Firebase stores timestamps as long

    // Default constructor (required for Firebase)
    public ChatMessage() {
    }

    // Constructor for messages with text only
    public ChatMessage(String sender, String email, String messageText, long timestamp) {
        this.sender = sender;
        this.email = email;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.imageUrl = null; // No image in this case
    }

    // Constructor for messages with text and image
    public ChatMessage(String sender, String email, String messageText, String imageUrl, long timestamp) {
        this.sender = sender;
        this.email = email;
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
