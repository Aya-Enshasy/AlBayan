package com.ayaenshasy.bayan.base;


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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.utils.AppContextWrapper;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.google.firebase.FirebaseApp;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
     public Dialog   loader_dialog;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
      AppPreferences preferences;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        preferences = AppPreferences.getInstance(this);


         loader_dialog = new Dialog(this);

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sp.edit();


    }





    protected void loaderDialog() {
        loader_dialog.setContentView(R.layout.loader_dialog);
        loader_dialog.setCancelable(false);
        loader_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        loader_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        loader_dialog.show();
    }


}