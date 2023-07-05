package com.ayaenshasy.AlBayan.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.ayaenshasy.AlBayan.adapter.MyPagerAdapter;
import com.ayaenshasy.AlBayan.base.BaseActivity;
import com.ayaenshasy.AlBayan.databinding.ActivityDailyHistoryBinding;
import com.ayaenshasy.AlBayan.databinding.ActivityStudentDetailsBinding;
import com.ayaenshasy.AlBayan.ui.fragments.DailyHistoryStudentFragment;
import com.ayaenshasy.AlBayan.ui.fragments.ExamHistoryStudentFragment;
import com.ayaenshasy.AlBayan.ui.fragments.MonthlyHistoryStudentFragment;
import com.ayaenshasy.AlBayan.ui.fragments.TeacherNotificationsFragment;
import com.ayaenshasy.AlBayan.ui.fragments.MonthlyHistoryStudentFragment;
import com.ayaenshasy.AlBayan.ui.fragments.TeacherNotificationsFragment;

public class StudentDetailsActivity extends BaseActivity {
    ActivityStudentDetailsBinding binding;
    private MyPagerAdapter pagerAdapter;
    private Fragment[] fragments;
    private String[] fragmentTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create the fragments and titles based on the condition
        if (isParent) {
            fragments = new Fragment[]{
                    new DailyHistoryStudentFragment(),
                    new MonthlyHistoryStudentFragment(),
                    new ExamHistoryStudentFragment(),
                    new TeacherNotificationsFragment()
            };

            fragmentTitles = new String[]{
                    "السجل اليومي",
                    "السجل الشهري",
                    "سجل الاختبارات",
                    "رسائل المحفظ"
            };
        } else {
            fragments = new Fragment[]{
                    new DailyHistoryStudentFragment(),
                    new MonthlyHistoryStudentFragment(),
                    new ExamHistoryStudentFragment()
            };

            fragmentTitles = new String[]{
                    "السجل اليومي",
                    "السجل الشهري",
                    "سجل الاختبارات"
            };
        }

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments, fragmentTitles);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
}

