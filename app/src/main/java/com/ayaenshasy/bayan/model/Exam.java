package com.ayaenshasy.bayan.model;

public class Exam {
    String degree,
            image,
            mosque,
            name;

    public Exam() {
    }

    public Exam(String degree, String image, String mosque, String name) {
        this.degree = degree;
        this.image = image;
        this.mosque = mosque;
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMosque() {
        return mosque;
    }

    public void setMosque(String mosque) {
        this.mosque = mosque;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
