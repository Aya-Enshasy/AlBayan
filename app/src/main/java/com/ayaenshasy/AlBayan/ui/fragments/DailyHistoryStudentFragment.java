package com.ayaenshasy.AlBayan.ui.fragments;

import static com.ayaenshasy.AlBayan.utils.Constant.USER_ID;
import static com.ayaenshasy.AlBayan.utils.Constant.USER_NAME;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.database.AppDatabase;
import com.ayaenshasy.AlBayan.database.AttendanceDao;
import com.ayaenshasy.AlBayan.database.DatabaseClient;
import com.ayaenshasy.AlBayan.databinding.FragmentDailyHistoryStudentBinding;
import com.ayaenshasy.AlBayan.model.Attendance;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyHistoryStudentFragment extends BaseFragment {
    FragmentDailyHistoryStudentBinding binding;
    int max = 100;
    int min = 10;
    int total = max - min;
    boolean bool_fajer = false;
    boolean bool_dohor = false;
    boolean bool_aser = false;
    boolean bool_magreb = false;
    boolean bool_esha = false;
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
//                binding.foldingCell.toggle(false);
//                binding.cellTitleView.setVisibility(View.VISIBLE);
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

    @SuppressLint("SetTextI18n")
    private void getUserAttendance(String userId, String currentDate) {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendanceCollection = db.collection("attendance");
        DocumentReference attendanceRef = attendanceCollection
                .document(userId)
                .collection("records")
                .document(currentDate);

        attendanceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Attendance attendance = document.toObject(Attendance.class);
                    if (attendance != null) {
                        Map<String, Boolean> islamicPrayers = attendance.getIslamicPrayers();
                        if (islamicPrayers == null) {
                            islamicPrayers = new HashMap<>();
                        }

                        boolean asr = Boolean.TRUE.equals(islamicPrayers.get("asr"));
                        boolean isha = Boolean.TRUE.equals(islamicPrayers.get("Isha"));
                        boolean fajr = Boolean.TRUE.equals(islamicPrayers.get("Fajr"));
                        boolean dhuhr = Boolean.TRUE.equals(islamicPrayers.get("Dhuhr"));
                        boolean maghrib = Boolean.TRUE.equals(islamicPrayers.get("Maghrib"));
                        String planToday = attendance.getPlanToday();
                        String planTomorrow = attendance.getPlanTomorrow();
                        String planYesterday = attendance.getPlanYesterday();
                        String repeated = attendance.getRepeated();
                        String repeatedYesterday = attendance.getRepeatedYesterday();
                        String todayPercentage = attendance.getTodayPercentage();
                        String yesterdayPercentage = attendance.getYesterdayPercentage();
                        String rateTeacher = attendance.getRateTeacher();
                        String rateParent = attendance.getRateParent();

                        // Update your UI with the retrieved data
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                binding.etPlanTomorrow.setText(planTomorrow);
                                binding.etPlanYesterday.setText(planYesterday);
                                binding.etPlanToday.setText(planToday);
                                binding.etRepeated.setText(repeated);
                                binding.etRepeatedYesterday.setText(repeatedYesterday);
                                binding.etTodayPercentage.setText(todayPercentage + " %");
                                binding.etYesterdayPercentage.setText(yesterdayPercentage + " %");
                                if (isParent)
                                    binding.fluidSlider.setBubbleText(rateParent);
                                else
                                    binding.fluidSlider.setBubbleText(rateTeacher);
                                binding.fajer.setChecked(fajr);
                                binding.dohor.setChecked(dhuhr);
                                binding.aser.setChecked(asr);
                                binding.magreb.setChecked(maghrib);
                                binding.eshaa.setChecked(isha);
                                binding.fajer.setEnabled(!fajr);
                                binding.dohor.setEnabled(!dhuhr);
                                binding.aser.setEnabled(!asr);
                                binding.magreb.setEnabled(!maghrib);
                                binding.eshaa.setEnabled(!isha);
                            }
                        });
                    }
                } else {
                    Log.e("TAG", "No attendance document found");
                }
            } else {
                Log.e("TAG", "Failed to retrieve user attendance: " + task.getException());
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        });
    }

//    private void editAttendanceData(String userId, String currentDate) {
//        binding.progressBar.setVisibility(View.VISIBLE);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference attendanceCollection = FirebaseFirestore.getInstance().collection("attendance");
//        DocumentReference attendanceRef = attendanceCollection
//                .document(userId)
//                .collection("records")
//                .document(currentDate);
//
//        Map<String, Object> updatedData = new HashMap<>();
//        Map<String, Boolean> islamicPrayers = new HashMap<>();
//        islamicPrayers.put("Asr", bool_aser);
//        islamicPrayers.put("Dhuhr", bool_dohor);
//        islamicPrayers.put("Fajr", bool_fajer);
//        islamicPrayers.put("Maghrib", bool_magreb);
//        islamicPrayers.put("Isha", bool_esha);
//        updatedData.put("islamicPrayers", islamicPrayers);
//
//        if (isParent)
//            updatedData.put("rateParent", binding.fluidSlider.getBubbleText());
//        else
//            updatedData.put("rateTeacher", binding.fluidSlider.getBubbleText());
//
//
//        // Update the data in Firestore
//        attendanceRef.set(updatedData, SetOptions.merge())
//                .addOnSuccessListener(aVoid -> {
//                    binding.progressBar.setVisibility(View.GONE);
//                    Toast.makeText(getActivity(), "تم ارسال البيانات بنجاح", Toast.LENGTH_SHORT).show();
//                    Log.d("TAG", "Attendance data updated successfully.");
//                })
//                .addOnFailureListener(e -> {
//                    binding.progressBar.setVisibility(View.GONE);
//                    Log.e("TAG", "Failed to update attendance data: " + e.getMessage());
//                });
//    }

    private void editAttendanceData(String userId, String currentDate) {
        binding.progressBar.setVisibility(View.VISIBLE);

        // Create a new thread or use an Executor to perform database operations asynchronously
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            // Create an instance of your Room DAO (data access object)
            AttendanceDao attendanceDao = DatabaseClient.getInstance(context).getAppDatabase().attendanceDao();

            // Create a new AttendanceEntity object with the required data
            Attendance attendanceEntity = new Attendance();
            attendanceEntity.setId(userId);
            attendanceEntity.setDate(currentDate);
            attendanceEntity.setIslamicPrayers(createIslamicPrayersMap());

            if (isParent)
                attendanceEntity.setRateParent(binding.fluidSlider.getBubbleText());
            else
                attendanceEntity.setRateTeacher(binding.fluidSlider.getBubbleText());

            // Insert the AttendanceEntity into the Room database
            attendanceDao.insert(attendanceEntity);

            getActivity().runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "تم حفظ البيانات بنجاح", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Attendance data saved successfully to the local database.");
            });
        });
    }

    private Map<String, Boolean> createIslamicPrayersMap() {
        Map<String, Boolean> islamicPrayers = new HashMap<>();
        islamicPrayers.put("Asr", bool_aser);
        islamicPrayers.put("Dhuhr", bool_dohor);
        islamicPrayers.put("Fajr", bool_fajer);
        islamicPrayers.put("Maghrib", bool_magreb);
        islamicPrayers.put("Isha", bool_esha);
        return islamicPrayers;
    }


}
