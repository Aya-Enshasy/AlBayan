package com.ayaenshasy.bayan.ui.fragments;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;
import static com.ayaenshasy.bayan.utils.Constant.USER_NAME;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.FragmentDailyHistoryStudentBinding;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyHistoryStudentFragment extends Fragment {
FragmentDailyHistoryStudentBinding binding;
    int max = 100;
    int min = 10;
    int total = max - min;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DailyHistoryStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyHistoryStudentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyHistoryStudentFragment newInstance(String param1, String param2) {
        DailyHistoryStudentFragment fragment = new DailyHistoryStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDailyHistoryStudentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.tvUserName.setText(getActivity().getIntent().getStringExtra(USER_NAME));
        binding.tvUserId.setText(getActivity().getIntent().getStringExtra(USER_ID));
        foldingCell();
        lottieImage();
        fluidSlider();
        return view;
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
