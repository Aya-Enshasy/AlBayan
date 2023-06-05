package com.ayaenshasy.bayan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ayaenshasy.bayan.databinding.ActivitySplashBinding;
import com.ayaenshasy.bayan.ui.activities.BottomNavigationBarActivity;
import com.ayaenshasy.bayan.ui.activities.LoginActivity;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;


public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        runActivity();

    }
    private void runActivity() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (AppPreferences.getInstance(getBaseContext()).getStringPreferences(Constant.LOGIN).equals("")){
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                    finish();
                }



            }
        }, 1000);
    }

}