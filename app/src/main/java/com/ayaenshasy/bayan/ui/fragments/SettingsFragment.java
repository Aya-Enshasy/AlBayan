package com.ayaenshasy.bayan.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.ui.activities.ChangePasswordActivity;
import com.ayaenshasy.bayan.ui.activities.DailyHistoryActivity;
import com.ayaenshasy.bayan.ui.activities.EditUserProfileActivity;
import com.ayaenshasy.bayan.ui.activities.MonthlyHistoryActivity;
import com.ayaenshasy.bayan.ui.activities.SupportActivity;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.ui.activities.AddUserActivity;
import com.ayaenshasy.bayan.ui.activities.LoginActivity;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class SettingsFragment extends BaseFragment {
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
        getData();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        binding.userName.setText(currentUser.getName());
        binding.userRole.setText(role_name);
        binding.identifier.setText(currentUser.getName() + "");
        Glide.with(context).load(currentUser.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true).into(binding.userImage);

//         if (!AppPreferences.getInstance(getActivity()).getStringPreferences(Constant.USER_ID).equals("123456789")){
//            binding.changePassword.setVisibility(View.GONE);
//            binding.addUser.setVisibility(View.GONE);
//        }else {
//            binding.changePassword.setVisibility(View.VISIBLE);
//            binding.addUser.setVisibility(View.VISIBLE);
//        }
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

//        binding.month.setOnClickListener(View -> {
//            startActivity(new Intent(getActivity(), MonthlyHistoryActivity.class));
//        });

        binding.changePassword.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
        });

        binding.logOut.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            preferences.clearPreferences();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}