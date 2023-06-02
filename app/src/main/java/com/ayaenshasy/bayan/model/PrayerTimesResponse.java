package com.ayaenshasy.bayan.model;

import android.service.autofill.Sanitizer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PrayerTimesResponse implements Serializable {
    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        @SerializedName("city")
        private String city;

        @SerializedName("country")
        private String country;

        @SerializedName("prayer_times")
        private List<PrayerTime> prayerTimes;

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public List<PrayerTime> getPrayerTimes() {
            return prayerTimes;
        }
    }

    public class PrayerTime {
        @SerializedName("prayer_name")
        private String prayerName;

        @SerializedName("prayer_time")
        private String prayerTime;

        public String getPrayerName() {
            return prayerName;
        }

        public String getPrayerTime() {
            return prayerTime;
        }
    }
}

