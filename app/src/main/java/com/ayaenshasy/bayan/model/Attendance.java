package com.ayaenshasy.bayan.model;

public class Attendance {
    private String id;
    private String date;
    private String planToday;
    private String todayPercentage;
    private String repeated;
    private String planTomorrow;

    // Empty constructor (required by Firebase Realtime Database)
    public Attendance() {
    }

    public Attendance(String id, String date, String planToday, String todayPercentage, String repeated, String planTomorrow) {
        this.id = id;
        this.date = date;
        this.planToday = planToday;
        this.todayPercentage = todayPercentage;
        this.repeated = repeated;
        this.planTomorrow = planTomorrow;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getPlanToday() {
        return planToday;
    }

    public String getTodayPercentage() {
        return todayPercentage;
    }

    public String getRepeated() {
        return repeated;
    }

    public String getPlanTomorrow() {
        return planTomorrow;
    }
}

