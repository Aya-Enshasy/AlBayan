package com.ayaenshasy.AlBayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.ayaenshasy.AlBayan.ui.fragments.HomeFragment;

import com.ayaenshasy.AlBayan.ui.fragments.PrayFragment;
 import com.ayaenshasy.AlBayan.ui.fragments.QuranFragment;
import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.ui.fragments.RemembranceFragment;
import com.ayaenshasy.AlBayan.ui.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationBarActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        openFragment(HomeFragment.newInstance());

        if (getIntent().getStringExtra("QURAAN")!=null){
            openFragment(QuranFragment.newInstance());
            bottomNavigationView.setSelectedItemId(R.id.Quran);
        }else if (getIntent().getStringExtra("AZKAR")!=null){
            openFragment(RemembranceFragment.newInstance());
            bottomNavigationView.setSelectedItemId(R.id.Remembrance);
        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Home) {
                    openFragment(HomeFragment.newInstance());
                } else if (item.getItemId() == R.id.Quran) {
                    openFragment(QuranFragment.newInstance());
                } else if (item.getItemId() == R.id.Remembrance) {
                    openFragment(RemembranceFragment.newInstance());
                }  else if (item.getItemId() == R.id.Settings) {
                    openFragment(SettingsFragment.newInstance());
                }   else {
                    openFragment(PrayFragment.newInstance());
                }
                return true;
            }
        });
    }

    void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.viewpager1, fragment);
        fragmentTransaction.commit();
    }



}