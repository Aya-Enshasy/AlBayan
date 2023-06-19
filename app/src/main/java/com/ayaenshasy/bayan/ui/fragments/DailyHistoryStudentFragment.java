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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = dateFormat.format(new Date());
//        currentDate = "2023-06-15";

        getUserAttendance(getActivity().getIntent().getStringExtra(USER_ID), currentDate);

        binding.addBtn.setOnClickListener(View -> {
            editAttendanceData(getActivity().getIntent().getStringExtra(USER_ID), currentDate);
        });
    }

    private void foldingCell() {
//        binding.foldingCell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.foldingCell.toggle(false);
//                binding.cellTitleView.setVisibility(View.VISIBLE);
//            }
//        });
    }

    private void lottieImage() {
        binding.lottieImg.setAnimation(R.raw.user_profile);
        binding.lottieImg.loop(false);
        binding.lottieImg.playAnimation();
    }



    private void getUserAttendance(String userId, String currentDate) {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference attendanceRef = db.collection("attendance")
                .document(currentDate)
                .collection(userId)
                .document(userId);

        attendanceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                binding.progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String planToday = document.getString("planToday");
                        String planTomorrow = document.getString("planTomorrow");
                        String planYesterday = document.getString("planYesterday");
                        String repeated = document.getString("repeated");
                        String repeatedYesterday = document.getString("repeatedYesterday");
                        String todayPercentage = document.getString("todayPercentage");
                        String yesterdayPercentage = document.getString("yesterdayPercentage");

                        if (document.contains("notes")) {
                            String notes = document.getString("notes");
                            binding.notes.setText(notes);
                        }

                        if (document.contains("rate")) {
                            String rate = document.getString("rate");
                            binding.fluidSlider.setText(rate);
                        }

                        binding.etPlanTomorrow.setText(planTomorrow);
                        binding.etPlanYesterday.setText(planYesterday);
                        binding.etPlanToday.setText(planToday);
                        binding.etRepeated.setText(repeated);
                        binding.etRepeatedYesterday.setText(repeatedYesterday);
                        binding.etTodayPercentage.setText(todayPercentage + " %");
                        binding.etYesterdayPercentage.setText(yesterdayPercentage + " %");

                        Log.e("TAG", "Successfully retrieved user attendance: " + planTomorrow);
                    } else {
                        Log.e("TAG", "User attendance document does not exist");
                    }
                } else {
                    Log.e("TAG", "Failed to retrieve user attendance: " + task.getException().getMessage());
                }
            }
        });
    }

    private void editAttendanceData(String userId, String currentDate) {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        CollectionReference examsCollection = db.collection("attendance");
        DocumentReference userEntryRef = examsCollection.document(currentDate).collection(userId).document(userId);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("planToday", binding.etPlanToday.getText().toString());
        updatedData.put("planTomorrow", binding.etPlanTomorrow.getText().toString());
        updatedData.put("planYesterday", binding.etPlanYesterday.getText().toString());
        updatedData.put("repeated", binding.etRepeated.getText().toString());
        updatedData.put("repeatedYesterday", binding.etRepeatedYesterday.getText().toString());
        updatedData.put("todayPercentage", binding.etTodayPercentage.getText().toString());
        updatedData.put("yesterdayPercentage", binding.etYesterdayPercentage.getText().toString());
        updatedData.put("notes", binding.notes.getText().toString());
        updatedData.put("rate", binding.fluidSlider.getText().toString());

        userEntryRef.set(updatedData)
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
