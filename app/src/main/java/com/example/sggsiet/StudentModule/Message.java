package com.example.sggsiet.StudentModule;


public class Message {
    private String messageText;
    private String imageUrl;
    private String sender;
    private long timestamp;

    private String email;

    public Message() {}  // Required for Firebase

    public Message(String messageText, String imageUrl, String sender, long timestamp,String email) {
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.timestamp = timestamp;
        this.email=email;
    }

    public String getMessageText() { return messageText; }

    public  String getEmail(){return  email;}
    public String getImageUrl() { return imageUrl; }
    public String getSender() { return sender; }
    public long getTimestamp() { return timestamp; }
}
