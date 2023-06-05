package com.ayaenshasy.bayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import com.ayaenshasy.bayan.ui.fragments.HomeFragment;

 import com.ayaenshasy.bayan.ui.fragments.PrayFragment;
import com.ayaenshasy.bayan.ui.fragments.QuranFragment;
import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.ui.fragments.RemembranceFragment;
import com.ayaenshasy.bayan.ui.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationBarActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

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