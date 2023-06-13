package com.ayaenshasy.bayan.model.user;

import android.net.Uri;

import com.ayaenshasy.bayan.model.Role;

public class Student extends User {
    private String parentIdNumber;
    private String teacherIdNumber;

//    public Student(String parentIdNumber, String teacherIdNumber) {
//        this.parentIdNumber = parentIdNumber;
//        this.teacherIdNumber = teacherIdNumber;
//    }

    public Student(String name, String idNumber, String phoneNumber, String imageUri, Role role, String birthDate,
                   String parentIdNumber, String teacherIdNumber) {
        super(name, idNumber, phoneNumber, imageUri, role, birthDate);
        this.parentIdNumber = parentIdNumber;
        this.teacherIdNumber = teacherIdNumber;
    }
}

