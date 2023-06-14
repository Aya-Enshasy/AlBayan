package com.ayaenshasy.bayan.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.databinding.FragmentCommittedStudentsBinding;
import com.ayaenshasy.bayan.databinding.FragmentNotCommittedStudentsBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.user.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotCommittedStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotCommittedStudentsFragment extends BaseFragment {
    FragmentNotCommittedStudentsBinding binding;
    private StudentAdapter adapter;
    List<Student> students = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotCommittedStudentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotCommittedStudentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotCommittedStudentsFragment newInstance(String param1, String param2) {
        NotCommittedStudentsFragment fragment = new NotCommittedStudentsFragment();
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
        binding = FragmentNotCommittedStudentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setRvData();
        return view;
    }

    private void setRvData() {
        binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        adapter = new StudentAdapter(students, context, new DataListener<Student>() {
            @Override
            public void sendData(Student student) {
//                showBottomSheet(student);
            }
        });
        binding.rvUser.setAdapter(adapter);

        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("users");
        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String teacherId = childSnapshot.getKey();  // Assuming teacher ID is the key of each child node
                    if (teacherId != null && teacherId.equals(currentUser.getId())) { // Replace 'userId' with the actual teacher ID
                        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
                        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                                List<Student> attendingStudents = new ArrayList<>(); // Create a local list to hold the attending students
                                String today = getCurrentDate(); // Assuming you have a method to get the current date in the format "yyyy-MM-dd"
                                DataSnapshot todaySnapshot = attendanceSnapshot.child(today);
                                if (todaySnapshot.exists()) {
                                    for (DataSnapshot studentSnapshot : todaySnapshot.getChildren()) {
                                        String studentId = studentSnapshot.getKey();
                                        if (studentId != null) {
                                            DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("students").child(studentId);
                                            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot studentDataSnapshot) {
                                                    Student student = studentDataSnapshot.getValue(Student.class);
                                                    if (student != null) {
                                                        attendingStudents.add(student);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    // Handle any errors
                                                }
                                            });
                                        }
                                    }
                                }
                                DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
                                studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<Student> allStudents = new ArrayList<>(); // Create a local list to hold all students
                                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                            Student student = studentSnapshot.getValue(Student.class);
                                            if (student != null) {
                                                allStudents.add(student);
                                            }
                                        }
                                        List<Student> notAttendingStudents = new ArrayList<>(allStudents);
                                        notAttendingStudents.removeAll(attendingStudents); // Remove attending students from the list
                                        adapter.setStudents(notAttendingStudents); // Set the list with not attending students
                                        binding.progressBar4.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle any errors
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle any errors
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }


    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}