package com.ayaenshasy.AlBayan.model;

import com.google.firebase.Timestamp;

public class Notification {
String message,parentId,student_id,title;
Timestamp timestamp;

    public Notification() {
    }

    public Notification(String message, String parentId, String student_id, String title, Timestamp timestamp) {
        this.message = message;
        this.parentId = parentId;
        this.student_id = student_id;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
