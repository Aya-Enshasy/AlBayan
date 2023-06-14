package com.ayaenshasy.bayan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ayaenshasy.bayan.databinding.ActivityDailyHistoryBinding;
import com.ayaenshasy.bayan.databinding.ActivityStudentDetailsBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class StudentDetailsActivity extends AppCompatActivity {
    ActivityStudentDetailsBinding binding;
    int max = 100;
    int min = 10;
    int total = max - min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        foldingCell();
        lottieImage();
        fluidSlider();


    }

    private void foldingCell(){
        binding.foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.foldingCell.toggle(false);
                binding.cellTitleView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void lottieImage(){
        binding.lottieImg.setAnimation(R.raw.user_profile);
        binding.lottieImg.loop(true);
        binding.lottieImg.playAnimation();
    }

    private void fluidSlider(){
        binding.fluidSlider.setPositionListener(new Function1<Float, Unit>() {
            @Override
            public Unit invoke(Float pos) {
                String value = String.valueOf((int) (min + (total * pos)));
                binding.fluidSlider.setBubbleText(value);
                return Unit.INSTANCE;
            }
        });
        binding.fluidSlider.setPosition(0.3f);
        binding.fluidSlider.setStartText(String.valueOf(min));
        binding.fluidSlider.setEndText(String.valueOf(max));

        binding.fluidSlider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Log.d("D", "setBeginTrackingListener");
                return Unit.INSTANCE;
            }
        });

        binding.fluidSlider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Log.d("D", "setEndTrackingListener");

                return Unit.INSTANCE;
            }
        });
    }}
