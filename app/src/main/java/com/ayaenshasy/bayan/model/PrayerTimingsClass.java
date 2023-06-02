package com.ayaenshasy.bayan.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrayerTimingsClass {

    private static final String API_BASE_URL = "https://api.aladhan.com/v1/";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    public interface PrayerTimesListener {
        void onPrayerTimesReceived(PrayerTimes prayerTimes);
        void onFailure(String errorMessage);
    }

    public void getPrayerTimes(String address, PrayerTimesListener listener) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().create();

        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());

        String apiUrl = API_BASE_URL + "timingsByAddress/" + date + "?address=" + address;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    PrayerTimesResponse prayerTimesResponse = gson.fromJson(responseBody, PrayerTimesResponse.class);
                    PrayerTimes prayerTimes = prayerTimesResponse.getData().getTimings();

                    listener.onPrayerTimesReceived(prayerTimes);
                } else {
                    listener.onFailure("Request was not successful. Response code: " + response.code());
                }
            }
        });
    }

    private static class PrayerTimesResponse {
        private PrayerTimesData data;

        public PrayerTimesData getData() {
            return data;
        }
    }

    private static class PrayerTimesData {
        private PrayerTimes timings;

        public PrayerTimes getTimings() {
            return timings;
        }
    }

    public static class PrayerTimes {
        private String Fajr;
        private String Sunrise;
        private String Dhuhr;
        private String Asr;
        private String Maghrib;
        private String Isha;

        public String getFajr() {
            return Fajr;
        }

        public String getSunrise() {
            return Sunrise;
        }

        public String getDhuhr() {
            return Dhuhr;
        }

        public String getAsr() {
            return Asr;
        }

        public String getMaghrib() {
            return Maghrib;
        }

        public String getIsha() {
            return Isha;
        }
    }
}


