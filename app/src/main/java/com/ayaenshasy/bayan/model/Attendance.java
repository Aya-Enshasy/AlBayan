package com.ayaenshasy.bayan.model;

import java.util.List;
import java.util.Map;

public class Attendance {
    private String id;
    private String date;
    private String planToday;
    private String todayPercentage;
    private String repeated;
    private Map<String, Boolean> islamicPrayers;
    private String planYesterday;
    private String yesterdayPercentage;
    private String repeatedYesterday;
    private String planTomorrow;

    // Empty constructor (required by Firebase Realtime Database)
    public Attendance() {
    }

    public Attendance(String id, String date, String planToday, String todayPercentage,
                      String repeated, String planYesterday, String yesterdayPercentage,
                      String repeatedYesterday, String planTomorrow) {
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


    public Attendance(String id, String date, String planToday, String todayPercentage, String repeated, String planYesterday, String yesterdayPercentage, String repeatedYesterday, Map<String, Boolean> islamicPrayers, String planTomorrow) {
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


    // Override toString() method for debugging or displaying purposes
    @Override
    public String toString() {
        return "Attendance{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", planToday='" + planToday + '\'' +
                ", todayPercentage='" + todayPercentage + '\'' +
                ", repeated='" + repeated + '\'' +
                ", planYesterday='" + planYesterday + '\'' +
                ", yesterdayPercentage='" + yesterdayPercentage + '\'' +
                ", repeatedYesterday='" + repeatedYesterday + '\'' +
                ", islamicPrayers=" + islamicPrayers +
                ", planTomorrow='" + planTomorrow + '\'' +
                '}';
    }}


