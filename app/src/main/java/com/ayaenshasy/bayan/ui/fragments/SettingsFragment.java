package com.ayaenshasy.bayan.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.dialogs.LogoutDialog;
import com.ayaenshasy.bayan.ui.activities.ChangePasswordActivity;
import com.ayaenshasy.bayan.ui.activities.DailyHistoryActivity;
import com.ayaenshasy.bayan.ui.activities.EditUserProfileActivity;
import com.ayaenshasy.bayan.ui.activities.SupportActivity;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.ui.activities.AddUserActivity;
import com.ayaenshasy.bayan.ui.activities.LoginActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class SettingsFragment extends BaseFragment implements LogoutDialog.LogoutDialogListener{
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


        if (currentUser.getRole().name().equals("TEACHER")){
            binding.day.setVisibility(View.VISIBLE);
        }else {
            binding.day.setVisibility(View.GONE);
        }

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
            showLogoutDialog();

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void showLogoutDialog() {
        LogoutDialog dialog = new LogoutDialog();
        dialog.setListener(this);
        dialog.show(getActivity().getSupportFragmentManager(), "logout_dialog");
    }

    @Override
    public void onLogoutConfirmed() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        preferences.clearPreferences();
    }

    @Override
    public void onLogoutCancelled() {
    }
}