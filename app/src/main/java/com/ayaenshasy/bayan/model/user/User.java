package com.ayaenshasy.bayan.model.user;

import android.net.Uri;

import com.ayaenshasy.bayan.model.Role;

public class User {
    private String name;
    private String idNumber;
    private String phoneNumber;
    private String imageUri;
    private Role role;
    private String birthDate;

    public User() {
    }

    public User(String name, String idNumber, String phoneNumber, String imageUri, Role role, String birthDate) {
        this.name = name;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.imageUri = imageUri;
        this.role = role;
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageUri() {
        return imageUri;
    }
}
