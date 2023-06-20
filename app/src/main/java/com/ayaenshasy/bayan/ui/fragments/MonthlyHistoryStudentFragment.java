package com.ayaenshasy.bayan.ui.fragments;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ayaenshasy.bayan.model.Attendance;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.EventListener;
import com.google.gson.reflect.TypeToken;

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDateStr = dateFormat.format(currentDate);
        String thirtyDaysAgoStr = dateFormat.format(thirtyDaysAgo);

        // Create a Firestore query to retrieve data within the date range and for users with ID "123"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("attendance")
                .whereGreaterThanOrEqualTo("date", thirtyDaysAgoStr)
                .whereLessThanOrEqualTo("date", currentDateStr)
                .whereEqualTo("userId", requireActivity().getIntent().getStringExtra(USER_ID));

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    Log.e("Firestore", "Error getting data", error);
                    Toast.makeText(getActivity(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Attendance> attendanceList = new ArrayList<>();

                for (QueryDocumentSnapshot document : snapshot) {
                    String id = document.getId();
                    String date = document.getString("date");
                    String planToday = document.getString("planToday");
                    String todayPercentage = document.getString("todayPercentage");
                    String repeated = document.getString("repeated");
                    String planYesterday = document.getString("planYesterday");
                    String yesterdayPercentage = document.getString("yesterdayPercentage");
                    String repeatedYesterday = document.getString("repeatedYesterday");
                    String planTomorrow = document.getString("planTomorrow");
                    Map<String, Boolean> islamicPrayers = document.toObject(Attendance.class).getIslamicPrayers();

                    Attendance attendance = new Attendance(id, date, planToday, todayPercentage, repeated, planYesterday, yesterdayPercentage, repeatedYesterday, islamicPrayers, planTomorrow);
                    attendanceList.add(attendance);
                }

                adapter.setExam(attendanceList);

                // Use the attendanceList for the current user as needed
                for (Attendance attendance : attendanceList) {
                    Log.d("Firestore", attendance.toString());
                }
            }
        });    }

}
