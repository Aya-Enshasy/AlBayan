package com.ayaenshasy.bayan.model.user;

import android.net.Uri;

import com.ayaenshasy.bayan.model.Role;

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

