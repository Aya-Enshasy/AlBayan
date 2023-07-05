package com.ayaenshasy.AlBayan.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public class Attendance {
    @PrimaryKey()
    @NonNull
    private String id;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "planToday")
    private String planToday;

    @ColumnInfo(name = "todayPercentage")
    private String todayPercentage;

    @ColumnInfo(name = "repeated")
    private String repeated;

    @ColumnInfo(name = "islamicPrayers")
    private Map<String, Boolean> islamicPrayers;

    @ColumnInfo(name = "planYesterday")
    private String planYesterday;

    @ColumnInfo(name = "yesterdayPercentage")
    private String yesterdayPercentage;

    @ColumnInfo(name = "repeatedYesterday")
    private String repeatedYesterday;
    @ColumnInfo(name = "planTomorrow")
    private String planTomorrow;
    @ColumnInfo(name = "rateTeacher")
    private String rateTeacher;
    @ColumnInfo(name = "rateParent")
    private String rateParent;
    @ColumnInfo(name = "notes")
    private String notes;

    // Empty constructor (required by Firebase Realtime Database)
    public Attendance() {
    }

    public Attendance(String id, String date, String planToday, String todayPercentage,
                      String repeated, String planYesterday, String yesterdayPercentage,
                      String repeatedYesterday, String planTomorrow,String notes) {
        this.id = id;
        this.date = date;
        this.planToday = planToday;
        this.todayPercentage = todayPercentage;
        this.repeated = repeated;
        this.planYesterday = planYesterday;
        this.yesterdayPercentage = yesterdayPercentage;
        this.repeatedYesterday = repeatedYesterday;
        this.planTomorrow = planTomorrow;
    }


    public Attendance(String id, String date, String planToday, String todayPercentage, String repeated,
                      String planYesterday, String yesterdayPercentage, String repeatedYesterday,
                      Map<String, Boolean> islamicPrayers, String planTomorrow) {
        this.id = id;
        this.date = date;
        this.planToday = planToday;
        this.todayPercentage = todayPercentage;
        this.repeated = repeated;
        this.islamicPrayers = islamicPrayers;
        this.planYesterday = planYesterday;
        this.yesterdayPercentage = yesterdayPercentage;
        this.repeatedYesterday = repeatedYesterday;
        this.planTomorrow = planTomorrow;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlanToday() {
        return planToday;
    }

    public void setPlanToday(String planToday) {
        this.planToday = planToday;
    }

    public String getTodayPercentage() {
        return todayPercentage;
    }

    public void setTodayPercentage(String todayPercentage) {
        this.todayPercentage = todayPercentage;
    }

    public String getRepeated() {
        return repeated;
    }

    public void setRepeated(String repeated) {
        this.repeated = repeated;
    }

    public Map<String, Boolean> getIslamicPrayers() {
        return islamicPrayers;
    }

    public void setIslamicPrayers(Map<String, Boolean> islamicPrayers) {
        this.islamicPrayers = islamicPrayers;
    }

    public String getPlanYesterday() {
        return planYesterday;
    }

    public void setPlanYesterday(String planYesterday) {
        this.planYesterday = planYesterday;
    }

    public String getYesterdayPercentage() {
        return yesterdayPercentage;
    }

    public void setYesterdayPercentage(String yesterdayPercentage) {
        this.yesterdayPercentage = yesterdayPercentage;
    }

    public String getRepeatedYesterday() {
        return repeatedYesterday;
    }

    public void setRepeatedYesterday(String repeatedYesterday) {
        this.repeatedYesterday = repeatedYesterday;
    }

    public String getPlanTomorrow() {
        return planTomorrow;
    }

    public void setPlanTomorrow(String planTomorrow) {
        this.planTomorrow = planTomorrow;
    }

    public String getRateParent() {
        return rateParent;
    }

    public void setRateParent(String rateParent) {
        this.rateParent = rateParent;
    }

    public String getRateTeacher() {
        return rateTeacher;
    }

    public void setRateTeacher(String rateTeacher) {
        this.rateTeacher = rateTeacher;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}


