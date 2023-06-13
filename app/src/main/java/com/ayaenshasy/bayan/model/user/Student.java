package com.ayaenshasy.bayan.model.user;

import android.net.Uri;

import com.ayaenshasy.bayan.model.Role;

public class Student extends User {
    private String parentIdNumber;

    public Student() {
    }

    public Student(String name, String id, String phone, String image, Role role, String birthDate, String gender, String parentIdNumber ) {
        super(name, id, phone, image, role, birthDate, gender);
        this.parentIdNumber = parentIdNumber;
    }
}

