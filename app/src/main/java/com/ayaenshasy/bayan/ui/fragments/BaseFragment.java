package com.ayaenshasy.bayan.ui.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.ayaenshasy.bayan.utils.AppPreferences.PREF_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.google.firebase.FirebaseApp;

public class BaseFragment extends Fragment {
    public Dialog loader_dialog;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public AppPreferences preferences;
    Context context;
    Role role;
    String role_name;
    User user;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity().getApplicationContext();
        preferences = new AppPreferences(context);
        role = preferences.getUserRole();
        role_name = preferences.getUserRole().name();
         user = preferences.getUserProfile();

//        lang = sharedPreferences.readString(AppSharedPreferences.LANG);
    }
}
