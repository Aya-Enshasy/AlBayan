package com.ayaenshasy.bayan;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;
import static com.ayaenshasy.bayan.utils.Constant.USER_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ayaenshasy.bayan.adapter.MyPagerAdapter;
import com.ayaenshasy.bayan.databinding.ActivityDailyHistoryBinding;
import com.ayaenshasy.bayan.databinding.ActivityStudentDetailsBinding;
import com.ayaenshasy.bayan.ui.fragments.CommittedStudentsFragment;
import com.ayaenshasy.bayan.ui.fragments.DailyHistoryStudentFragment;
import com.ayaenshasy.bayan.ui.fragments.ExamHistoryStudentFragment;
import com.ayaenshasy.bayan.ui.fragments.MonthlyHistoryStudentFragment;
import com.ayaenshasy.bayan.ui.fragments.NotCommittedStudentsFragment;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class StudentDetailsActivity extends AppCompatActivity {
    ActivityStudentDetailsBinding binding;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments, fragmentTitles);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);




    }
    Fragment[] fragments = {
            new DailyHistoryStudentFragment(),
            new MonthlyHistoryStudentFragment(),
            new ExamHistoryStudentFragment()
    };

    String[] fragmentTitles = {
            "السجل اليومي",
            "السجل الشهري",
            "سجل الاختبارات ",
    };
}
