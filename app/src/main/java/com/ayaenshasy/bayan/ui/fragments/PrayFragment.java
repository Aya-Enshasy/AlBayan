package com.ayaenshasy.bayan.ui.fragments;

import static com.ayaenshasy.bayan.utils.TimeUpdater.KEY_PRAY_DATE;
import static com.ayaenshasy.bayan.utils.TimeUpdater.KEY_PRAY_TIME;

import android.os.Bundle;

import com.ayaenshasy.bayan.databinding.FragmentPrayBinding;
import com.ayaenshasy.bayan.model.PrayerTimingsClass;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.base.BaseFragment;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.TimeUpdater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PrayFragment extends BaseFragment{

    FragmentPrayBinding binding;
    private PrayerTimingsClass prayerTimesHelper;
     private static final String KEY_FAJR = "fajr";
    private static final String KEY_DHUHR = "dhuhr";
    private static final String KEY_ASR = "asr";
    private static final String KEY_MAGHRIB = "maghrib";
    private static final String KEY_ISHA = "isha";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PrayFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PrayFragment newInstance() {
        PrayFragment fragment = new PrayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPrayBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        prayerTimesHelper = new PrayerTimingsClass();
//        fetchPrayerTimes();

        TimeUpdater timeUpdater = new TimeUpdater(binding.fajer,binding.dohor,binding.aser,binding.magrep,binding.aisha,binding.date,binding.prayTime,binding.prayDate,getActivity());
        timeUpdater.startUpdatingTime();

        getDate();
        getData();

        return view;
    }
    private void getData(){
        String fajr =  AppPreferences.getInstance(getActivity()).getStringPreference(KEY_FAJR);
        String dhuhr =AppPreferences.getInstance(getActivity()).getStringPreference(KEY_DHUHR);
        String asr = AppPreferences.getInstance(getActivity()).getStringPreference(KEY_ASR);
        String maghrib = AppPreferences.getInstance(getActivity()).getStringPreference(KEY_MAGHRIB);
        String isha =AppPreferences.getInstance(getActivity()).getStringPreference(KEY_ISHA);
        String pray_date =AppPreferences.getInstance(getActivity()).getStringPreference(KEY_PRAY_DATE);
        String pray_time =AppPreferences.getInstance(getActivity()).getStringPreference(KEY_PRAY_TIME);

        binding.fajer.setText(fajr);
        binding.dohor.setText(dhuhr);
        binding.aser.setText(asr);
        binding.magrep.setText(maghrib);
        binding.aisha.setText(isha);
        binding.prayDate.setText(pray_date);
        binding.prayTime.setText(pray_time);
    }

    private void getDate(){

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String formattedDate = dateFormat.format(currentDate);
        binding.date.setText(formattedDate);
        System.out.println("Current Date: " + formattedDate);
    }

    private boolean isPrayerTime(String prayerTime) {
         Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = sdf.format(currentTime.getTime());

         return currentTimeString.equals(prayerTime);
    }

    public void showPrayerNotification(String title, String message) {
        showPrayerNotification(title, message);
    }

 }
