package com.ayaenshasy.AlBayan.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.User;
import com.ayaenshasy.AlBayan.utils.AppPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseFragment extends Fragment {
    public Dialog loader_dialog;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public AppPreferences preferences;
    Context context;
    Role role;
    String role_name;
    User currentUser;
    Boolean isParent;
    String currentDate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        preferences = new AppPreferences(context);
        String currentDate = getCurrentDate();

        isParent = preferences.getBooleanPreference(AppPreferences.IS_PARENT);
        if (isParent) {
            if (preferences.getParentProfile().getId() != null && !preferences.getParentProfile().getName().isEmpty()) {
//                role = preferences.getParentProfile().getRole();
//                role_name = role.name();
                currentUser = preferences.getParentProfile();
                currentUser.setRole(Role.PARENT);
            }
        } else {
            if (preferences.getUserProfile().getRole() != null && !preferences.getUserProfile().getName().isEmpty()) {
                role = preferences.getUserRole();
                role_name = preferences.getUserRole().name();
                currentUser = preferences.getUserProfile();
            }
        }

    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
