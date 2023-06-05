package com.ayaenshasy.bayan.ui.fragments;

import android.os.Bundle;

import com.ayaenshasy.bayan.databinding.FragmentPrayBinding;
import com.ayaenshasy.bayan.model.PrayerTimingsClass;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrayFragment extends BaseFragment{

    FragmentPrayBinding binding;
    private PrayerTimingsClass prayerTimesHelper;

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
        fetchPrayerTimes();
        getDate();

        return view;
    }
    private void getDate(){

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String formattedDate = dateFormat.format(currentDate);
        binding.date.setText(formattedDate);
        System.out.println("Current Date: " + formattedDate);
    }

    private void fetchPrayerTimes() {
        prayerTimesHelper.getPrayerTimes("Palestine", new PrayerTimingsClass.PrayerTimesListener() {
            @Override
            public void onPrayerTimesReceived(PrayerTimingsClass.PrayerTimes prayerTimes) {
                // Update the UI with the prayer times
                binding.fajer.setText(prayerTimes.getFajr());
                binding.dohor.setText(prayerTimes.getDhuhr());
                binding.aser.setText(prayerTimes.getAsr());
                binding.magrep.setText(prayerTimes.getMaghrib());
                binding.aisha.setText(prayerTimes.getIsha());
                 Log.e("prayerTimes.getFajr()",prayerTimes.getSunrise());
             }

            @Override
            public void onFailure(String errorMessage) {
                // Handle error
                // You can show an error message or log the error
            }
        });
    }


}
