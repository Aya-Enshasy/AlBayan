package com.ayaenshasy.AlBayan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ayaenshasy.AlBayan.base.BaseActivity;
import com.ayaenshasy.AlBayan.databinding.ActivitySplashBinding;
import com.ayaenshasy.AlBayan.ui.activities.BottomNavigationBarActivity;
import com.ayaenshasy.AlBayan.ui.activities.LoginActivity;
import com.ayaenshasy.AlBayan.utils.AppPreferences;

import java.util.Locale;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    ActivitySplashBinding binding;
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        Locale arabicLocale = new Locale("ar");
        Configuration config = getResources().getConfiguration();
        config.setLocale(arabicLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(binding.getRoot());

        runActivity();

        Log.e("onNewToken", preferences.readString(AppPreferences.DEVICE_TOKEN));
    }

    private void runActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferences.getUserProfile().getId() == null && preferences.getUserProfile().getId() == null &&
                        preferences.getParentProfile().getId() == null) {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                } else {
                    preferences.getUserProfile();
                    startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                    finish();
                }


            }
        }, 3000);
    }

}