package com.ayaenshasy.bayan.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.bayan.databinding.FragmentMonthlyHistoryStudentBinding;
import com.ayaenshasy.bayan.model.Attendance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyHistoryStudentFragment extends BaseFragment {
    private DatabaseReference databaseReference;
    FragmentMonthlyHistoryStudentBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MonthlyHistoryStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthlyHistoryStudentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyHistoryStudentFragment newInstance(String param1, String param2) {
        MonthlyHistoryStudentFragment fragment = new MonthlyHistoryStudentFragment();
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
        binding = FragmentMonthlyHistoryStudentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getMonth();
        getData();

        return view;
    }

    private void getMonth(){
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Add one month to the current date
        calendar.add(Calendar.MONTH, 1);
        Date nextDate = calendar.getTime();

        // Format the dates using SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);
        String nextDateString = dateFormat.format(nextDate);

        // Print the current date and the next date
        System.out.println("Current date: " + currentDateString);
        System.out.println("Next date after one month: " + nextDateString);
    }

    private void getData(){
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Add one month to the current date
        calendar.add(Calendar.MONTH, 1);
        Date nextDate = calendar.getTime();

        // Format the dates using SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = dateFormat.format(currentDate);
        String endDateString = dateFormat.format(nextDate);

        // Get the reference to the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("attendance");

        // Query the data between the start and end dates
        databaseReference.orderByChild(startDateString).startAt(startDateString).endAt(endDateString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve the Attendance object
                    Attendance attendance = snapshot.getValue(Attendance.class);

                    // Process the attendance data here
                    // Example: Log the date and islamicPrayers
                    Log.d("Attendance", "Date: " + attendance.getDate());
                    Log.d("Attendance", "Islamic Prayers: " + attendance.getIslamicPrayers());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }
    }
