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
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommittedStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommittedStudentsFragment extends BaseFragment {
    FragmentCommittedStudentsBinding binding;
    private StudentAdapter adapter;
    List<Student> students = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommittedStudentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommittedStudentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommittedStudentsFragment newInstance(String param1, String param2) {
        CommittedStudentsFragment fragment = new CommittedStudentsFragment();
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
        binding = FragmentCommittedStudentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setRvData();
        return view;
    }

    private void setRvData() {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                @Override
                public void sendData(Student student) {
//                    showBottomSheet(student);
                }
            });
            binding.rvUser.setAdapter(adapter);

            // Retrieve teacher ID from Firebase
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("users");
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String teacherId = childSnapshot.getKey();  // Assuming teacher ID is the key of each child node
                        if (teacherId != null) {
                            queryStudentsByTeacherId(teacherId);
                            break; // Break after finding the teacher ID
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors
                }
            });
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            Query query = usersRef.orderByChild("responsible_id").equalTo(currentUser.getId());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the user data
                        User user = userSnapshot.getValue(User.class);

                        // Do something with the user data
                        // For example, print the user's name
                        System.out.println(user.getName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that occur
                    System.out.println("Error: " + databaseError.getMessage());
                }
            });

        }
    }

    private void queryStudentsByTeacherId(String teacherId) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        Query query = studentsRef.orderByChild("responsible_id").equalTo(currentUser.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                students.clear(); // Clear the list before adding new data
                String currentDate = getCurrentDate(); // Replace with the method to get the current date

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    String studentId = student.getId();

                    DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance")
                            .child(currentDate)
                            .child(studentId);

                    attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                            boolean isAttendanceMarkedToday = attendanceSnapshot.exists();
                            if (isAttendanceMarkedToday){
                                student.setChecked(isAttendanceMarkedToday);
                                students.add(student);
                            }

                            // Notify the adapter when all students are processed
                            if (students.size() == dataSnapshot.getChildrenCount()) {
                                binding.progressBar4.setVisibility(View.GONE);
                                adapter.setStudents(students);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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