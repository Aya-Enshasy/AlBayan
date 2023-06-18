package com.ayaenshasy.bayan.ui.fragments;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;
import static com.ayaenshasy.bayan.utils.Constant.USER_NAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.FragmentDailyHistoryStudentBinding;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    boolean bool_fajer = false;
    boolean bool_dohor = false;
    boolean bool_aser = false;
    boolean bool_magreb = false;
    boolean bool_esha = false;
    String currentDate;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DailyHistoryStudentFragment() {
        // Required empty public constructor
    }

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

        init();

        return view;
    }

    private void init() {
        binding.tvUserName.setText(getActivity().getIntent().getStringExtra(USER_NAME));
        binding.tvUserId.setText(getActivity().getIntent().getStringExtra(USER_ID));
        foldingCell();
        lottieImage();
        fluidSlider();
        checkBoxes();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = dateFormat.format(new Date());
//        currentDate = "2023-06-15";

        getUserAttendance(getActivity().getIntent().getStringExtra(USER_ID), currentDate);

        binding.addBtn.setOnClickListener(View -> {
            editAttendanceData(getActivity().getIntent().getStringExtra(USER_ID), currentDate);
        });
    }

    private void foldingCell() {
        binding.foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.foldingCell.toggle(false);
                binding.cellTitleView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void lottieImage() {
        binding.lottieImg.setAnimation(R.raw.user_profile);
        binding.lottieImg.loop(true);
        binding.lottieImg.playAnimation();
    }

    private void fluidSlider() {
        binding.fluidSlider.setPositionListener(new Function1<Float, Unit>() {
            @Override
            public Unit invoke(Float pos) {
                String value = String.valueOf((int) (min + (total * pos)));
                binding.fluidSlider.setBubbleText(value);
                return Unit.INSTANCE;
            }
        });
        binding.fluidSlider.setPosition(0.5f);
        binding.fluidSlider.setStartText(String.valueOf(min));
        binding.fluidSlider.setEndText(String.valueOf(max));

        binding.fluidSlider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return Unit.INSTANCE;
            }
        });

        binding.fluidSlider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Log.e("setEndTrackingListener", binding.fluidSlider.getBubbleText() + "");

                return Unit.INSTANCE;
            }
        });
    }

    private void checkBoxes() {
        binding.fajer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bool_fajer = true;
                } else {
                    bool_fajer = false;
                }
            }
        });
        binding.dohor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bool_dohor = true;
                } else {
                    bool_dohor = false;
                }
            }
        });
        binding.aser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bool_aser = true;
                } else {
                    bool_aser = false;
                }
            }
        });
        binding.magreb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bool_magreb = true;
                } else {
                    bool_magreb = false;
                }
            }
        });
        binding.eshaa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bool_esha = true;
                } else {
                    bool_esha = false;
                }
            }
        });
    }

    private void getUserAttendance(String userId, String currentDate) {
        binding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference attendanceRef = databaseRef.child("attendance").child(currentDate).child(userId);

        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binding.progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    Map<String, Object> attendanceData = (Map<String, Object>) dataSnapshot.getValue();
                    Map<String, Boolean> prayerData = (Map<String, Boolean>) attendanceData.get("islamicPrayers");

                    boolean asr = prayerData.get("Asr");
                    boolean isha = prayerData.get("Isha");
                    boolean fajr = prayerData.get("Fajr");
                    boolean dhuhr = prayerData.get("Dhuhr");
                    boolean maghrib = prayerData.get("Maghrib");
                    String planToday = dataSnapshot.child("planToday").getValue(String.class);
                    String planTomorrow = dataSnapshot.child("planTomorrow").getValue(String.class);
                    String planYesterday = dataSnapshot.child("planYesterday").getValue(String.class);
                    String repeated = dataSnapshot.child("repeated").getValue(String.class);
                    String repeatedYesterday = dataSnapshot.child("repeatedYesterday").getValue(String.class);
                    String todayPercentage = dataSnapshot.child("todayPercentage").getValue(String.class);
                    String yesterdayPercentage = dataSnapshot.child("yesterdayPercentage").getValue(String.class);
                    if ( dataSnapshot.child("notes")!=null){
                        String notes = dataSnapshot.child("notes").getValue(String.class);
                        binding.notes.setText(notes);
                    }
                    if ( dataSnapshot.child("rate2")!=null){
                        Float rate = dataSnapshot.child("rate2").getValue(Float.class);
                        if ( rate!=null)
                          binding.fluidSlider.setPosition(rate);
                     }

                    binding.etPlanTomorrow.setText(planTomorrow);
                    binding.etPlanYesterday.setText(planYesterday);
                    binding.etPlanToday.setText(planToday);
                    binding.etRepeated.setText(repeated);
                    binding.etRepeatedYesterday.setText(repeatedYesterday);
                    binding.etTodayPercentage.setText(todayPercentage + " %");
                    binding.etYesterdayPercentage.setText(yesterdayPercentage + " %");
                    binding.fajer.setChecked(fajr);
                    binding.dohor.setChecked(dhuhr);
                    binding.aser.setChecked(asr);
                    binding.magreb.setChecked(maghrib);
                    binding.eshaa.setChecked(isha);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("TAG", "Failed to retrieve user attendance: " + databaseError.getMessage());
            }
        });
    }

    private void editAttendanceData(String userId, String currentDate) {
        binding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference attendanceRef = databaseRef.child("attendance").child(currentDate).child(userId);
        Map<String, Object> updatedData = new HashMap<>();

        updatedData.put("islamicPrayers/Fajr", bool_fajer);
        updatedData.put("islamicPrayers/Dhuhr", bool_dohor);
        updatedData.put("islamicPrayers/Asr", bool_aser);
        updatedData.put("islamicPrayers/Maghrib", bool_magreb);
        updatedData.put("islamicPrayers/Isha", bool_esha);
        updatedData.put("notes", binding.notes.getText().toString());
        updatedData.put("rate", binding.fluidSlider.getBubbleText());
        updatedData.put("rate2", binding.fluidSlider.getPosition());

        // Update the data in the database
        attendanceRef.updateChildren(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "تم ارسال البيانات بنجاح", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Attendance data updated successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.e("TAG", "Failed to update attendance data: " + e.getMessage());
                    }
                });
    }


}
