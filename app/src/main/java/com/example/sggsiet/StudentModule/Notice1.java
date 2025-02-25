package com.example.sggsiet.StudentModule;

public class Notice1 {  // Changed class name to Notice1
    private String noticeId;
    private String noticeText;
    private long timestamp;

    // Empty constructor for Firebase
    public Notice1() {}

    public Notice1(String noticeId, String noticeText, long timestamp) {
        this.noticeId = noticeId;
        this.noticeText = noticeText;
        this.timestamp = timestamp;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public String getNoticeText() {
        return noticeText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
