package com.ayaenshasy.bayan.model.user;

import android.net.Uri;

import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.Role;

import java.util.Map;

public class Student extends User {
    private  boolean isChecked;
    private String parentIdNumber;

    public Student() {
    }

    public Student(String name, String id, String phone, String image, Role role, String birthDate, String gender, String parentIdNumber, boolean isChecked) {
        super(name, id, phone, image, role, birthDate, gender);
        this.parentIdNumber = parentIdNumber;
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

    public String getParentIdNumber() {
        return parentIdNumber;
    }

    public void setParentIdNumber(String parentIdNumber) {
        this.parentIdNumber = parentIdNumber;
    }
}

