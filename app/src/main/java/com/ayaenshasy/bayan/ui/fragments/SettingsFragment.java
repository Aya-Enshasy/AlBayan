package com.ayaenshasy.bayan.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.DailyHistoryActivity;
import com.ayaenshasy.bayan.EditUserProfileActivity;
import com.ayaenshasy.bayan.MonthlyHistoryActivity;
import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.SupportActivity;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.ui.activities.AddUserActivity;
import com.ayaenshasy.bayan.ui.activities.LoginActivity;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;


public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        openActivities();

        return view;
    }

    private void openActivities() {
        binding.addUser.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), AddUserActivity.class));
        });

        binding.editProfile.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), EditUserProfileActivity.class));
        });

        binding.support.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), SupportActivity.class));
        });

        binding.day.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), DailyHistoryActivity.class));
        });

        binding.month.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), MonthlyHistoryActivity.class));
        });

        binding.logOut.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            AppPreferences.getInstance(getActivity()).setStringPreferences(Constant.LOGIN, "");
        });
    }
}