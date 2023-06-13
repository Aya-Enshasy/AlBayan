package com.ayaenshasy.bayan.model.user;

import android.net.Uri;

import com.ayaenshasy.bayan.model.Role;

public class Student extends User {
    private String parentIdNumber;

    public Student(String name, String idNumber, String parentIdNumber, String phoneNumber, String birthDate, String imageUri) {
        super(name, idNumber, phoneNumber, imageUri, Role.STUDENT,birthDate);
        this.parentIdNumber = parentIdNumber;
//        this.birthDate = birthDate;
    }


}

