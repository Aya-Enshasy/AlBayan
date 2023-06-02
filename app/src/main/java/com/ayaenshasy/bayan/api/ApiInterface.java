package com.ayaenshasy.bayan.api;


import com.ayaenshasy.bayan.model.PrayerTimesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("prayer-times")
    Call<PrayerTimesResponse> getPrayerTimes(@Query("city") String city, @Query("country") String country);
}
