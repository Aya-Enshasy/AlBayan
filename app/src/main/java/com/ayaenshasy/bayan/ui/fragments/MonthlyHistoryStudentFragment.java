package com.ayaenshasy.bayan.ui.fragments;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.MonthHistoryAdapter;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.bayan.databinding.FragmentMonthlyHistoryStudentBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyHistoryStudentFragment extends BaseFragment {
    private DatabaseReference databaseReference;
    FragmentMonthlyHistoryStudentBinding binding;
    String startDateString, endDateString;
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

    private void getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // Move one month back
        Date lastMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day to the first day of the last month
        Date startOfLastMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Set the day to the last day of the last month
        Date endOfLastMonth = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        startDateString = dateFormat.format(startOfLastMonth);
        endDateString = dateFormat.format(endOfLastMonth);
    }

    private void getData() {
        ArrayList<Attendance> attendances = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        MonthHistoryAdapter adapter = new MonthHistoryAdapter(attendances, context);
        binding.recyclerView.setAdapter(adapter);
// Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

// Calculate the date 30 days ago
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date thirtyDaysAgo = calendar.getTime();

// Convert the dates to the desired format (e.g., "yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDateStr = dateFormat.format(currentDate);
        String thirtyDaysAgoStr = dateFormat.format(thirtyDaysAgo);

// Create a Firebase query to retrieve data within the date range and for users with ID "123"
        Query query = FirebaseDatabase.getInstance().getReference("attendance")
                .orderByKey()
                .startAt(thirtyDaysAgoStr)
                .endAt(currentDateStr);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Attendance> attendanceList = new ArrayList<>();

                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot entrySnapshot : dateSnapshot.getChildren()) {
                        String id = entrySnapshot.getKey();

                        // Check if the ID matches "123"
                        if (id.equals("123")) {
                            String date = dateSnapshot.getKey();  // Get the date of the data
                            String planToday = entrySnapshot.child("planToday").getValue(String.class);
                            String todayPercentage = entrySnapshot.child("todayPercentage").getValue(String.class);
                            String repeated = entrySnapshot.child("repeated").getValue(String.class);
                            String planYesterday = entrySnapshot.child("planYesterday").getValue(String.class);
                            String yesterdayPercentage = entrySnapshot.child("yesterdayPercentage").getValue(String.class);
                            String repeatedYesterday = entrySnapshot.child("repeatedYesterday").getValue(String.class);
                            String planTomorrow = entrySnapshot.child("planTomorrow").getValue(String.class);
                            Map<String, Boolean> islamicPrayers = entrySnapshot.child("islamicPrayers").getValue(new GenericTypeIndicator<Map<String, Boolean>>() {
                            });

                            Attendance attendance = new Attendance(id, date, planToday, todayPercentage, repeated, planYesterday, yesterdayPercentage, repeatedYesterday, islamicPrayers, planTomorrow);
                            attendanceList.add(attendance);
                        }
                    }
                }
                adapter.setExam(attendanceList);

                // Use the attendanceList for users with ID "123" as needed
                for (Attendance attendance : attendanceList) {
                    System.out.println(attendance.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

}
