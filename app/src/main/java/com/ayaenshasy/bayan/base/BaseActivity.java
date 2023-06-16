package com.ayaenshasy.bayan.base;


import static com.ayaenshasy.bayan.utils.AppPreferences.IS_PARENT;
import static com.ayaenshasy.bayan.utils.AppPreferences.PREF_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.LoaderDialogBinding;
import com.ayaenshasy.bayan.databinding.SoraItemBinding;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppContextWrapper;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.google.firebase.FirebaseApp;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    public Dialog loader_dialog;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public AppPreferences preferences;
    Context context;
    public Role role;
    public String role_name;
    public User user;
    public boolean isParent;
    LoaderDialogBinding loaderDialogBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        preferences = AppPreferences.getInstance(this);
        context = this;
        loaderDialogBinding = LoaderDialogBinding.inflate(LayoutInflater.from(this), null, false);

//        role = preferences.getUserRole();
//        role_name = preferences.getUserRole().name();
        user = preferences.getUserProfile();
        isParent = preferences.getBooleanPreference(IS_PARENT);

        loader_dialog = new Dialog(this);

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sp.edit();


    }

    protected void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    protected void loaderDialog() {
        loaderDialogBinding.lottieImg.setAnimation(R.raw.waiting_sand);
        loaderDialogBinding.lottieImg.loop(true);
        loaderDialogBinding.lottieImg.playAnimation();

        // Check if the activity is running
        if (!isFinishing()) {
            // Create the dialog with the activity context
            loader_dialog = new Dialog(this);

            // Set the content view of the dialog
            loader_dialog.setContentView(R.layout.loader_dialog);
            loader_dialog.setCancelable(false);
            loader_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
            loader_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);

            // Show the dialog
            loader_dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Dismiss the dialog if it is showing
        if (loader_dialog != null && loader_dialog.isShowing()) {
            loader_dialog.dismiss();
        }
    }

}
