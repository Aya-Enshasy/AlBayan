package com.ayaenshasy.bayan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivitySplashBinding;
import com.ayaenshasy.bayan.model.RemembranceDetailsModel;
import com.ayaenshasy.bayan.ui.activities.BottomNavigationBarActivity;
import com.ayaenshasy.bayan.ui.activities.LoginActivity;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale arabicLocale = new Locale("ar");
        Configuration config = getResources().getConfiguration();
        config.setLocale(arabicLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        runActivity();
        Log.e("onNewToken", preferences.readString(AppPreferences.DEVICE_TOKEN));

//        getPassword();
//        getUserData();
    }

    private void runActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
          if (preferences.getUserProfile().getRole() == null) {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                } else {
              preferences.getUserProfile();
              startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                    finish();
                }


            }
        }, 1000);
    }

    private List<RemembranceDetailsModel> convertJsonToChaptersList(String json) {
        List<RemembranceDetailsModel> chaptersList = new ArrayList<>();
        try {
            JSONObject chaptersObject = new JSONObject(json);
            JSONArray chaptersArray = chaptersObject.getJSONArray("content");

            for (int i = 0; i < chaptersArray.length(); i++) {
                JSONObject chapterObject = chaptersArray.getJSONObject(i);
                String chapterName = chapterObject.getString("zekr");
                int chapterNumber = chapterObject.getInt("repeat");

                RemembranceDetailsModel quranChapter = new RemembranceDetailsModel();
                quranChapter.setText(chapterName);
                quranChapter.setRepeat(chapterNumber);
                chaptersList.add(quranChapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chaptersList;
    }


}