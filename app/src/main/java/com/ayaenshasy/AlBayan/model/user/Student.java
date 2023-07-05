package com.ayaenshasy.AlBayan.model.user;

import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.Role;

import java.util.Map;

public class Student extends User {
    private  boolean isChecked;
    private String parentId;

    public Student() {
    }

    public Student(String name, String id, String phone, String image, Role role, String birthDate, String gender, String parentId, boolean isChecked) {
        super(name, id, phone, image, role, birthDate, gender);
        this.parentId = parentId;
        this.isChecked = isChecked;
    }
    private Map<String, Map<String, Attendance>> attendance;

    // Constructor...
    // Getters and setters...

    public Map<String, Map<String, Attendance>> getAttendance() {
        return attendance;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentIdNumber) {
        this.parentId = parentIdNumber;
    }
}

