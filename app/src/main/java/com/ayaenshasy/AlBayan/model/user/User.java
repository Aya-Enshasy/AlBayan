package com.ayaenshasy.AlBayan.model.user;

import com.ayaenshasy.AlBayan.model.Role;

public class User {
    private String name;
    private String id;
    private String phone;
    private String image;
    private Role role;
    private String birthDate;
    private String gender;
    private String fcm;

    public User() {
    }

    public User(String name, String id, String phone, String image, Role role, String birthDate, String gender) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.image = image;
        this.role = role;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public User(String name, String id, String phone, String image, Role role, String birthDate, String gender, String fcm) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.image = image;
        this.role = role;
        this.birthDate = birthDate;
        this.gender = gender;
        this.fcm = fcm;
    }

    public User(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }
}
