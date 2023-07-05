package com.ayaenshasy.AlBayan.ui.fragments;

import static com.ayaenshasy.AlBayan.utils.Constant.USER_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.MonthHistoryAdapter;
import com.ayaenshasy.AlBayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.AlBayan.databinding.FragmentMonthlyHistoryStudentBinding;
import com.ayaenshasy.AlBayan.model.Attendance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyHistoryStudentFragment extends BaseFragment {
    private DatabaseReference databaseReference;
    FragmentMonthlyHistoryStudentBinding binding;
    String startDateString, endDateString;
    String student_id;

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
        student_id = getActivity().getIntent().getStringExtra(USER_ID);
        getMonth();
        fetchAttendanceRecordsForLast30Days();

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

    private void fetchAttendanceRecordsForLast30Days() {
        // Calculate the date for 30 days ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date thirtyDaysAgo = calendar.getTime();

        CollectionReference attendanceCollection = FirebaseFirestore.getInstance().collection("attendance");
        Query query = attendanceCollection
                .document(student_id)
                .collection("records")
                //.whereGreaterThan("currentDate", thirtyDaysAgo)
                .orderBy("currentDate", Query.Direction.DESCENDING);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> attendanceRecords = queryDocumentSnapshots.getDocuments();
                    // Process the attendance records here
                    List<Attendance> attendanceList = new ArrayList<>();

                    for (DocumentSnapshot document : attendanceRecords) {
                        Attendance attendance = document.toObject(Attendance.class);
                        attendance.setDate(document.getString("currentDate"));
                        attendanceList.add(attendance);


                    }
                    MonthHistoryAdapter adapter = new MonthHistoryAdapter(attendanceList , this.requireContext()) ;
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
                    binding.recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Toast.makeText(getContext(), "Failed to fetch attendance records: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}