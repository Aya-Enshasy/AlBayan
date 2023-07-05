package com.ayaenshasy.AlBayan.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Define the Exam entity class
@Entity(tableName = "exams")
public class Exam {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String examId;
    @ColumnInfo(name = "studentId")
    private String studentId;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "degree")
    private String degree;
    @ColumnInfo(name = "mosque")
    private String mosque;
    @ColumnInfo(name = "date")
    private String date;
    private String image;
//    @ColumnInfo(name = "imageData")
//    private byte[] imageData; // New field for the image data


    public Exam() {
    }

    public Exam(String degree, String image, String mosque, String name, String date) {
        this.degree = degree;
        this.image = image;
        this.mosque = mosque;
        this.name = name;
        this.date = date;
    }

//    public Exam(String id, String name, String degree, String mosque, String date, byte[] imageData) {
//        this.id = id;
//        this.name = name;
//        this.degree = degree;
//        this.mosque = mosque;
//        this.date = date;
////        this.imageData = imageData;
//    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

//    public byte[] getImageData() {
//        return imageData;
//    }
//
//    public void setImageData(byte[] imageData) {
//        this.imageData = imageData;
//    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
