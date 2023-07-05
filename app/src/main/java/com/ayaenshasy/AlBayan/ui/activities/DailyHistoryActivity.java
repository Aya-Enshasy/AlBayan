package com.ayaenshasy.AlBayan.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.MyPagerAdapter;
import com.ayaenshasy.AlBayan.ui.fragments.CommittedStudentsFragment;
import com.ayaenshasy.AlBayan.ui.fragments.NotCommittedStudentsFragment;
import com.ayaenshasy.AlBayan.ui.fragments.NotCommittedStudentsFragment;
import com.google.android.material.tabs.TabLayout;

public class DailyHistoryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_history);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments, fragmentTitles);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    Fragment[] fragments = {
            new CommittedStudentsFragment(),
            new NotCommittedStudentsFragment(),
    };

    String[] fragmentTitles = {
            "الطلاب الملتزمين",
            "الطلاب الغير ملتزمين",
       };

}