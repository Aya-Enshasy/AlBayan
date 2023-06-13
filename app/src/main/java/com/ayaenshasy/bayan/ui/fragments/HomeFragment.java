package com.ayaenshasy.bayan.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {

    FragmentHomeBinding binding;
    private StudentAdapter adapter;
    private DatabaseReference studentsRef;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Initialize Firebase database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        studentsRef = firebaseDatabase.getReference("students");

        setData();
        return view;
    }

    private void setData() {
        setMainUserData();
        setRvData();
    }

    private void setRvData() {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context));

            List<Student> students = new ArrayList<>();
            adapter = new StudentAdapter(students, context);
            binding.rvUser.setAdapter(adapter);

            // Retrieve teacher ID from Firebase
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("users");
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String teacherId = childSnapshot.getKey();  // Assuming teacher ID is the key of each child node
                        if (teacherId != null) {
                            // Query students based on teacher ID
                            DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
                            Query query = studentsRef.orderByChild("responsible_id").equalTo(teacherId);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Student> students = new ArrayList<>();
                                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                        Student student = studentSnapshot.getValue(Student.class);
                                        students.add(student);
                                    }
                                    adapter.setStudents(students);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
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
        } else {
            // Handle the case for non-teacher role if needed
        }

    }

    @SuppressLint("SetTextI18n")
    private void setMainUserData() {
        binding.userName.setText(currentUser.getName());
        binding.userRole.setText(role_name);
        binding.identifier.setText(currentUser.getName() + "");
        Glide.with(context).load(currentUser.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true).into(binding.userImage);
    }
}
